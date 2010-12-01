/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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

package com.zimbra.soap.mail.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.mail.type.ImportContact;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=MailConstants.E_IMPORT_CONTACTS_RESPONSE)
@XmlType(propOrder = {MailConstants.E_CONTACT})
public class ImportContactsResponse {

    @XmlElement(name=MailConstants.E_CONTACT, required=true)
    private ImportContact cn;
    
    public ImportContactsResponse() {
    }
    
    public ImportContact getContact() {
        return cn;
    }
    
    public void setContact(ImportContact contact) {
        this.cn = contact;
    }
}
