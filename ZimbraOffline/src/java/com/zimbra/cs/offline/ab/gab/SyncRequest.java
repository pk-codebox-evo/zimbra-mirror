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

import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.util.VersionConflictException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;

import java.io.IOException;

public class SyncRequest {
    private final SyncSession session;
    private final GabService service;
    private final RequestType type;
    private final int itemId;
    private BaseEntry entry;
    private com.google.gdata.util.ServiceException error;

    private static final Log LOG = OfflineLog.gab;

    private static final int MAX_COUNT = 3;

    public static SyncRequest insert(SyncSession session, int itemId, BaseEntry entry) {
        return new SyncRequest(session, RequestType.INSERT, itemId, entry);
    }

    public static SyncRequest update(SyncSession session, int itemId, BaseEntry entry) {
        return new SyncRequest(session, RequestType.UPDATE, itemId, entry);
    }

    public static SyncRequest delete(SyncSession session, int itemId, BaseEntry entry) {
        return new SyncRequest(session, RequestType.DELETE, itemId, entry);
    }
    
    private SyncRequest(SyncSession session, RequestType type, int itemId, BaseEntry entry) {
        this.session = session;
        this.service = session.getGabService();
        this.type = type;
        this.itemId = itemId;
        this.entry = entry;
    }

    public RequestType getType() { return type; }
    public int getItemId() { return itemId; }
    public BaseEntry getEntry() { return entry; }

    public boolean isInsert() { return type == RequestType.INSERT; }
    public boolean isUpdate() { return type == RequestType.UPDATE; }
    public boolean isDelete() { return type == RequestType.DELETE; }
    
    public void setEntry(BaseEntry entry) {
        this.entry = entry;
    }
    
    public boolean isGroup() {
        return entry != null && entry.getClass() == ContactGroupEntry.class;
    }

    private boolean isVersionConflict() {
        return error != null && error instanceof VersionConflictException;
    }

    public void execute() throws ServiceException, IOException {
        if (isUpdate()) {
            int count = 0;
            while (count++ < MAX_COUNT && !doExecute() && isVersionConflict()) {
                LOG.debug("Retrying UPDATE request for itemId %d (count = %d)",
                          itemId, count);
                VersionConflictException vce = (VersionConflictException) error;
                entry = service.getCurrentEntry(vce, entry.getClass());
            }
        } else {
            doExecute();
        }
        if (error != null) {
            LOG.debug("%s request failed for item id %d: %s", type, itemId,
                      error.getMessage());
            throw ServiceException.FAILURE(type + " request failed", error);
        }
    }
    
    private boolean doExecute() throws IOException {
        if (session.isTraceEnabled()) {
            LOG.debug("Executing %s request for item id %d:\n%s", type, itemId,
                      service.pp(entry));
        }
        error = null;
        try {
            if (isInsert()) {
                entry = service.insert(entry);
            } else if (isUpdate()) {
                entry = service.update(entry);
            } else if (isDelete()) {
                service.delete(entry);
            }
        } catch (com.google.gdata.util.ServiceException e) {
            error = e;
            return false;
        }
        return true;
    }
}
