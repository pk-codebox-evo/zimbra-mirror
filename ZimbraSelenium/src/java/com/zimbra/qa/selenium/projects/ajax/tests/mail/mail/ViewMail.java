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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import java.io.File;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;
//import com.zimbra.qa.selenium.projects.ajax.ui.preferences.trustedaddresses.DisplayTrustedAddress;


public class ViewMail extends PrefGroupMailByMessageTest {

	boolean injected = false;
	final String mimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email00";
	
	public ViewMail() throws HarnessException {
		logger.info("New "+ ViewMail.class.getCanonicalName());
		
		
		

		
		


	}
	
	
	@Bugs(	ids = "57047" )
	@Test(	description = "Receive a mail with Sender: specified",
			groups = { "functional" })
	public void ViewMail_01() throws HarnessException {
		
		final String subject = "subject12996131112962";
		final String from = "from12996131112962@example.com";
		final String sender = "sender12996131112962@example.com";

		if ( !injected ) {
			LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));
			injected = true;
		}


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(mail, "Verify message is received");
		ZAssert.assertEquals(from, mail.dFromRecipient.dEmailAddress, "Verify the from matches");
		ZAssert.assertEquals(sender, mail.dSenderRecipient.dEmailAddress, "Verify the sender matches");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		//ZAssert.assertEquals(	actual.zGetMailProperty(Field.OnBehalfOf), from, "Verify the On-Behalf-Of matches the 'From:' header");
		/* TODO: ... debugging to be removed */
		String fromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span:nth-child(3)>span[class='addrBubble']";
		ZAssert.assertEquals(app.zPageMail.sGetText(fromLocator).trim(), from, "Verify the On-Behalf-Of matches the 'From:' header");
		
		//ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), sender, "Verify the From matches the 'Sender:' header");
		/* TODO: ... debugging to be removed */
		String senderLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[class='addrBubble']";
		ZAssert.assertEquals(app.zPageMail.sGetText(senderLocator).trim(), sender, "Verify the From matches the 'Sender:' header");
		
	}

	@Test(	description = "Receive a mail with Reply-To: specified",
			groups = { "functional" })
	public void ViewMail_02() throws HarnessException {
		
		final String subject = "subject13016959916873";
		final String from = "from13016959916873@example.com";
		final String replyto = "replyto13016959916873@example.com";

		if ( !injected ) {
			LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));
			injected = true;
		}


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(mail, "Verify message is received");
		ZAssert.assertEquals(from, mail.dFromRecipient.dEmailAddress, "Verify the from matches");
		ZAssert.assertEquals(replyto, mail.dReplyToRecipient.dEmailAddress, "Verify the Reply-To matches");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		//ZAssert.assertEquals(	actual.zGetMailProperty(Field.ReplyTo), replyto, "Verify the Reply-To matches the 'Reply-To:' header");
		/* TODO: ... debugging to be removed */
		String replytoLocator = "css=div[id='zv__TV-main__MSG'] tr[id$='_reply to'] td.LabelColValue span[class='addrBubble'] ";
		ZAssert.assertEquals(app.zPageMail.sGetText(replytoLocator).trim(), replyto, "Verify the Reply-To matches the 'Reply-To:' header");
		
		//ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
		/* TODO: ... debugging to be removed */
		String fromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[class='addrBubble']";
		ZAssert.assertEquals(app.zPageMail.sGetText(fromLocator).trim(), from, "Verify the From matches the 'From:' header");
	
	}

	@Bugs(	ids = "61575")
	@Test(	description = "Receive a mail with Resent-From: specified",
			groups = { "functional" })
	public void ViewMail_03() throws HarnessException {
		
		final String subject = "subject13147509564213";
		final String from = "from13011239916873@example.com";
		final String resentfrom = "resentfrom13016943216873@example.com";

		if ( !injected ) {
			LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));
			injected = true;
		}


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(mail, "Verify message is received");
		ZAssert.assertEquals(resentfrom, mail.dRedirectedFromRecipient.dEmailAddress, "Verify the Resent-From matches");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		//ZAssert.assertEquals(	actual.zGetMailProperty(Field.ResentFrom), resentfrom, "Verify the Resent-From matches the 'Resent-From:' header");
		/* TODO: ... debugging to be removed */
		String resentfromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[class='addrBubble']span:contains(resentfrom)";
		ZAssert.assertEquals(app.zPageMail.sGetText(resentfromLocator).trim(), resentfrom, "Verify the From matches the 'From:' header");

		//ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
		/* TODO: ... debugging to be removed */
		String fromLocator = "css=div[id='zv__TV-main__MSG'] td[id$='_from'] span[class='addrBubble']";
		ZAssert.assertEquals(app.zPageMail.sGetText(fromLocator).trim(), from, "Verify the From matches the 'From:' header");


		
	}

	@Bugs(	ids = "64444")
	@Test(	description = "Receive a mail with only audio/vav content",
			groups = { "functional" })
	public void ViewMail_04() throws HarnessException {
		
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug64444";
		final String subject = "subject13150123168433";
		final String from = "from13160123168433@testdomain.com";
		final String to = "to3163210168433@testdomain.com";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject +")");
		ZAssert.assertNotNull(mail, "Verify message is received");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), from, "Verify the From matches the 'From:' header");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.To), to, "Verify the From matches the 'From:' header");
		

		
	}

	@Bugs(	ids = "66565")
	@Test(	description = "Receive a mail formatting in the subject",
			groups = { "functional" })
	public void ViewMail_05() throws HarnessException {
		
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug66565";
		final String subject = "subject13197565510464";
		final String subjectText = "<u><i> subject13197565510464 </i></u>";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject +")");
		ZAssert.assertNotNull(mail, "Verify message is received");
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the Subject
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Subject), subjectText, "Verify the Subject matches");
		
		// Verify the <u> and <i> elements are not in the DOM (only in text)
		//
		// i.e. this woud be wrong:
		//
		// <td width="100%" class="subject">
		//  <u>
		//   <i>
		//    subject13197565510464 
		//   </i>
		//  </u>
		// </td>
		//
		// Expected:
		// <td width="100%" class="subject">
		//  &lt;u&gt;&lt;i&gt; subject13197565510464 &lt;/i&gt;&lt;/u&gt;
		// </td>
		//
		String locator = "css=div[id='zv__TV-main__MSG'] tr[id$='_hdrTableTopRow'] td[class~='SubjectCol']";
		ZAssert.assertFalse( actual.sIsElementPresent(locator + " u"), "Verify the <u> element is not in the DOM");
		ZAssert.assertFalse( actual.sIsElementPresent(locator + " i"), "Verify the <i> element is not in the DOM");



		
	}

	@Bugs(	ids = "65933,65623")
	@Test(	description = "Verify message with only HTML part",
			groups = { "functional" })
	public void ViewMail_06() throws HarnessException {

		// Inject the sample mime
		String subject = "subject13188948451403";
		String content = "Welcome to the NetWorker Listserv list";
		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug65933";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(MimeFolder));

		
		// Refresh the inbox
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertStringContains(actual.zGetMailProperty(Field.Body), content, "Verify the body displays correctly");
		
	}

	@Bugs(	ids = "65933,65623")
	@Test(	description = "Verify message with only HTML part and charset",
			groups = { "functional" })
	public void ViewMail_07() throws HarnessException {

		// Inject the sample mime
		String subject = "subject13189485723753";
		String content = "Enrico Medici";
		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug65623";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(MimeFolder));

		
		// Refresh the inbox
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertStringContains(actual.zGetMailProperty(Field.Body), content, "Verify the body displays correctly");
		
	}

	@Bugs(	ids = "65079")
	@Test(	description = "Verify message with only HTML part and charset",
			groups = { "functional" })
	public void ViewMail_08() throws HarnessException {

		// Inject the sample mime
		String subject = "subject13189993282183";
		String content = "Incident Title";
		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug65079";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(MimeFolder));

		
		// Refresh the inbox
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertStringContains(actual.zGetMailProperty(Field.Body), content, "Verify the body displays correctly");
		
	}

	@Test(	description = "zimbraPrefMessageViewHtmlPreferred=TRUE: Receive message with text only parts - should be rendered as text",
			groups = { "functional" })
	public void ViewMail_09() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email04/mimeTextOnly.txt";
		final String subject = "subject13214016725788";
		final String content = "The Ming Dynasty";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), content, "Verify the text content");
		
	}

	@Test(	description = "zimbraPrefMessageViewHtmlPreferred=TRUE: Receive message with html only parts - should be rendered as html",
			groups = { "functional" })
	public void ViewMail_10() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email04/mimeHtmlOnly.txt";
		final String subject = "subject13214016672655";
		final String content = "Bold";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), content, "Verify the text content");
		
		
	}
	
	@Test(	description = "zimbraPrefMessageViewHtmlPreferred=TRUE: Receive message with text and html  parts - should be rendered as html",
			groups = { "functional" })
	public void ViewMail_11() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email04/mimeTextAndHtml.txt";
		final String subject = "subject13214016621403";
		final String content = "Bold";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), content, "Verify the text content");
		
		
	}


	@Bugs(ids = "67854")
	@Test(	description = "Verify empty message shows 'no content'",
			groups = { "functional" })
	public void ViewMail_12() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug67854";
		final String subject = "subject13218526621403";
		final String content = "The message has no text content.";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Get the content
		String body = actual.zGetMailProperty(Field.Body);
		
		// Verify the Content shows "no content"
		ZAssert.assertStringContains(body, content, "Verify the text content");
		
		// Bug 67854: Verify the Content does not show HTML
		ZAssert.assertStringDoesNotContain(body, "&lt;table", "Verify the content does not contain HTML");

	}

	@Bugs(ids = "72248")
	@Test(	description = "Verify multipart/alternative with only 1 part",
			groups = { "functional" })
	public void ViewMail_13() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug72248";
		final String subject = "subject13217218621403";
		final String content = "content1328844621403";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Get the content
		String body = actual.zGetMailProperty(Field.Body);
		
		// Verify the Content shows "no content"
		ZAssert.assertStringContains(body, content, "Verify the text content");
		

	}

	@Test(	description = "Verify multipart/alternative with text and html parts",
			groups = { "functional" })
	public void ViewMail_14() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug72233";
		final String subject = "bug72233";
		// final String textcontent = "text1328844621404";
		final String htmlcontent = "html1328844621404";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Get the content
		String body = actual.zGetMailProperty(Field.Body);
		
		// Verify the Content shows correctly
		ZAssert.assertStringContains(body, htmlcontent, "Verify the html content");
		

	}

	// See also 
	// com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.trustedaddresses.TrustedEmailAddrMsgView.TrustedEmailAddrMsgView_01
	// et. al.
	
	@Test(	description = "View a message with external images",
			groups = { "functional" })
	public void ViewMail_20() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/externalImage01/externalimage01.txt";
		final String subject = "externalimage01";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		// Verify Warning info bar with other links

		ZAssert.assertTrue(app.zPageMail.zHasWDDLinks(), "Verify display images link");
		

	}


}
