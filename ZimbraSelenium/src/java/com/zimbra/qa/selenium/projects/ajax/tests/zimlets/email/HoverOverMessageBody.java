/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.email;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.TooltipContact;


public class HoverOverMessageBody extends PrefGroupMailByMessageTest {

	
	public HoverOverMessageBody() throws HarnessException {
		logger.info("New "+ HoverOverMessageBody.class.getCanonicalName());
		
	}
	
	@Test(	description = "Hover over a contact in a message body",
			groups = { "functional" })
	public void HoverOverMessageBody_01() throws HarnessException {

		//-- DATA Setup
		final String email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@foo.com";
		final String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>last"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='email'>"+ email +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );

		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='" + FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox).getId() + "' >"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"Line 1\n"
        	+				"abc "+ email +" def\n"
        	+				"Line 2\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");


		
		
		//-- GUI Actions
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Hover over the email address
		String locator = "css=span[id$='_ZmEmailObjectHandler']:contains("+ email +")";
		app.zPageMail.sMouseOver(locator, (WebElement[]) null);
		
		
		
		//-- VERIFICATION
		
		
		// Verify the contact tool tip opens
		TooltipContact tooltip = new TooltipContact(app);
		tooltip.zWaitForActive();
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");
		
		
	}
	
	@Test(	description = "Hover over a GAL contact in a message body",
			groups = { "functional" })
	public void HoverOverMessageBody_02() throws HarnessException {

		//-- DATA Setup
		
		// Create a contact in the GAL
		ZimbraAccount contactGAL = (new ZimbraAccount()).provision().authenticate();
		
		final String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='" + FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox).getId() + "' >"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"Line 1\n"
        	+				"abc "+ contactGAL.EmailAddress +" def\n"
        	+				"Line 2\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");


		
		
		//-- GUI Actions
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Hover over the email address
		String locator = "css=span[id$='_ZmEmailObjectHandler']:contains("+ contactGAL.EmailAddress +")";
		app.zPageMail.sMouseOver(locator, (WebElement[]) null);
		
		
		
		//-- VERIFICATION
		
		
		// Verify the contact tool tip opens
		TooltipContact tooltip = new TooltipContact(app);
		tooltip.zWaitForActive();
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");
		
		
	}

	@Test(	description = "Hover over a contact group in a message body",
			groups = { "functional" })
	public void HoverOverMessageBody_03() throws HarnessException {

		//-- DATA Setup

		String groupName = "group" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
					"<cn >" +
						"<a n='type'>group</a>" +
						"<a n='nickname'>" + groupName +"</a>" +
						"<a n='fileAs'>8:" +  groupName +"</a>" +
				        "<m type='I' value='" + ZimbraAccount.AccountA().EmailAddress + "' />" +
				        "<m type='I' value='" + ZimbraAccount.AccountB().EmailAddress + "' />" +
					"</cn>" +
				"</CreateContactRequest>");

		final String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='" + FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox).getId() + "' >"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"Line 1\n"
        	+				"abc "+ groupName +" def\n"
        	+				"Line 2\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");


		
		
		//-- GUI Actions
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Hover over the email address
		String locator = "css=span[id$='_com_zimbra_email']:contains("+ groupName +")";
		
		
		
		//-- VERIFICATION
		
		
		// Verify the contact group is not converted to the email zimlet link
		boolean present = app.zPageMail.sIsElementPresent(locator);
		ZAssert.assertFalse(present, "Verify the contact group name is not highlighted");
		
		
	}
	
	@Test(	description = "Hover over an unknown email address",
			groups = { "functional" })
	public void HoverOverMessageBody_04() throws HarnessException {

		//-- DATA Setup
		final String email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@foo.com";
		final String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='" + FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox).getId() + "' >"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"Line 1\n"
        	+				"abc "+ email +" def\n"
        	+				"Line 2\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");


		
		
		//-- GUI Actions
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Hover over the email address
		String locator = "css=span[id$='_ZmEmailObjectHandler']:contains("+ email +")";
		app.zPageMail.sMouseOver(locator, (WebElement[]) null);
		
		
		
		//-- VERIFICATION
		
		
		// Verify the contact tool tip opens
		TooltipContact tooltip = new TooltipContact(app);
		tooltip.zWaitForActive();
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");
		
		
	}


}
