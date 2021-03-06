/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.service.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.service.mail.SendInviteReply;

/**
 * @author vmahajan
 */
public class OfflineSendInviteReply extends SendInviteReply {

    @Override
    protected boolean deleteInviteOnReply(Account acct) throws ServiceException {
        return OfflineProvisioning.getOfflineInstance().getLocalAccount().isPrefDeleteInviteOnReply();
    }
}
