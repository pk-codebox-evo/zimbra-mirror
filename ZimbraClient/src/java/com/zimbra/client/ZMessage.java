/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.zimbra.client.event.ZModifyEvent;
import com.zimbra.client.event.ZModifyMessageEvent;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.zclient.ZClientException;

public class ZMessage implements ZItem, ToZJSONObject {

    private String mId;
    private String mFlags;
    private String mSubject;
    private String mFragment;
    private String mTags;
    private String mFolderId;
    private String mConversationId;
    private String mPartName;
    private long mReceivedDate;
    private long mSentDate;
    private String mMessageIdHeader;
    private List<ZEmailAddress> mAddresses;
    private ZMimePart mMimeStructure;
    private String mContent;
    private String mContentURL;
    private long mSize;
    private String mReplyType;
    private String mInReplyTo;
    private String mOrigId;
    private ZInvite mInvite;
    private ZShare mShare;
    private ZMailbox mMailbox;
    private Map<String, String> mReqHdrs;
    private String mIdentityId;

    public ZMessage(Element e, ZMailbox mailbox) throws ServiceException {
        mMailbox = mailbox;
        mId = e.getAttribute(MailConstants.A_ID);
        mFlags = e.getAttribute(MailConstants.A_FLAGS, null);
        mTags = e.getAttribute(MailConstants.A_TAGS, null);
        mReplyType = e.getAttribute(MailConstants.A_REPLY_TYPE, null);
        mOrigId = e.getAttribute(MailConstants.A_ORIG_ID, null);
        mSubject = e.getAttribute(MailConstants.E_SUBJECT, null);
        mFragment = e.getAttribute(MailConstants.E_FRAG, null);
        mMessageIdHeader = e.getAttribute(MailConstants.E_MSG_ID_HDR, null);
        mInReplyTo = e.getAttribute(MailConstants.E_IN_REPLY_TO, null);

        mReceivedDate = e.getAttributeLong(MailConstants.A_DATE, 0);
        mSentDate = e.getAttributeLong(MailConstants.A_SENT_DATE, 0);
        mFolderId = e.getAttribute(MailConstants.A_FOLDER, null);
        mConversationId = e.getAttribute(MailConstants.A_CONV_ID, null);
        mPartName = e.getAttribute(MailConstants.A_PART, null);
        mSize = e.getAttributeLong(MailConstants.A_SIZE, -1);
        mIdentityId = e.getAttribute(MailConstants.A_IDENTITY_ID, null);

        Element content = e.getOptionalElement(MailConstants.E_CONTENT);
        if (content != null) {
            mContent = content.getText();
            mContentURL = content.getAttribute(MailConstants.A_URL, null);
        }

        mAddresses = new ArrayList<ZEmailAddress>();
        for (Element emailEl: e.listElements(MailConstants.E_EMAIL)) {
            mAddresses.add(new ZEmailAddress(emailEl));
        }

        //request headers
        mReqHdrs = new HashMap<String,String>();
        Element attrsEl = e.getOptionalElement("_attrs");
        if(attrsEl != null) {
            for (Element.Attribute eHdr : attrsEl.listAttributes()) {
                mReqHdrs.put(eHdr.getKey(),eHdr.getValue());
            }
        }

        Element mp = e.getOptionalElement(MailConstants.E_MIMEPART);
        if (mp != null)
            mMimeStructure = new ZMimePart(null, mp);

        Element inviteEl = e.getOptionalElement(MailConstants.E_INVITE);
        if (inviteEl != null)
            mInvite = new ZInvite(inviteEl);

        Element shrEl = e.getOptionalElement("shr");
        if (shrEl != null) {
            String shareContent = shrEl.getAttribute(MailConstants.E_CONTENT);
            if (shareContent != null) {
                mShare = ZShare.parseXml(shareContent);
            }
        }
    }

    @Override
    public void modifyNotification(ZModifyEvent event) throws ServiceException {
    	if (event instanceof ZModifyMessageEvent) {
    		ZModifyMessageEvent mevent = (ZModifyMessageEvent) event;
            if (mevent.getId().equals(mId)) {
                mFlags = mevent.getFlags(mFlags);
                mTags = mevent.getTagIds(mTags);
                mFolderId = mevent.getFolderId(mFolderId);
                mConversationId = mevent.getConversationId(mConversationId);
            }
        }
    }

    public ZMailbox getMailbox() {
        return mMailbox;
    }

    public  ZShare getShare() {
        return mShare;
    }

    /**
     *
     * @return invite object if this message contains an invite, null otherwise.
     */
    public ZInvite getInvite() {
        return mInvite;
    }

    /**
     *
     * @return Zimbra id of message we are replying to if this is a draft.
     */
    public String getOriginalId() {
        return mOrigId;
    }

    /**
     *
     * @return message-id header of message we are replying to if this is a draft
     */
    public String getInReplyTo() {
        return mInReplyTo;
    }

    /**
     *
     * @return reply type if this is a draft
     */
    public String getReplyType() {
        return mReplyType;
    }

    public long getSize() {
        return mSize;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getUuid() {
        return null;
    }

    public boolean hasFlags() {
        return mFlags != null && mFlags.length() > 0;
    }

    public boolean hasTags() {
        return mTags != null && mTags.length() > 0;
    }

    @Override
    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject zjo = new ZJSONObject();
        zjo.put("id", mId);
        zjo.put("flags", mFlags);
        zjo.put("tagIds", mTags);
        zjo.put("inReplyTo", mInReplyTo);
        zjo.put("originalId", mOrigId);
        zjo.put("subject", mSubject);
        zjo.put("fragment", mFragment);
        zjo.put("partName", mPartName);
        zjo.put("messageIdHeader", mMessageIdHeader);
        zjo.put("receivedDate", mReceivedDate);
        zjo.put("sentDate", mSentDate);
        zjo.put("folderId", mFolderId);
        zjo.put("conversationId", mConversationId);
        zjo.put("size", mSize);
        zjo.put("content", mContent);
        zjo.put("contentURL", mContentURL);
        zjo.put("addresses", mAddresses);
        zjo.put("mimeStructure", mMimeStructure);
        zjo.put("invite", mInvite);
        zjo.put("share", mShare);
        zjo.put("isInvite", getInvite() != null);
        zjo.put("hasAttachment", hasAttachment());
        zjo.put("hasFlags", hasFlags());
        zjo.put("hasTags", hasTags());
        zjo.put("isDeleted", isDeleted());
        zjo.put("isDraft", isDraft());
        zjo.put("isFlagged", isFlagged());
        zjo.put("isHighPriority", isHighPriority());
        zjo.put("isLowPriority", isLowPriority());
        zjo.put("isForwarded", isForwarded());
        zjo.put("isNotificationSent", isNotificationSent());
        zjo.put("isRepliedTo", isRepliedTo());
        zjo.put("isSentByMe", isSentByMe());
        zjo.put("isUnread", isUnread());
        zjo.put("idnt", mIdentityId);
        zjo.putMap("requestHeaders", mReqHdrs);
        return zjo;
    }

    @Override
    public String toString() {
        return String.format("[ZMessage %s]", mId);
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }

    /**
     *
     * @return the part name if this message is actually a part of another message
     */
    public String getPartName() {
        return mPartName;
    }

    public String getFlags() {
        return mFlags;
    }

    public String getSubject() {
        return mSubject;
    }

    public String getFragment() {
        return mFragment;
    }

    public String getTagIds() {
        return mTags;
    }

    public String getConversationId() {
        return mConversationId;
    }

    public Map<String,String> getRequestHeader() {
        return mReqHdrs;
    }

    public List<ZEmailAddress> getEmailAddresses() {
        return mAddresses;
    }

    public String getFolderId() {
        return mFolderId;
    }

    public String getMessageIdHeader() {
        return mMessageIdHeader;
    }

    public ZMimePart getMimeStructure() {
        return mMimeStructure;
    }

    public long getReceivedDate() {
        return mReceivedDate;
    }

    public long getSentDate() {
        return mSentDate;
    }

    /** content of the message, if raw is specified. if message too big or not ASCII, a content servlet URL is returned */
    public String getContent() {
        return mContent;
    }

    /** if raw is specified and message too big or not ASCII, a content servlet URL is returned */
    public String getContentURL() {
        return mContentURL;
    }

    public String getIdentityId() {
        return mIdentityId;
    }

    public static class ZMimePart implements ToZJSONObject {
        private String mPartName;
        private String mName;
        private String mContentType;
        private String mContentDisposition;
        private String mFileName;
        private String mContentId;
        private String mContentLocation;
        private String mContentDescription;
        private String mContent;
        private boolean mIsBody;
        private List<ZMimePart> mChildren;
        private long mSize;
        private ZMimePart mParent;
        private boolean mTruncated;

        public ZMimePart(ZMimePart parent, Element e) throws ServiceException {
            mParent = parent;
            mPartName = e.getAttribute(MailConstants.A_PART);
            mName = e.getAttribute(MailConstants.A_NAME, null);
            mContentType = e.getAttribute(MailConstants.A_CONTENT_TYPE, null);
            mContentDisposition = e.getAttribute(MailConstants.A_CONTENT_DISPOSITION, null);
            mFileName = e.getAttribute(MailConstants.A_CONTENT_FILENAME, null);
            mContentId = e.getAttribute(MailConstants.A_CONTENT_ID, null);
            mContentDescription = e.getAttribute(MailConstants.A_CONTENT_DESCRIPTION, null);
            mContentLocation = e.getAttribute(MailConstants.A_CONTENT_LOCATION, null);
            mIsBody = e.getAttributeBool(MailConstants.A_BODY, false);
            mSize = e.getAttributeLong(MailConstants.A_SIZE, 0);
            mContent = e.getAttribute(MailConstants.E_CONTENT, null);
            mChildren = new ArrayList<ZMimePart>();
            for (Element mpEl: e.listElements(MailConstants.E_MIMEPART)) {
                mChildren.add(new ZMimePart(this, mpEl));
            }
            mTruncated = e.getAttributeBool(MailConstants.A_TRUNCATED_CONTENT, false);
        }

        @Override
        public ZJSONObject toZJSONObject() throws JSONException {
            ZJSONObject zjo = new ZJSONObject();
            zjo.put("partName", mPartName);
            zjo.put("content", mContent);
            zjo.put("contentType", mContentType);
            zjo.put("contentDisposition", mContentDisposition);
            zjo.put("contentId", mContentId);
            zjo.put("contentLocation", mContentLocation);
            zjo.put("contentDescription", mContentDescription);
            zjo.put("isBody", mIsBody);
            zjo.put("size", mSize);
            zjo.put("name", mName);
            zjo.put("fileName", mFileName);
            zjo.put("children", mChildren);
            return zjo;
        }

        @Override
        public String toString() {
            return String.format("[ZMimePart %s]", mPartName);
        }

        public String dump() {
            return ZJSONObject.toString(this);
        }

        public ZMimePart getParent() {
            return mParent;
        }

        /** "" means top-level part, 1 first part, 1.1 first part of a multipart inside of 1. */
        public String getPartName() {
            return mPartName;
        }

        /** name attribute from the Content-Type param list */
        public String getName() {
            return mName;
        }

        /** MIME Content-Type */
        public String getContentType() {
            return mContentType;
        }

        /** MIME Content-Disposition */
        public String getContentDisposition() {
            return mContentDisposition;
        }

        /** filename attribute from the Content-Disposition param list */
        public String getFileName() {
            return mFileName;
        }

        /** MIME Content-ID (for display of embedded images) */
        public String getContentId() {
            return mContentId;
        }

        /** MIME/Microsoft Content-Location (for display of embedded images) */
        public String getContentLocation() {
            return mContentLocation;
        }

        /** MIME Content-Description.  Note cont-desc is not currently used in the code. */
        public String getContentDescription() {
            return mContentDescription;
        }

        /** content of the part, if requested */
        public String getContent() {
            return mContent;
        }

        /** set to 1, if this part is considered to be the "body" of the message for display purposes */
        public boolean isBody() {
            return mIsBody;
        }

        /** get child parts */
        public List<ZMimePart> getChildren() {
            return mChildren;
        }

        public long getSize() {
            return mSize;
        }

        public boolean wasTruncated() {
            return mTruncated;
        }
    }

    public boolean hasAttachment() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.attachment.getFlagChar()) != -1;
    }

    public boolean isDeleted() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.deleted.getFlagChar()) != -1;
    }

    public boolean isDraft() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.draft.getFlagChar()) != -1;
    }

    public boolean isFlagged() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.flagged.getFlagChar()) != -1;
    }

    public boolean isForwarded() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.forwarded.getFlagChar()) != -1;
    }

    public boolean isNotificationSent() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.notificationSent.getFlagChar()) != -1;
    }

    public boolean isRepliedTo() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.replied.getFlagChar()) != -1;
    }

    public boolean isSentByMe() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.sentByMe.getFlagChar()) != -1;
    }

    public boolean isUnread() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.unread.getFlagChar()) != -1;
    }

    public boolean isHighPriority() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.highPriority.getFlagChar()) != -1;
    }

    public boolean isLowPriority() {
        return hasFlags() && mFlags.indexOf(ZMessage.Flag.lowPriority.getFlagChar()) != -1;
    }

    public void delete() throws ServiceException {
        getMailbox().deleteMessage(getId());
    }

    public void deleteItem() throws ServiceException {
        delete();
    }

    public void trash() throws ServiceException {
        getMailbox().trashMessage(getId());
    }

    public void markRead(boolean read) throws ServiceException {
        getMailbox().markMessageRead(getId(), read);
    }

    public void flag(boolean flag) throws ServiceException {
        getMailbox().flagMessage(getId(), flag);
    }


    public void tag(String nameOrId, boolean tagged) throws ServiceException {
        ZTag tag = mMailbox.getTag(nameOrId);
        if (tag == null)
            throw ZClientException.CLIENT_ERROR("unknown tag: "+nameOrId, null);
        else
           tag(tag, tagged);
    }

    public void tag(ZTag tag, boolean tagged) throws ServiceException {
        mMailbox.tagMessage(mId, tag.getId(), tagged);
    }

    public void move(String pathOrId) throws ServiceException {
        ZFolder destFolder = mMailbox.getFolder(pathOrId);
        if (destFolder == null)
            throw ZClientException.CLIENT_ERROR("unknown folder: "+pathOrId, null);
        else
            move(destFolder);
    }

    public void move(ZFolder destFolder) throws ServiceException {
        mMailbox.moveMessage(mId, destFolder.getId());
    }

    public void markSpam(boolean spam, String pathOrId) throws ServiceException {
        ZFolder destFolder = mMailbox.getFolder(pathOrId);
        if (destFolder == null)
            throw ZClientException.CLIENT_ERROR("unknown folder: "+pathOrId, null);
        else
            markSpam(spam, destFolder);
    }

    public void markSpam(boolean spam, ZFolder destFolder) throws ServiceException {
        getMailbox().markMessageSpam(getId(), spam, destFolder == null ? null : destFolder.getId());
    }

    public void update(String destFolderId, String tagList, String flags) throws ServiceException {
        getMailbox().updateMessage(getId(), destFolderId, tagList, flags); // TODO: simplify tags/folders
    }
}
