/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.imap;

/**
 * Exception thrown when session notification encounters a message which cannot be properly renumbered
 * This typically occurs when mailbox.item_id_checkpoint is inconsistent due to earlier manual DB modification
 * See bug 46549 and bug 77780 for more details on the sequence of events which results in this bad state
 */
public class ImapRenumberException extends RuntimeException {

    private static final long serialVersionUID = 6406289034846208672L;

    public ImapRenumberException() {
        super();
    }

    public ImapRenumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImapRenumberException(String message) {
        super(message);
    }

}
