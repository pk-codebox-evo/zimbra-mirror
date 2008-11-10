/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.cs.offline.ab.SyncState;
import com.zimbra.cs.offline.ab.LocalData.ChangeType;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.client.http.HttpGDataRequest;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.io.IOException;

public class SyncSession {
    private final DataSource ds;
    private final LocalData localData;
    private final GabService service;

    private static class Stats {
        int added, updated, deleted;

        public String toString() {
            return String.format(
                "%d added, %d updated, and %d deleted", added, updated, deleted);
        }
    }

    private static final Log LOG = OfflineLog.gab;

    private static final boolean DEBUG_TRACE = true;
    private static final boolean HTTP_DEBUG = false;

    static {
        if (HTTP_DEBUG) {
            Logger httpLogger = Logger.getLogger(HttpGDataRequest.class.getName());
            httpLogger.setLevel(Level.ALL);
            //Logger xmlLogger = Logger.getLogger(XmlParser.class.getName());
            // Create a log handler which prints all log events to the console.
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            httpLogger.addHandler(handler);
            //xmlLogger.addHandler(handler);
        }
    }

    public SyncSession(DataSource ds) throws ServiceException {
        this.ds = ds;
        localData = new LocalData((OfflineDataSource) ds);
        service = new GabService(ds.getUsername(), ds.getDecryptedPassword());
    }

    public void sync() throws ServiceException {
        try {
            syncContacts();
        } catch (Exception e) {
            throw ServiceException.FAILURE("Google contact sync error", e);
        }
    }

    private void syncContacts() throws IOException, ServiceException {
        Mailbox mbox = localData.getMailbox();
        SyncState state = localData.loadState();
        // Get remote changes since last sync
        DateTime currentTime = DateTime.now();
        String rev = state.getLastRevision();
        DateTime lastSyncTime = rev != null ? DateTime.parseDateTime(rev) : null;
        ContactFeed contacts = service.getContacts(lastSyncTime, currentTime);
        ContactGroupFeed groups = service.getGroups(lastSyncTime, currentTime);
        state.setLastRevision(currentTime.toString());
        List<SyncRequest> groupRequests;
        List<SyncRequest> contactRequests;
        int seq = state.getLastModSequence();
        synchronized (mbox) {
            // Get local changes sync last sync
            Map<Integer, ChangeType> groupChanges = localData.getGroupChanges(seq);
            Map<Integer, ChangeType> contactChanges = localData.getContactChanges(seq);
            // Process remote group and contact changes
            processGroupChanges(groups.getEntries(), groupChanges);
            processContactChanges(contacts.getEntries(), contactChanges);
            // Process local changes and determine changes to push
            groupRequests = processGroupChanges(groupChanges);
            contactRequests = processContactChanges(contactChanges);
            state.setLastModSequence(mbox.getLastChangeID());
        }
        // Push local changes to remote
        pushGroupChanges(groupRequests);
        pushContactChanges(contactRequests);
        int errors = groupRequests.size() + contactRequests.size();
        if (errors > 0) {
            LOG.debug("Contact sync had %d errors", errors);
        }
        localData.saveState(state);
    }

    private void processGroupChanges(List<ContactGroupEntry> entries,
                                     Map<Integer, ChangeType> changes)
        throws ServiceException, IOException {
        LOG.debug("Processing %d remote contact group changes", entries.size());
        Stats stats = new Stats();
        for (ContactGroupEntry entry : entries) {
            DataSourceItem dsi = localData.getReverseMapping(entry.getId());
            try {
                processGroupChange(entry, dsi, changes, stats);
            } catch (ServiceException e) {
                localData.syncContactFailed(e, dsi.itemId, service.pp(entry));
            }
        }
        LOG.debug("Processed remote contact group changes: " + stats);
    }

    private void processGroupChange(ContactGroupEntry entry,
                                    DataSourceItem dsi,
                                    Map<Integer, ChangeType> changes,
                                    Stats stats) throws ServiceException {
        if (isTraceEnabled()) {
            LOG.debug("Processing remote group entry:\n%s", service.pp(entry));
        }
        int itemId = dsi.itemId;
        boolean deleted = entry.getDeleted() != null;
        if (itemId > 0) {
            // Contact group updated or deleted
            if (deleted) {
                // Don't delete tag since we can't be sure that it is not
                // also used by other non-contact items
                localData.deleteMapping(itemId);
                changes.remove(itemId);
                stats.deleted++;
            } else if (!changes.containsKey(itemId)) {
                String newName = getName(entry);
                String oldName = getName(getEntry(dsi, ContactGroupEntry.class));
                if (!newName.equals(oldName)) {
                    // Contact group was renamed...
                    localData.renameTag(itemId, newName);
                    stats.updated++;
                }
                updateEntry(itemId, entry);
            }
        } else if (!deleted) {
            // Contact group was added
            Tag tag = localData.createTag(getName(entry));
            updateEntry(tag.getId(), entry);
            stats.added++;
        }
    }

    private static String getName(BaseEntry entry) {
        return entry.getTitle().getPlainText();
    }
    
    private void processContactChanges(List<ContactEntry> entries,
                                       Map<Integer, ChangeType> changes)
        throws ServiceException, IOException {
        LOG.debug("Processing %d remote contact changes", entries.size());
        Stats stats = new Stats();
        for (ContactEntry entry : entries) {
            DataSourceItem dsi = localData.getReverseMapping(entry.getId());
            try {
                processContactChange(entry, dsi, changes, stats);
            } catch (ServiceException e) {
                localData.syncContactFailed(e, dsi.itemId, service.pp(entry));
            }
        }
        LOG.debug("Processed remote contact changes: " + stats);
    }

    private void processContactChange(ContactEntry entry,
                                      DataSourceItem dsi,
                                      Map<Integer, ChangeType> changes,
                                      Stats stats) throws ServiceException {
        if (isTraceEnabled()) {
            LOG.debug("Processing remote contact entry:\n%s", service.pp(entry));
        }
        int itemId = dsi.itemId;
        boolean deleted = entry.getDeleted() != null;
        if (itemId > 0) {
            // Contact updated or deleted
            if (deleted) {
                localData.deleteContact(itemId);
                localData.deleteMapping(itemId);
                changes.remove(itemId);
                stats.deleted++;
            } else if (!changes.containsKey(itemId)) {
                // Remote contact was updated with no local change
                String url = getEditUrl(getEntry(dsi, ContactEntry.class));
                if (!getEditUrl(entry).equals(url)) {
                    // Only update local entry if edit url has changed
                    // (avoids modifying contacts which we just pushed)
                    ContactData cd = new ContactData(entry);
                    localData.modifyContact(
                        itemId, cd.getParsedContact(), getTagBitmask(entry));
                    updateEntry(itemId, entry);
                    stats.updated++;
                }
            }
        } else if (!deleted) {
            // New contact added
            ContactData cd = new ContactData(entry);
            Contact contact = localData.createContact(
                cd.getParsedContact(), getTagBitmask(entry));
            updateEntry(contact.getId(), entry);
            stats.added++;
        }
    }

    private static String getEditUrl(BaseEntry entry) {
        return entry.getEditLink().getHref();
    }

    private List<SyncRequest> processGroupChanges(Map<Integer, ChangeType> changes)
        throws ServiceException {
        List<SyncRequest> reqs = new ArrayList<SyncRequest>();
        for (Map.Entry<Integer, ChangeType> entry : changes.entrySet()) {
            reqs.add(getGroupSyncRequest(entry.getKey(), entry.getValue()));
        }
        return reqs;
    }

    private List<SyncRequest> processContactChanges(Map<Integer, ChangeType> changes)
        throws ServiceException {
        List<SyncRequest> reqs = new ArrayList<SyncRequest>();
        for (Map.Entry<Integer, ChangeType> entry : changes.entrySet()) {
            reqs.add(getContactSyncRequest(entry.getKey(), entry.getValue()));
        }
        return reqs;
    }
    
    private SyncRequest getContactSyncRequest(int itemId, ChangeType type)
        throws ServiceException {
        ContactEntry entry;
        // For ADD and UPDATE, group membership info will be set later after
        // we've pushed contact group changes. Otherwise, we may not yet
        // have the ids for newly added groups.
        switch (type) {
        case ADD:
            entry = getContactData(itemId).newContactEntry();
            return SyncRequest.insert(this, itemId, entry);
        case UPDATE:
            entry = getEntry(itemId, ContactEntry.class);
            getContactData(itemId).updateContactEntry(entry);
            return SyncRequest.update(this, itemId, entry);
        case DELETE:
            entry = getEntry(itemId, ContactEntry.class);
            return SyncRequest.delete(this, itemId, entry);
        default:
            throw new AssertionError();
        }
    }

    private SyncRequest getGroupSyncRequest(int itemId, ChangeType type)
        throws ServiceException {
        ContactGroupEntry entry;
        switch (type) {
        case ADD:
            entry = new ContactGroupEntry();
            entry.setTitle(new PlainTextConstruct(localData.getTag(itemId).getName()));
            return SyncRequest.insert(this, itemId, entry);
        case UPDATE:
            entry = getEntry(itemId, ContactGroupEntry.class);
            entry.setTitle(new PlainTextConstruct(localData.getTag(itemId).getName()));
            return SyncRequest.update(this, itemId, entry);
        case DELETE:
            entry = getEntry(itemId, ContactGroupEntry.class);
            return SyncRequest.delete(this, itemId, entry);
        default:
            throw new AssertionError();
        }
    }

    private void pushGroupChanges(List<SyncRequest> reqs)
        throws ServiceException, IOException {
        LOG.debug("Pushing contact group changes");
        Stats stats = new Stats();
        Iterator<SyncRequest> it = reqs.iterator();
        while (it.hasNext()) {
            SyncRequest req = it.next();
            if (pushChange(req, stats)) {
                it.remove();
            }
        }
        LOG.debug("Pushed contact group changes: ", stats);
    }
    
    private void pushContactChanges(List<SyncRequest> reqs)
        throws ServiceException, IOException {
        Stats stats = new Stats();
        Iterator<SyncRequest> it = reqs.iterator();
        while (it.hasNext()) {
            SyncRequest req = it.next();
            switch (req.getType()) {
            case INSERT: case UPDATE:
                setGroupInfo((ContactEntry) req.getEntry(), req.getItemId());
            }
            if (pushChange(req, stats)) {
                it.remove();
            }
        }
        LOG.debug("Contact changes pushed: " + stats);
    }
    
    private void setGroupInfo(ContactEntry entry, int itemId)
        throws ServiceException {
        List<GroupMembershipInfo> groups = entry.getGroupMembershipInfos();
        groups.clear();
        for (Tag tag : localData.getContact(itemId).getTagList()) {
            ContactGroupEntry ge = getEntry(tag.getId(), ContactGroupEntry.class);
            if (ge != null) {
                groups.add(new GroupMembershipInfo(false, ge.getId()));
            }
        }
    }

    private boolean pushChange(SyncRequest req, Stats stats)
        throws ServiceException, IOException {
        int itemId = req.getItemId();
        try {
            req.execute();
            switch (req.getType()) {
            case DELETE:
                localData.deleteMapping(itemId);
                stats.deleted++;
                break;
            case UPDATE:
                updateEntry(itemId, req.getEntry());
                stats.updated++;
                break;
            case INSERT:
                updateEntry(itemId, req.getEntry());
                stats.added++;
                break;
            }
            return true;
        } catch (ServiceException e) {
            localData.syncContactFailed(e, itemId, service.pp(req.getEntry()));
            return false;
        }
    }

    private ContactData getContactData(int itemId) throws ServiceException {
        return new ContactData(localData.getContact(itemId));
    }

    private <T extends BaseEntry> T getEntry(int itemId, Class<T> entryClass)
        throws ServiceException {
        return getEntry(localData.getMapping(itemId), entryClass);
    }

    private <T extends BaseEntry> T getEntry(DataSourceItem dsi, Class<T> entryClass)
        throws ServiceException {
        // LOG.debug("Loading contact data for item id = %d", dsi.itemId);
        String xml = localData.getEntry(dsi);
        return xml != null ? service.parseEntry(xml, entryClass) : null;
    }

    private void updateEntry(int itemId, BaseEntry entry) throws ServiceException {
        localData.updateMapping(itemId, entry.getId(), service.toXml(entry));
    }

    private long getTagBitmask(ContactEntry entry) throws ServiceException {
        long mask = 0;
        for (GroupMembershipInfo info : entry.getGroupMembershipInfos()) {
            Boolean deleted = info.getDeleted();
            if (deleted == null || !deleted) {
                String id = info.getHref();
                DataSourceItem dsi = localData.getReverseMapping(id);
                if (dsi.itemId == -1) {
                    throw ServiceException.FAILURE(
                        "Missing item id for contact group: " + id, null);
                }
                mask |= localData.getTag(dsi.itemId).getBitmask();
            }
        }
        return mask;
    }

    public boolean isTraceEnabled() {
        return LOG.isDebugEnabled() && (DEBUG_TRACE || ds.isDebugTraceEnabled());
    }

    public GabService getGabService() { return service; }
}
