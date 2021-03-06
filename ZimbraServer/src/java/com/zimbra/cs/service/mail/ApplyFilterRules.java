/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

package com.zimbra.cs.service.mail;

import com.google.common.io.Closeables;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.filter.RuleManager;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.soap.ZimbraSoapContext;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.parser.generated.ParseException;
import org.dom4j.QName;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;


public class ApplyFilterRules extends MailDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("cannot access account");

        // Get rules.
        String fullScript = getRules(account);
        if (StringUtil.isNullOrEmpty(fullScript)) {
            throw ServiceException.INVALID_REQUEST("Account has no filter rules defined.", null);
        }

        List<Element> ruleElements =
            request.getElement(MailConstants.E_FILTER_RULES).listElements(MailConstants.E_FILTER_RULE);
        if (ruleElements.size() == 0) {
            String msg = String.format("No %s elements specified.", MailConstants.E_FILTER_RULE);
            throw ServiceException.INVALID_REQUEST(msg, null);
        }

        // Concatenate script parts and create a new script to run on existing messages.
        StringBuilder buf = new StringBuilder();
        for (Element ruleEl : ruleElements) {
            String name = ruleEl.getAttribute(MailConstants.A_NAME);
            String singleRule = RuleManager.getRuleByName(fullScript, name);
            if (singleRule == null) {
                String msg = String.format("Could not find a rule named '%s'", name);
                throw ServiceException.INVALID_REQUEST(msg, null);
            }
            buf.append(singleRule).append("\n");
        }
        String partialScript = buf.toString();
        ZimbraLog.filter.debug("Applying partial script to existing messages: %s", partialScript);
        Node node = null;
        try {
            node = RuleManager.parse(partialScript);
        } catch (ParseException e) {
            throw ServiceException.FAILURE("Unable to parse Sieve script: " + partialScript, e);
        }

        // Get the ids of the messages to filter.
        Element msgEl = request.getOptionalElement(MailConstants.E_MSG);
        String query = getElementText(request, MailConstants.E_QUERY);
        if (msgEl != null && query != null) {
            String msg = String.format("Cannot specify both %s and %s elements.",
                MailConstants.E_MSG, MailConstants.E_QUERY);
            throw ServiceException.INVALID_REQUEST(msg, null);
        }

        Mailbox mbox = getRequestedMailbox(zsc);
        List<Integer> messageIds = new ArrayList<Integer>();
        List<Integer> affectedIds = new ArrayList<Integer>();
        OperationContext octxt = getOperationContext(zsc, context);

        if (msgEl != null) {
            String[] ids = msgEl.getAttribute(MailConstants.A_IDS).split(",");
            for (String id : ids) {
                messageIds.add(Integer.valueOf(id));
            }
        } else if (query != null) {
            ZimbraQueryResults results = null;
            try {
                results = mbox.index.search(octxt, query,
                        EnumSet.of(MailItem.Type.MESSAGE), SortBy.NONE, Integer.MAX_VALUE);
                while (results.hasNext()) {
                    ZimbraHit hit = results.getNext();
                    messageIds.add(hit.getItemId());
                }
            } catch (Exception e) {
                String msg = String.format("Unable to run search for query: '%s'", query);
                throw ServiceException.INVALID_REQUEST(msg, e);
            } finally {
                Closeables.closeQuietly(results);
            }
        } else {
            String msg = String.format("Must specify either the %s or %s element.",
                MailConstants.E_MSG, MailConstants.E_QUERY);
            throw ServiceException.INVALID_REQUEST(msg, null);
        }

        int max = account.getFilterBatchSize();
        if (messageIds.size() > max) {
            throw ServiceException.INVALID_REQUEST("Attempted to apply filter rules to " + messageIds.size() +
                " messages, which exceeded the limit of " + max, null);
        }

        ZimbraLog.filter.info("Applying filter rules to %s existing messages.", messageIds.size());
        long sleepInterval = account.getFilterSleepInterval();

        // Apply filter rules.
        for (int i = 0; i < messageIds.size(); i++) {
            if (i > 0 && sleepInterval > 0) {
                try {
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException e) {
                }
            }

            int id = messageIds.get(i);
            try {
                mbox.getMessageById(octxt, id);
                if (RuleManager.applyRulesToExistingMessage(octxt, mbox, id, node)) {
                    affectedIds.add(id);
                }
            } catch (NoSuchItemException e) {
                // Message was deleted since the search was done (bug 41609).
                ZimbraLog.filter.info("Skipping message %d: %s.", id, e.toString());
            } catch (ServiceException e) {
                ZimbraLog.filter.warn("Unable to filter message %d.", id, e);
            }
        }

        // Send response.
        Element response = zsc.createElement(getResponseElementName());
        if (affectedIds.size() > 0) {
            response.addElement(MailConstants.E_MSG)
                .addAttribute(MailConstants.A_IDS, StringUtil.join(",", affectedIds));
        }
        return response;
    }

    protected String getRules(Account account) {
        return RuleManager.getIncomingRules(account);
    }

    protected QName getResponseElementName() {
        return MailConstants.APPLY_FILTER_RULES_RESPONSE;
    }

    private String getElementText(Element parent, String childName) {
        Element child = parent.getOptionalElement(childName);
        if (child == null) {
            return null;
        }
        return child.getTextTrim();
    }
}
