/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.prov.ldap;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.ldap.ILdapContext;
import com.zimbra.cs.ldap.LdapException;
import com.zimbra.cs.ldap.LdapServerType;
import com.zimbra.cs.ldap.SearchLdapOptions;
import com.zimbra.cs.ldap.ZAttributes;
import com.zimbra.cs.ldap.ZLdapContext;
import com.zimbra.cs.ldap.ZSearchControls;
import com.zimbra.cs.ldap.ZSearchResultEntry;
import com.zimbra.cs.ldap.ZSearchResultEnumeration;
import com.zimbra.cs.ldap.LdapException.LdapEntryNotFoundException;
import com.zimbra.cs.ldap.LdapException.LdapMultipleEntriesMatchedException;

public abstract class LdapHelper {
    
    private LdapProv ldapProv;
    
    protected LdapHelper(LdapProv ldapProv) {
        this.ldapProv = ldapProv;
    }
    
    protected LdapProv getProv() {
        return ldapProv;
    }
    
    public abstract void searchLdap(ILdapContext ldapContext, SearchLdapOptions searchOptions) 
    throws ServiceException;
    
    /**
     * Modifies the specified entry.  <code>attrs</code> is a <code>Map</code> consisting of
     * keys that are <code>String</code>s, and values that are either
     * <ul>
     *   <li><code>null</code>, in which case the attr is removed</li>
     *   <li>a single <code>Object</code>, in which case the attr is modified
     *     based on the object's <code>toString()</code> value</li>
     *   <li>an <code>Object</code> array or <code>Collection</code>,
     *     in which case a multi-valued attr is updated</li>
     * </ul>
     */
    public abstract void modifyAttrs(ZLdapContext zlc, String dn, Map<String, ? extends Object> attrs, 
            Entry entry) throws ServiceException;
    
    
    /**
     * Search for an entry by search base and query.
     * At most one entry will be returned from the search.
     * 
     * @param base        search base
     * 
     * @param query       search query
     * 
     * @param initZlc     initial ZLdapContext
     *                        - if null, a new one will be created to be used for the search, 
     *                          and then closed
     *                        - if not null, it will be used for the search, this API will 
     *                          *not* close it, it is the responsibility of callsite to close 
     *                          it when it i no longer needed.
     *                          
     * @param useMaster   if initZlc is null, whether to do the search on LDAP master.
     *
     * @return            a ZSearchResultEnumeration is an entry is found
     *                    null if the search does not find any matching entry.
     *                        
     * @throws LdapMultipleEntriesMatchedException  if more than one entries is matched
     * 
     * @throws ServiceException                     all other errors
     */
    public abstract ZSearchResultEntry searchForEntry(String base, String query, 
            ZLdapContext initZlc, boolean useMaster) 
    throws LdapMultipleEntriesMatchedException, ServiceException;
    
    /**
     * Get all attributes of the LDAP entry at the specified DN.
     * 
     * @param dn
     * @param initZlc
     * @param useMaster
     * 
     * @return a ZAttributes objects
     *         Note: this API never returns null.  If an entry is not found at the specified 
     *         DN, LdapEntryNotFoundException will be thrown.
     * 
     * @throws LdapEntryNotFoundException  if the entry is not found
     * @throws ServiceException            all other errors
     */
    public abstract ZAttributes getAttributes(String dn, ZLdapContext initZlc, LdapServerType ldapServerType) 
    throws LdapEntryNotFoundException, ServiceException;
    
    public ZAttributes getAttributes(String dn) throws LdapEntryNotFoundException, ServiceException {
        return getAttributes(dn, null, LdapServerType.REPLICA);
    }
    
    /**
     * A convenient wrapper for ZldapContext.searchDir.
     * Saves callsites the burden of having to get and close ZldapContext
     * 
     * @param baseDN
     * @param query
     * @param searchControls
     * @return
     * @throws LdapException
     */
    public abstract ZSearchResultEnumeration searchDir(String baseDN, String query, 
            ZSearchControls searchControls, ZLdapContext initZlc, LdapServerType ldapServerType) 
    throws ServiceException;
        
    public ZSearchResultEnumeration searchDir(String baseDN, String query, 
            ZSearchControls searchControls) 
    throws ServiceException {
        return searchDir(baseDN, query, searchControls, null, LdapServerType.REPLICA);
    }
}
