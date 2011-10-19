/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.zimbra.common.account.Key;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.GalContact;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.gal.GalGroup;
import com.zimbra.cs.gal.GalGroupInfoProvider;
import com.zimbra.cs.gal.GalSearchControl;
import com.zimbra.cs.gal.GalSearchParams;
import com.zimbra.cs.gal.GalSearchResultCallback;
import com.zimbra.cs.index.ContactHit;
import com.zimbra.cs.index.ProxiedHit;
import com.zimbra.cs.index.SearchParams;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.soap.type.GalSearchType;
import com.zimbra.soap.ZimbraSoapContext;

public class ContactAutoComplete {
    public static final class AutoCompleteResult {
        public ContactRankings rankings;
        public final Collection<ContactEntry> entries;
        public boolean canBeCached;
        public final int limit;
        private final Set<String> keys;

        public AutoCompleteResult(int l) {
            entries = new TreeSet<ContactEntry>();
            keys = new HashSet<String>();
            canBeCached = true;
            limit = l;
        }

        public void addEntry(ContactEntry entry) {
            String key = entry.getKey();
            if (keys.contains(key)) {
                return;
            }
            if (entries.size() >= limit) {
                canBeCached = false;
                return;
            }
            if (entry.mRanking == 0) {
                // if the match comes from gal or folder search
                // check the ranking table for matching email
                // address
                int ranking = rankings.query(key);
                if (ranking > 0) {
                    entry.mRanking = ranking;
                }
            }
            entries.add(entry);
            keys.add(key);
        }

        public void appendEntries(AutoCompleteResult result) {
            for (ContactEntry entry : result.entries) {
                addEntry(entry);
            }
        }

        void clear() {
            entries.clear();
            keys.clear();
        }

    }

    public static final class ContactEntry implements Comparable<ContactEntry> {
        String mEmail;
        String mDisplayName;
        String mLastName;
        boolean mIsContactGroup;  // is contact group
        boolean mIsGroup;        // is GAL group or contact group
        boolean mCanExpandGroupMembers;
        ItemId mId;
        int mFolderId;
        int mRanking;
        long mLastAccessed;

        protected String getKey() {
            return (mIsContactGroup ? mDisplayName : mEmail).toLowerCase();
        }

        public String getEmail() {
            if (isContactGroup()) {
                return null;
            }
            StringBuilder buf = new StringBuilder();
            if (mDisplayName != null && mDisplayName.length() > 0) {
                buf.append("\"");
                buf.append(mDisplayName);
                buf.append("\" ");
            }
            buf.append("<").append(mEmail).append(">");
            return buf.toString();
        }

        public ItemId getId() {
            return mId;
        }

        public int getFolderId() {
            return mFolderId;
        }

        public int getRanking() {
            return mRanking;
        }

        public boolean isContactGroup() {
            return mIsContactGroup;
        }

        public boolean isGroup() {
            return mIsGroup;
        }

        public boolean canExpandGroupMembers() {
            return mCanExpandGroupMembers;
        }

        public String getDisplayName() {
            return mDisplayName;
        }

        void setIsGalGroup(String email, Map<String,? extends Object> attrs, Account authedAcct, boolean needCanExpand) {
            setIsGalGroup(email, (String)attrs.get(ContactConstants.A_zimbraId), authedAcct, needCanExpand);
        }

        void setIsGalGroup(String email, String zimbraId, Account authedAcct, boolean needCanExpand) {
            boolean canExpand = false;
            if (needCanExpand) {
                canExpand = GalSearchControl.canExpandGalGroup(email, zimbraId, authedAcct);
            }
            setIsGalGroup(canExpand);
        }

        void setIsGalGroup(boolean canExpand) {
            mIsGroup = true;
            mCanExpandGroupMembers = canExpand;
        }

        void setIsContactGroup() {
            mIsGroup = true;
            mIsContactGroup = true;
            mCanExpandGroupMembers = true;
        }

        void setName(String name) {
            if (name == null) {
                name = "";
            }
            mDisplayName = name;
            mLastName = "";
            int space = name.lastIndexOf(' ');
            if (space > 0) {
                mLastName = name.substring(space + 1);
            }
        }

        // ascending order
        @Override
        public int compareTo(ContactEntry that) {
            int nameCompare = this.getKey().compareToIgnoreCase(that.getKey());
            if (nameCompare == 0) {
                return 0;
            }
            // check the ranking
            int diff = that.mRanking - this.mRanking;
            if (diff != 0) {
                return diff;
            }
            // make ranked contacts more prominent, followed by
            // address book contacts then gal contacts.
            if (mFolderId == FOLDER_ID_GAL && that.mFolderId != FOLDER_ID_GAL || that.mFolderId == FOLDER_ID_UNKNOWN) {
                return 1;
            }
            if (mFolderId != FOLDER_ID_GAL && that.mFolderId == FOLDER_ID_GAL || mFolderId == FOLDER_ID_UNKNOWN) {
                return -1;
            }
            // alphabetical
            return nameCompare;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ContactEntry) {
                return compareTo((ContactEntry)obj) == 0;
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            toString(buf);
            return buf.toString();
        }

        public void toString(StringBuilder buf) {
            buf.append(mRanking).append(" ");
            if (isContactGroup()) {
                buf.append(getDisplayName()).append(" (contact group)");
            } else {
                buf.append(getEmail());
            }
            buf.append(" ").append(new Date(mLastAccessed));
        }
    }

    private static final Splitter TOKEN_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().trimResults();
    public static final int FOLDER_ID_GAL = 0;
    public static final int FOLDER_ID_UNKNOWN = -1;

    private boolean mIncludeGal;
    private boolean mNeedCanExpand; // whether the canExpand info is needed for GAL groups

    private static final Set<MailItem.Type> CONTACT_TYPES = EnumSet.of(MailItem.Type.CONTACT);

    private boolean mIncludeSharedFolders;
    private Collection<String> mEmailKeys;

    private GalSearchType mSearchType;
    private ZimbraSoapContext mZsc;
    private Account mAuthedAcct;
    private Account mRequestedAcct;
    private OperationContext octxt;

    private static final List<String> DEFAULT_EMAIL_KEYS = ImmutableList.of(
            ContactConstants.A_email, ContactConstants.A_email2, ContactConstants.A_email3);

    public ContactAutoComplete(Account acct, OperationContext octxt) {
        this(acct, null, octxt);
    }

    public ContactAutoComplete(Account acct, ZimbraSoapContext zsc, OperationContext octxt) {
        mZsc = zsc;
        this.octxt = octxt;
        try {
            mRequestedAcct = acct;
            mIncludeSharedFolders = mRequestedAcct.isPrefSharedAddrBookAutoCompleteEnabled();
            String contactEmailFields = mRequestedAcct.getContactEmailFields();
            if (contactEmailFields != null) {
                mEmailKeys = ImmutableList.copyOf(Splitter.on(',').split(contactEmailFields));
            }
            mIncludeGal = mRequestedAcct.isPrefGalAutoCompleteEnabled();

            if (mZsc != null) {
                String authedAcctId = mZsc.getAuthtokenAccountId();
                if (authedAcctId != null) {
                    mAuthedAcct = Provisioning.getInstance().get(Key.AccountBy.id, authedAcctId);
                }
            }
            if (mAuthedAcct == null) {
                mAuthedAcct = mRequestedAcct;
            }
        } catch (ServiceException e) {
            ZimbraLog.gal.warn("error initializing ContactAutoComplete", e);
        }
        if (mEmailKeys == null) {
            mEmailKeys = DEFAULT_EMAIL_KEYS;
        }
        mSearchType = GalSearchType.account;
    }

    private String getRequestedAcctId() {
        return mRequestedAcct.getId();
    }

    public Collection<String> getEmailKeys() {
        return mEmailKeys;
    }

    public boolean includeGal() {
        return mIncludeGal;
    }

    public void setIncludeGal(boolean includeGal) {
        mIncludeGal = includeGal;
    }

    public void setNeedCanExpand(boolean needCanExpand) {
        mNeedCanExpand = needCanExpand;
    }

    public void setSearchType(GalSearchType type) {
        mSearchType = type;
    }

    public AutoCompleteResult query(String str, Collection<Integer> folders, int limit) throws ServiceException {
        ZimbraLog.gal.debug("AutoComplete querying: %s", str);
        long t0 = System.currentTimeMillis();
        AutoCompleteResult result = new AutoCompleteResult(limit);
        result.rankings = new ContactRankings(getRequestedAcctId());
        if (limit <= 0) {
            return result;
        }

        if (result.entries.size() >= limit) {
            return result;
        }

        // query ranking table
        Collection<ContactEntry> rankingTableMatches = result.rankings.search(str);

        if (!rankingTableMatches.isEmpty()) {
            for (ContactEntry entry : rankingTableMatches) {
                String emailAddr = entry.getKey();
                resolveGroupInfo(entry, emailAddr);
                result.addEntry(entry);
            }
        }

        long t1 = System.currentTimeMillis();

        // search other folders
        if (result.entries.size() < limit) {
            queryFolders(str, folders, limit, result);
        }
        long t2 = System.currentTimeMillis();

        if (mIncludeGal && result.entries.size() < limit) {
            queryGal(str, result);
        }

        long t3 = System.currentTimeMillis();

        ZimbraLog.gal.info("autocomplete: overall=%dms, ranking=%dms, folder=%dms, gal=%dms",
                t3 - t0, t1 - t0, t2 - t1, t3 - t2);
        return result;
    }

    /**
     * ranking table and local contact matches don't have group indicator persisted on them,
     * cross-ref GAL to check if the address is a group.
     *
     * If the address is a group, set group info in the ContactEntry object.  Also, change the
     * folder ID to GAL.  Client relies on this to display the expand icon, otherwise it would
     * consider the entry a local contact group and will not offer to expand it.
     *
     * @param entry
     * @param email
     * @return true if the address is a group, false otherwise
     */
    private void resolveGroupInfo(ContactEntry entry, String email) {
        GalGroup.GroupInfo groupInfo = GalGroupInfoProvider.getInstance().getGroupInfo(email, mNeedCanExpand, mRequestedAcct, mAuthedAcct);
        if (groupInfo != null) {
            boolean canExpand = (GalGroup.GroupInfo.CAN_EXPAND == groupInfo);
            entry.setIsGalGroup(canExpand);

            // set folder ID to GAL, client relies on this to display the expand icon
            entry.mFolderId = FOLDER_ID_GAL;
        }
    }

    private void queryGal(String str, AutoCompleteResult result) {
        ZimbraLog.gal.debug("querying gal");
        GalSearchParams params = new GalSearchParams(mRequestedAcct, mZsc);
        params.setQuery(str);
        params.setType(mSearchType);
        params.setLimit(200);
        params.setNeedCanExpand(mNeedCanExpand);
        params.setResultCallback(new AutoCompleteCallback(str, result, params));
        try {
            try {
                GalSearchControl gal = new GalSearchControl(params);
                gal.autocomplete();
            } catch (ServiceException e) {
                if (ServiceException.PERM_DENIED.equals(e.getCode())) {
                    ZimbraLog.gal.debug("cannot autocomplete gal: %s", e.getMessage()); // do not log stack
                } else {
                    throw e;
                }
            }
        } catch (Exception e) {
            ZimbraLog.gal.warn("cannot autocomplete gal", e);
            return;
        }
    }

    private class AutoCompleteCallback extends GalSearchResultCallback {
        AutoCompleteResult result;
        String str;

        public AutoCompleteCallback(String str, AutoCompleteResult result, GalSearchParams params) {
            super(params);
            this.result = result;
            this.str = str;
        }

        public void handleContactAttrs(Map<String, ? extends Object> attrs) {
            addMatchedContacts(str, attrs, FOLDER_ID_GAL, null, result);
        }

        @Override
        public Element handleContact(Contact c) throws ServiceException {
            ZimbraLog.gal.debug("gal entry: %d", c.getId());
            handleContactAttrs(c.getFields());
            return null;
        }

        @Override
        public void visit(GalContact c) throws ServiceException {
            ZimbraLog.gal.debug("gal entry: %d", c.getId());
            handleContactAttrs(c.getAttrs());
        }

        @Override
        public void handleElement(Element e) throws ServiceException {
            ZimbraLog.gal.debug("gal entry: %s", e.getAttribute(MailConstants.A_ID));
            handleContactAttrs(parseContactElement(e));
        }

        @Override
        public void setSortBy(String sortBy) {
        }

        @Override
        public void setQueryOffset(int offset) {
        }

        @Override
        public void setHasMoreResult(boolean more) {
        }
    }

    private String getFieldAsString(Map<String,? extends Object> attrs, String fieldName) {
        Object value = attrs.get(fieldName);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof String[]) {
            String[] values = (String[]) value;
            if (values.length > 0) {
                return values[0];
            }
        }
        return null;
    }

    /**
     *  Add contact entry to result
     *  @see com.zimbra.cs.mailbox.OfflineGalContactAutoComplete
     */
    protected void addEntry(ContactEntry entry, AutoCompleteResult result) {
        result.addEntry(entry);
    }

    private boolean matchesEmail(List<String> tokens, String email) {
        if (!Strings.isNullOrEmpty(email) && tokens.size() == 1) {
            String token = tokens.get(0);
            return email.toLowerCase().startsWith(token);
        }
        // multi token query doesn't match any email addresses
        return false;
    }

    private boolean matchesName(List<String> tokens, String name) {
        if (tokens.isEmpty() || Strings.isNullOrEmpty(name)) {
            return false;
        } else if (tokens.size() == 1) { // single token
            return name.toLowerCase().startsWith(tokens.get(0));
        } else {
            return toPattern(tokens).matcher(name).matches();
        }
    }

    private boolean matchesName(List<String> tokens, Map<String, ? extends Object> attrs) {
        if (tokens.isEmpty()) {
            return false;
        } else if (tokens.size() == 1) { // single token
            String token = tokens.get(0);
            String firstName = getFieldAsString(attrs, ContactConstants.A_firstName);
            if (!Strings.isNullOrEmpty(firstName) && firstName.toLowerCase().startsWith(token)) {
                return true;
            }
            String lastName = getFieldAsString(attrs, ContactConstants.A_lastName);
            if (!Strings.isNullOrEmpty(lastName) && lastName.toLowerCase().startsWith(token)) {
                return true;
            }
            String middleName = getFieldAsString(attrs, ContactConstants.A_middleName);
            if (!Strings.isNullOrEmpty(middleName) && middleName.toLowerCase().startsWith(token)) {
                return true;
            }
            String fullName = getFieldAsString(attrs, ContactConstants.A_fullName);
            if (!Strings.isNullOrEmpty(fullName) && fullName.toLowerCase().startsWith(token)) {
                return true;
            }
            String nickname = getFieldAsString(attrs, ContactConstants.A_nickname);
            if (!Strings.isNullOrEmpty(nickname) && nickname.toLowerCase().startsWith(token)) {
                return true;
            }
            // check lastname first as it's lastname firstname order in Japanese
            String phoneticLastName = getFieldAsString(attrs, ContactConstants.A_phoneticLastName);
            if (!Strings.isNullOrEmpty(phoneticLastName) && phoneticLastName.toLowerCase().startsWith(token)) {
                return true;
            }
            String phoneticFirstName = getFieldAsString(attrs, ContactConstants.A_phoneticFirstName);
            if (!Strings.isNullOrEmpty(phoneticFirstName) && phoneticFirstName.toLowerCase().startsWith(token)) {
                return true;
            }
            return false;
        } else { // multi tokens
            Pattern pattern = toPattern(tokens);

            String firstName = getFieldAsString(attrs, ContactConstants.A_firstName);
            String lastName = getFieldAsString(attrs, ContactConstants.A_lastName);
            String middleName = getFieldAsString(attrs, ContactConstants.A_middleName);
            // first middle last pattern
            if (pattern.matcher(Joiner.on(' ').skipNulls().join(firstName, middleName, lastName)).matches()) {
                return true;
            }
            // last first middle pattern
            if (pattern.matcher(Joiner.on(' ').skipNulls().join(lastName, firstName, middleName)).matches()) {
                return true;
            }

            String fullName = getFieldAsString(attrs, ContactConstants.A_fullName);
            if (!Strings.isNullOrEmpty(fullName) && pattern.matcher(fullName).matches()) {
                return true;
            }

            String nickname = getFieldAsString(attrs, ContactConstants.A_nickname);
            if (!Strings.isNullOrEmpty(nickname) && pattern.matcher(nickname).matches()) {
                return true;
            }

            String phoneticFirstName = getFieldAsString(attrs, ContactConstants.A_phoneticFirstName);
            String phoneticLastName = getFieldAsString(attrs, ContactConstants.A_phoneticLastName);
            // phonetic-last phonetic-first pattern (check this first as it's more common in Japanese)
            if (pattern.matcher(Joiner.on(' ').skipNulls().join(phoneticLastName, phoneticFirstName)).matches()) {
                return true;
            }
            // phonetic-first phonetic-last pattern (check this next as it's less common in Japanese)
            if (pattern.matcher(Joiner.on(' ').skipNulls().join(phoneticFirstName, phoneticLastName)).matches()) {
                return true;
            }
            return false;
        }
    }

    private Pattern toPattern(List<String> tokens) {
        StringBuilder regex = new StringBuilder();
        for (String token : tokens) {
            regex.append(regex.length() == 0 ? "(^|\\s)" : "\\s").append(Pattern.quote(token)).append(".*");
        }
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public void addMatchedContacts(String query, Map<String, ? extends Object> attrs, int folderId, ItemId id,
            AutoCompleteResult result) {
        if (!result.canBeCached) {
            return;
        }

        List<String> tokens = Lists.newArrayListWithExpectedSize(1);
        for (String token : Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().trimResults().split(query)) {
            tokens.add(token.toLowerCase());
        }

        if (attrs.get(ContactConstants.A_groupMember) == null) { // NOT a contact group.
            boolean nameMatches = matchesName(tokens, attrs);

            // matching algorithm is slightly different between matching
            // personal Contacts in the addressbook vs GAL entry if there is
            // multiple email address associated to the entry.  multiple
            // email address in Contact typically means alternative email
            // address, such as work email, home email, etc.  however in GAL,
            // multiple email address indicates an alias to the same contact
            // object.  for Contacts we want to show all the email addresses
            // available for the Contact entry.  but for GAL we need to show
            // just one email address.

            String fullName = getFieldAsString(attrs, ContactConstants.A_fullName);
            if (Strings.isNullOrEmpty(fullName)) {
                String first = getFieldAsString(attrs, ContactConstants.A_firstName);
                String middle = getFieldAsString(attrs, ContactConstants.A_middleName);
                String last = getFieldAsString(attrs, ContactConstants.A_lastName);
                fullName = Joiner.on(' ').skipNulls().join(first, middle, last);
            }

            for (String emailKey : mEmailKeys) {
                String email = getFieldAsString(attrs, emailKey);
                if (email != null && (nameMatches || matchesEmail(tokens, email))) {
                    ContactEntry entry = new ContactEntry();
                    entry.mEmail = email;
                    entry.setName(fullName);
                    entry.mId = id;
                    entry.mFolderId = folderId;
                    if (Contact.isGroup(attrs)) {
                        entry.setIsGalGroup(email, attrs, mAuthedAcct, mNeedCanExpand);
                    } else if (entry.mFolderId != FOLDER_ID_GAL) {
                        // is a local contact
                        // bug 55673, check if the addr is a group
                        resolveGroupInfo(entry, email);
                    }
                    addEntry(entry, result);
                    ZimbraLog.gal.debug("adding %s", entry.getEmail());
                    if (folderId == FOLDER_ID_GAL) {
                        // we've matched the first email address for this
                        // GAL contact.  move onto the next contact.
                        return;
                    }
                }
            }
        } else { // IS a local contact group
            String nickname = getFieldAsString(attrs, ContactConstants.A_nickname);
            if (matchesName(tokens, nickname)) {
                ContactEntry entry = new ContactEntry();
                entry.mDisplayName = nickname;
                entry.mId = id;
                entry.mFolderId = folderId;
                entry.setIsContactGroup();
                result.addEntry(entry);
                ZimbraLog.gal.debug("adding %s", entry.getKey());
            }
        }
    }

    private void queryFolders(String str, Collection<Integer> folderIDs, int limit, AutoCompleteResult result) throws ServiceException {
        str = str.toLowerCase();
        ZimbraQueryResults qres = null;
        try {
            Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(getRequestedAcctId());
            List<Folder> folders = new ArrayList<Folder>();
            Map<ItemId, Mountpoint> mountpoints = new HashMap<ItemId, Mountpoint>();
            if (folderIDs == null) {
                for (Folder folder : mbox.getFolderList(octxt, SortBy.NONE)) {
                    if (folder.getDefaultView() != MailItem.Type.CONTACT || folder.inTrash()) {
                        continue;
                    } else if (folder instanceof Mountpoint) {
                        Mountpoint mp = (Mountpoint) folder;
                        mountpoints.put(mp.getTarget(), mp);
                        if (mIncludeSharedFolders) {
                            folders.add(folder);
                        }
                    } else {
                        folders.add(folder);
                    }
                }
            } else {
                for (int fid : folderIDs) {
                    Folder folder = mbox.getFolderById(octxt, fid);
                    folders.add(folder);
                    if (folder instanceof Mountpoint) {
                        Mountpoint mp = (Mountpoint) folder;
                        mountpoints.put(mp.getTarget(), mp);
                    }
                }
            }
            SearchParams params = new SearchParams();
            params.setQueryString(generateQuery(str, folders));
            params.setDefaultField("contact:");
            params.setTypes(CONTACT_TYPES);
            params.setSortBy(SortBy.NONE);
            params.setLimit(limit + 1);
            params.setPrefetch(true);
            params.setFetchMode(SearchParams.Fetch.NORMAL);
            ZimbraLog.gal.debug("querying contact folders: %s", params.getQueryString());
            qres = mbox.index.search(SoapProtocol.Soap12, octxt, params);
            while (qres.hasNext()) {
                ZimbraHit hit = qres.getNext();
                Map<String,String> fields = null;
                ItemId id = null;
                int fid = 0;
                if (hit instanceof ContactHit) {
                    Contact c = ((ContactHit) hit).getContact();
                    ZimbraLog.gal.debug("hit: %d", c.getId());
                    fields = c.getFields();
                    id = new ItemId(c);
                    fid = c.getFolderId();
                } else if (hit instanceof ProxiedHit) {
                    fields = new HashMap<String, String>();
                    Element top = ((ProxiedHit) hit).getElement();
                    id = new ItemId(top.getAttribute(MailConstants.A_ID), (String) null);
                    ZimbraLog.gal.debug("hit: %s", id);
                    ItemId fiid = new ItemId(top.getAttribute(MailConstants.A_FOLDER), (String) null);
                    Mountpoint mp = mountpoints.get(fiid);
                    if (mp != null) {
                        // if the hit came from a descendant folder of
                        // the mountpoint, we don't have a peer folder ID.
                        fid = mp.getId();
                    }
                    for (Element elt : top.listElements(MailConstants.E_ATTRIBUTE)) {
                        try {
                            String name = elt.getAttribute(MailConstants.A_ATTRIBUTE_NAME);
                            fields.put(name, elt.getText());
                        } catch (ServiceException se) {
                            ZimbraLog.gal.warn("error handling proxied query result " + hit);
                        }
                    }
                } else {
                    continue;
                }

                addMatchedContacts(str, fields, fid, id, result);
                if (!result.canBeCached) {
                    return;
                }
            }
        } finally {
            Closeables.closeQuietly(qres);
        }
    }

    private String generateQuery(String query, Collection<Folder> folders) {
        StringBuilder buf = new StringBuilder("(");
        boolean first = true;
        for (Folder folder : folders) {
            int fid = folder.getId();
            if (fid < 1) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                buf.append(" OR ");
            }
            // include descendant folders if mountpoint
            buf.append(folder instanceof Mountpoint ? "underid:" : "inid:");
            buf.append(fid);
        }
        buf.append(')');
        for (String token : TOKEN_SPLITTER.split(query)) {
            buf.append(" \"").append(token.replace("\"", "\\\"")).append('"');
        }
        return buf.toString();
    }
}
