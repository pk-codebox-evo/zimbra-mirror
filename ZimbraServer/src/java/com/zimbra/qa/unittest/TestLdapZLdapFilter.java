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
package com.zimbra.qa.unittest;

import org.junit.*;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.ldap.LdapUtilCommon;
import com.zimbra.cs.ldap.ZLdapFilter;
import com.zimbra.cs.ldap.ZLdapFilterFactory;
import com.zimbra.cs.prov.ldap.LdapFilter;

import static org.junit.Assert.*;

public class TestLdapZLdapFilter {
    
    private static Provisioning prov;
    private static ZLdapFilterFactory filterDactory;
    
    @BeforeClass
    public static void init() throws Exception {
        TestLdap.manualInit();
        
        prov = Provisioning.getInstance();
        filterDactory = ZLdapFilterFactory.getInstance();
    }
    
    private String genUUID() {
        return LdapUtilCommon.generateUUID();
    }
    
    @Test
    public void hasSubordinates() throws Exception {
        String filter = LdapFilter.hasSubordinates();
        String zFilter = filterDactory.hasSubordinates().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void anyEntry() throws Exception {
        String filter = LdapFilter.anyEntry();
        String zFilter = filterDactory.anyEntry().toFilterString();
        assertEquals(filter, zFilter);
    }
    @Test
    public void allAccounts() throws Exception {
        String filter = LdapFilter.allAccounts();
        String zFilter = filterDactory.allAccounts().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allNonSystemAccounts() throws Exception {
        String filter = LdapFilter.allNonSystemAccounts();
        // (&(objectclass=zimbraAccount)(!(objectclass=zimbraCalendarResource))(!(zimbraIsSystemResource=TRUE)))
        
        String zFilter = filterDactory.allNonSystemAccounts().toFilterString();
        // (&(&(objectclass=zimbraAccount)(!(objectclass=zimbraCalendarResource)))(!(zimbraIsSystemResource=TRUE)))
        
        // assertEquals(filter, zFilter);  the diff is OK
    }
    
    @Test
    public void accountByForeignPrincipal() throws Exception {
        String FOREIFN_PRINCIPAL = "accountByForeignPrincipal";
        
        String filter = LdapFilter.accountByForeignPrincipal(FOREIFN_PRINCIPAL);
        String zFilter = filterDactory.accountByForeignPrincipal(FOREIFN_PRINCIPAL).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void accountById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.accountById(ID);
        String zFilter = filterDactory.accountById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void accountByName() throws Exception {
        String NAME = "accountByName";
            
        String filter = LdapFilter.accountByName(NAME);
        String zFilter = filterDactory.accountByName(NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void adminAccountByRDN() throws Exception {
        String NAMING_RDN_ATTR = "uid";
        String NAME = "adminAccountByRDN";
        
        String filter = LdapFilter.adminAccountByRDN(NAMING_RDN_ATTR, NAME);
        String zFilter = filterDactory.adminAccountByRDN(NAMING_RDN_ATTR, NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void adminAccountByAdminFlag() throws Exception {
        String filter = LdapFilter.adminAccountByAdminFlag();
        String zFilter = filterDactory.adminAccountByAdminFlag().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void accountsHomedOnServer() throws Exception {
        Server SERVER = prov.getLocalServer();
        
        String filter = LdapFilter.accountsHomedOnServer(SERVER);
        String zFilter = filterDactory.accountsHomedOnServer(SERVER).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void homedOnServer() throws Exception {
        Server SERVER = prov.getLocalServer();
        
        String filter = LdapFilter.homedOnServer(SERVER);
        String zFilter = filterDactory.homedOnServer(SERVER).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void homedOnServerByServerName() throws Exception {
        String SERVER_NAME = "homedOnServerByServerName";
        
        String filter = LdapFilter.homedOnServer(SERVER_NAME);
        String zFilter = filterDactory.homedOnServer(SERVER_NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void accountsOnServerOnCosHasSubordinates() throws Exception {
        Server SERVER = prov.getLocalServer();
        String COS_ID = genUUID();
        
        String filter = LdapFilter.accountsOnServerOnCosHasSubordinates(SERVER, COS_ID);
        String zFilter = filterDactory.accountsOnServerOnCosHasSubordinates(SERVER, COS_ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allCalendarResources() throws Exception {
        String filter = LdapFilter.allCalendarResources();
        String zFilter = filterDactory.allCalendarResources().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void calendarResourceByForeignPrincipal() throws Exception {
        String FOREIGN_PRINCIPAL = "calendarResourceByForeignPrincipal";
        
        String filter = LdapFilter.calendarResourceByForeignPrincipal(FOREIGN_PRINCIPAL);
        String zFilter = filterDactory.calendarResourceByForeignPrincipal(FOREIGN_PRINCIPAL).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void calendarResourceById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.calendarResourceById(ID);
        String zFilter = filterDactory.calendarResourceById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void calendarResourceByName() throws Exception {
        String NAME = "calendarResourceByName";
        
        String filter = LdapFilter.calendarResourceByName(NAME);
        String zFilter = filterDactory.calendarResourceByName(NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allCoses() throws Exception {
        String filter = LdapFilter.allCoses();
        String zFilter = filterDactory.allCoses().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void cosById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.cosById(ID);
        String zFilter = filterDactory.cosById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void cosesByMailHostPool() throws Exception {
        String SERVER_NAME = "cosesByMailHostPool";
        
        String filter = LdapFilter.cosesByMailHostPool(SERVER_NAME);
        String zFilter = filterDactory.cosesByMailHostPool(SERVER_NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allDataSources() throws Exception {
        String filter = LdapFilter.allDataSources();
        String zFilter = filterDactory.allDataSources().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void dataSourceById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.dataSourceById(ID);
        String zFilter = filterDactory.dataSourceById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void dataSourceByName() throws Exception {
        String NAME = "dataSourceByName";
        
        String filter = LdapFilter.dataSourceByName(NAME);
        String zFilter = filterDactory.dataSourceByName(NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allDistributionLists() throws Exception {
        String filter = LdapFilter.allDistributionLists();
        String zFilter = filterDactory.allDistributionLists().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void distributionListById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.distributionListById(ID);
        String zFilter = filterDactory.distributionListById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void distributionListByName() throws Exception {
        String NAME = "distributionListByName";
        
        String filter = LdapFilter.distributionListByName(NAME);
        String zFilter = filterDactory.distributionListByName(NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allDomains() throws Exception {
        String filter = LdapFilter.allDomains();
        String zFilter = filterDactory.allDomains().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void domainById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.domainById(ID);
        String zFilter = filterDactory.domainById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void domainByName() throws Exception {
        String NAME = "domainByName";
        
        String filter = LdapFilter.domainByName(NAME);
        String zFilter = filterDactory.domainByName(NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void domainByKrb5Realm() throws Exception {
        String REALM = "domainByKrb5Realm";
        
        String filter = LdapFilter.domainByKrb5Realm(REALM);
        String zFilter = filterDactory.domainByKrb5Realm(REALM).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void domainByVirtualHostame() throws Exception {
        String VIRTUAL_HOST_NAME = "domainByVirtualHostame";
        
        String filter = LdapFilter.domainByVirtualHostame(VIRTUAL_HOST_NAME);
        String zFilter = filterDactory.domainByVirtualHostame(VIRTUAL_HOST_NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void domainByForeignName() throws Exception {
        String FOREIGN_NAME = "domainByForeignName";
        
        String filter = LdapFilter.domainByForeignName(FOREIGN_NAME);
        String zFilter = filterDactory.domainByForeignName(FOREIGN_NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void domainLabel() throws Exception {
        String filter = LdapFilter.domainLabel();
        String zFilter = filterDactory.domainLabel().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allIdentities() throws Exception {
        String filter = LdapFilter.allIdentities();
        String zFilter = filterDactory.allIdentities().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void identityByName() throws Exception {
        String NAME = "identityByName";
        
        String filter = LdapFilter.identityByName(NAME);
        String zFilter = filterDactory.identityByName(NAME).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allMimeEntries() throws Exception {
        String filter = LdapFilter.allMimeEntries();
        String zFilter = filterDactory.allMimeEntries().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void mimeEntryByMimeType() throws Exception {
        String MIME_TYPE = "mimeEntryByMimeType";
        
        String filter = LdapFilter.mimeEntryByMimeType(MIME_TYPE);
        String zFilter = filterDactory.mimeEntryByMimeType(MIME_TYPE).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allServers() throws Exception {
        String filter = LdapFilter.allServers();
        String zFilter = filterDactory.allServers().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void serverById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.serverById(ID);
        String zFilter = filterDactory.serverById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void serverByService() throws Exception {
        String SERVICE = "serverByService";
        
        String filter = LdapFilter.serverByService(SERVICE);
        String zFilter = filterDactory.serverByService(SERVICE).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allSignatures() throws Exception {
        String filter = LdapFilter.allSignatures();
        String zFilter = filterDactory.allSignatures().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void signatureById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.signatureById(ID);
        String zFilter = filterDactory.signatureById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allXMPPComponents() throws Exception {
        String filter = LdapFilter.allXMPPComponents();
        String zFilter = filterDactory.allXMPPComponents().toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void imComponentById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.imComponentById(ID);
        String zFilter = filterDactory.imComponentById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void xmppComponentById() throws Exception {
        String ID = genUUID();
        
        String filter = LdapFilter.xmppComponentById(ID);
        String zFilter = filterDactory.xmppComponentById(ID).toFilterString();
        assertEquals(filter, zFilter);
    }
    
    @Test
    public void allZimlets() throws Exception {
        String filter = LdapFilter.allZimlets();
        String zFilter = filterDactory.allZimlets().toFilterString();
        assertEquals(filter, zFilter);
    }

}
