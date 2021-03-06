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

package com.zimbra.soap.admin.message;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.admin.type.ZimletDeploymentStatus;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=AdminConstants.E_DEPLOY_ZIMLET_RESPONSE)
@XmlType(propOrder = {})
public class DeployZimletResponse {

    /**
     * @zm-api-field-description Progress information on deployment to servers
     */
    @XmlElement(name=AdminConstants.E_PROGRESS /* progress */, required=false)
    private List<ZimletDeploymentStatus> progresses = Lists.newArrayList();

    public DeployZimletResponse() {
    }

    public void setProgresses(Iterable <ZimletDeploymentStatus> progresses) {
        this.progresses.clear();
        if (progresses != null) {
            Iterables.addAll(this.progresses,progresses);
        }
    }

    public void addProgress(ZimletDeploymentStatus progress) {
        this.progresses.add(progress);
    }

    public List<ZimletDeploymentStatus> getProgresses() {
        return Collections.unmodifiableList(progresses);
    }

    public Objects.ToStringHelper addToStringInfo(
                Objects.ToStringHelper helper) {
        return helper
            .add("progresses", progresses);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this))
                .toString();
    }
}
