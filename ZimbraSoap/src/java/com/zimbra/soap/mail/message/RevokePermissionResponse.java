/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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

package com.zimbra.soap.mail.message;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.mail.type.AccountACEinfo;

/*
 * Delete this class in bug 66989
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=MailConstants.E_REVOKE_PERMISSION_RESPONSE)
public class RevokePermissionResponse {

    /**
     * @zm-api-field-description Permissions that were successfully revoked
     */
    @XmlElement(name=MailConstants.E_ACE /* ace */, required=false)
    private List<AccountACEinfo> aces = Lists.newArrayList();

    public RevokePermissionResponse() {
    }

    public void setAces(Iterable <AccountACEinfo> aces) {
        this.aces.clear();
        if (aces != null) {
            Iterables.addAll(this.aces,aces);
        }
    }

    public RevokePermissionResponse addAce(AccountACEinfo ace) {
        this.aces.add(ace);
        return this;
    }

    public List<AccountACEinfo> getAces() {
        return Collections.unmodifiableList(aces);
    }

    public Objects.ToStringHelper addToStringInfo(Objects.ToStringHelper helper) {
        return helper
            .add("aces", aces);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this)).toString();
    }
}
