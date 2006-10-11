/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.dav.service.method;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.dav.DavContext;
import com.zimbra.cs.dav.DavElements;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.DavProtocol;
import com.zimbra.cs.dav.DavContext.Depth;
import com.zimbra.cs.dav.resource.DavResource;
import com.zimbra.cs.dav.resource.UrlNamespace;
import com.zimbra.cs.dav.service.DavMethod;

public class PropFind extends DavMethod {
	
	public static final String PROPFIND  = "PROPFIND";
	
	public String getName() {
		return PROPFIND;
	}
	
	public void handle(DavContext ctxt) throws DavException, IOException {
		boolean nameOnly = false;
		Map<String,QName> requestedProps = null;
		
		if (ctxt.hasRequestMessage()) {
			Document req = ctxt.getRequestMessage();
			Element top = req.getRootElement();
			if (!top.getName().equals(DavElements.P_PROPFIND))
				throw new DavException("msg "+top.getName()+" not allowed in PROPFIND", HttpServletResponse.SC_BAD_REQUEST, null);

			@SuppressWarnings("unchecked")
			List<Element> elems = top.elements();
			if (elems.size() != 1)
				throw new DavException("msg propfind should contain one element", HttpServletResponse.SC_BAD_REQUEST, null);
			
			for (Element e : elems) {
				String name = e.getName();
				if (name.equals(DavElements.P_ALLPROP))
					requestedProps = null;
				else if (name.equals(DavElements.P_PROPNAME))
					nameOnly = true;
				else if (!name.equals(DavElements.P_PROP))
					throw new DavException("invalid element "+e.getName(), HttpServletResponse.SC_BAD_REQUEST, null);
				else {
					requestedProps = new java.util.HashMap<String,QName>();
					@SuppressWarnings("unchecked")
					List<Element> props = e.elements();
					for (Element prop : props)
						requestedProps.put(prop.getName(), prop.getQName());
				}
			}
		}
		DavResource resource = UrlNamespace.getResource(ctxt);
		addComplianceHeader(ctxt, resource);
		ctxt.setStatus(DavProtocol.STATUS_MULTI_STATUS);
		
		Document document = org.dom4j.DocumentHelper.createDocument();
		Element resp = document.addElement(DavElements.E_MULTISTATUS);
		addResourceToResponse(ctxt, resource, resp, nameOnly, requestedProps, false);

		if (ctxt.getDepth() != Depth.ZERO) {
			ZimbraLog.dav.debug("depth: "+ctxt.getDepth().name());

			List<DavResource> children = UrlNamespace.getChildren(ctxt, resource);
			for (DavResource child : children)
				addResourceToResponse(ctxt, child, resp, nameOnly, requestedProps, ctxt.getDepth() == Depth.INFINITY);
		}
		sendResponse(ctxt, document);
	}
	
	private void addResourceToResponse(DavContext ctxt, DavResource rs, Element top, boolean nameOnly, Map<String,QName> requestedProps, boolean includeChildren) throws DavException {
		Element resp = top.addElement(DavElements.E_RESPONSE);
		resp.addElement(DavElements.E_HREF).setText(UrlNamespace.getResourceUrl(rs));
		Map<Integer,Element> propstatMap = new HashMap<Integer,Element>();
		Set<String> propNames;
		if (requestedProps == null)
			propNames = rs.getAllPropertyNames();
		else
			propNames = requestedProps.keySet();
		if (requestedProps == null || requestedProps.containsKey(DavElements.P_RESOURCETYPE))
			rs.addResourceTypeElement(findPropstat(resp, propstatMap, STATUS_OK), nameOnly);
		for (String name : propNames) {
			if (name.equals(DavElements.P_RESOURCETYPE))
				continue;
			Element propstat = findPropstat(resp, propstatMap, HttpServletResponse.SC_OK);
			Element e = rs.addPropertyElement(propstat, name, nameOnly);
			if (e == null) {
				Element error = findPropstat(resp, propstatMap, HttpServletResponse.SC_NOT_FOUND);
				if (requestedProps == null)
					error.addElement(name);
				else
					error.addElement(requestedProps.get(name));
				continue;
			}
		}
		if (includeChildren) {
			List<DavResource> children = UrlNamespace.getChildren(ctxt, rs);
			for (DavResource child : children)
				addResourceToResponse(ctxt, child, top, nameOnly, requestedProps, includeChildren);
		}
	}
	
	private Element findPropstat(Element top, Map<Integer,Element> propstatMap, int status) {
		Element prop = propstatMap.get(status);
		if (prop == null) {
			prop = top.addElement(DavElements.E_PROPSTAT).addElement(DavElements.E_PROP);
			addStatusElement(prop, status);
			propstatMap.put(status, prop);
		}
		return prop;
	}
}
