/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.offline;

import com.zimbra.common.util.LogFactory;

public class OfflineLog {
    /** The "zimbra.offline" logger. For offline sync logs. */
    public static final com.zimbra.common.util.Log offline = LogFactory.getLog("zimbra.offline");

    /** The "zimbra.offline.request" logger. For recording SOAP traffic
     *  sent to the remote server. */
    public static final com.zimbra.common.util.Log request = LogFactory.getLog("zimbra.offline.request");

    /** The "zimbra.offline.response" logger. For recording SOAP traffic
     *  received from the remote server. */
    public static final com.zimbra.common.util.Log response = LogFactory.getLog("zimbra.offline.response");

    /** The "zimbra.offline.yab" logger. For recording Yahoo Address Book sync events */
    public static final com.zimbra.common.util.Log yab = LogFactory.getLog("zimbra.offline.yab");

    /** The "zimbra.offline.gab" logger. For recording Google Address Book sync events */
    public static final com.zimbra.common.util.Log gab = LogFactory.getLog("zimbra.offline.gab");

    /** For logging YMail cascade events*/
    public static final com.zimbra.common.util.Log ymail = LogFactory.getLog("zimbra.offline.ymail");
}
