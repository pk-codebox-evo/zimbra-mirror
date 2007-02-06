<%--
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.2

The contents of this file are subject to the Zimbra Public License
Version 1.2 ("License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
the License for the specific language governing rights and limitations
under the License.

The Original Code is: Zimbra Collaboration Suite Web Client

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
All Rights Reserved.

Contributor(s):

***** END LICENSE BLOCK *****
--%>
<%
    String contextPath = request.getContextPath();
    if (contextPath.equals("/")) contextPath = "";

    String mode = (String)request.getAttribute("mode");
    boolean inDevMode = mode != null && mode.equals("mjsf");

    String vers = (String)request.getAttribute("version");
    if (vers == null) vers = "";

    String ext = (String)request.getAttribute("fileExtension");
    if (ext == null) ext = "";
%>
<!-- bootstrap classes -->
<script type="text/javascript" src='<%=contextPath%>/js/Boot_all.js<%=ext%>?v=<%=vers%>'></script>

<script type="text/javascript">
AjxPackage.setBasePath("<%=contextPath%>/js");
AjxPackage.setExtension("<%= inDevMode ? "" : "_all" %>.js<%=ext%>");
AjxPackage.setQueryString("v=<%=vers%>");
</script>
