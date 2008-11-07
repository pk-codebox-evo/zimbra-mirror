/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;
import com.zimbra.soap.SoapContextExtension;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.MailConstants;

public class OfflineService implements DocumentService {

    public static final String NAMESPACE_STR = "urn:zimbraOffline";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    // sync
    public static final QName SYNC_REQUEST = QName.get("SyncRequest", NAMESPACE);
    public static final QName SYNC_RESPONSE = QName.get("SyncResponse", NAMESPACE);

    public void registerHandlers(DocumentDispatcher dispatcher) {
        // sync
        dispatcher.registerHandler(SYNC_REQUEST, new OfflineSync());
        
        dispatcher.registerHandler(MailConstants.NO_OP_REQUEST, new OfflineNoOp());

        // fetching external data
        dispatcher.registerHandler(MailConstants.FOLDER_ACTION_REQUEST, new OfflineFolderAction());
        dispatcher.registerHandler(MailConstants.GET_IMPORT_STATUS_REQUEST, new OfflineGetImportStatus());
        dispatcher.registerHandler(MailConstants.IMPORT_DATA_REQUEST, new OfflineImportData());
        dispatcher.registerHandler(AccountConstants.GET_INFO_REQUEST, new OfflineGetInfo());
        
        dispatcher.registerHandler(MailConstants.DIFF_DOCUMENT_REQUEST, new OfflineDocumentHandlers.DiffDocument());
        dispatcher.registerHandler(MailConstants.LIST_DOCUMENT_REVISIONS_REQUEST, new OfflineDocumentHandlers.ListDocumentRevisions());
        dispatcher.registerHandler(MailConstants.GET_WIKI_REQUEST, new OfflineDocumentHandlers.GetWiki());
        dispatcher.registerHandler(MailConstants.SAVE_DOCUMENT_REQUEST, new OfflineSaveDocument());
        dispatcher.registerHandler(MailConstants.SAVE_WIKI_REQUEST, new OfflineDocumentHandlers.SaveWiki());
        dispatcher.registerHandler(MailConstants.WIKI_ACTION_REQUEST, new OfflineDocumentHandlers.WikiAction());
        
        dispatcher.registerHandler(AccountConstants.AUTO_COMPLETE_GAL_REQUEST, new OfflineAutoCompleteGal());        
        dispatcher.registerHandler(AccountConstants.SEARCH_GAL_REQUEST, new OfflineSearchGal());
        dispatcher.registerHandler(MailConstants.GET_FREE_BUSY_REQUEST, OfflineServiceProxy.GetFreeBusy());
        dispatcher.registerHandler(AccountConstants.SEARCH_CALENDAR_RESOURCES_REQUEST, OfflineServiceProxy.SearchCalendarResources());
        
        // not the most suitable place to do this, but it's just too easy.
        SoapContextExtension.register(OfflineContextExtension.ZDSYNC, new OfflineContextExtension());
    }
}