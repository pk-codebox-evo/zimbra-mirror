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

package com.zimbra.soap.admin.type;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.zimbra.common.soap.AdminConstants;

@XmlAccessorType(XmlAccessType.NONE)
public class TZFixupRuleMatchDate {

    /**
     * @zm-api-field-taga match-month
     * @zm-api-field-descriptiona Match month.  Value between 1 (January) and 12 (December)
     */
    @XmlAttribute(name=AdminConstants.A_MON /* mon */, required=true)
    private final int month;

    /**
     * @zm-api-field-taga match-month-day
     * @zm-api-field-descriptiona Match month day.  Value between 1 and 31
     */
    @XmlAttribute(name=AdminConstants.A_MDAY /* mday */, required=true)
    private final int monthDay;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private TZFixupRuleMatchDate() {
        this(-1, -1);
    }

    public TZFixupRuleMatchDate(int month, int monthDay) {
        this.month = month;
        this.monthDay = monthDay;
    }

    public int getMonth() { return month; }
    public int getMonthDay() { return monthDay; }

    public Objects.ToStringHelper addToStringInfo(
                Objects.ToStringHelper helper) {
        return helper
            .add("month", month)
            .add("monthDay", monthDay);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this))
                .toString();
    }
}
