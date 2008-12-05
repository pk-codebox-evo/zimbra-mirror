<%@ tag body-content="empty" %>
<%@ attribute name="context" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.tag.SearchContext"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlclient" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<!-- Dependencies -->
<script type="text/javascript" src="../yui/2.5.1/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../yui/2.5.1/animation/animation-debug.js"></script>

<!-- Drag and Drop source file -->
<script type="text/javascript" src="../yui/2.5.1/dragdrop/dragdrop-debug.js"></script>

<app:handleError>
    <zm:getMailbox var="mailbox"/>
    <app:searchTitle var="title" context="${context}"/>
    <c:set var="cid" value="${empty param.id ? context.searchResult.hits[0].id : param.id}"/>
    <fmt:message var="unknownRecipient" key="unknownRecipient"/>
    <fmt:message var="unknownSubject" key="noSubject"/>
    <c:set var="useTo" value="${context.folder.isSent or context.folder.isDrafts}"/>
    <c:set var="selectedRow" value="${param.selectedRow}"/>
    <c:set var="context" value="${context}" />
    <c:set var="csi" value="${param.csi}"/>
    <c:set var="idcheck" value="${not empty param.id ? param.id : context.currentItem.id}"/>
    <c:if test="${mailbox.prefs.readingPaneEnabled and not empty idcheck}">
        <zm:searchConv var="convSearchResult" id="${not empty param.cid ? param.cid : context.currentItem.id}" context="${context}" fetch="${empty csi ? 'first': 'none'}" markread="true" sort="${param.css}" />
        <c:if test="${empty csi}">
            <c:set var="csi" value="${convSearchResult.fetchedMessageIndex}"/>
            <c:if test="${csi ge 0}">
                <zm:getMessage var="msg" id="${convSearchResult.hits[csi].id}" markread="${(context.folder.isMountPoint and context.folder.effectivePerm eq 'r') ? 'false' : 'true'}" neuterimages="${mailbox.prefs.displayExternalImages ? '1' : param.xim}"/>
            </c:if>
        </c:if>
        <c:if test="${msg eq null}">
            <c:if test="${csi lt 0 or csi ge convSearchResult.size}">
                <c:set var="csi" value="0"/>
            </c:if>
            <zm:getMessage var="msg" id="${not empty param.id ? param.id : convSearchResult.hits[csi].id}" markread="${(context.folder.isMountPoint and context.folder.effectivePerm eq 'r') ? 'false' : 'true'}" neuterimages="${mailbox.prefs.displayExternalImages ? '1' : param.xim}"/>
        </c:if>
        <zm:computeNextPrevItem var="cursor" searchResult="${context.searchResult}" index="${context.currentItemIndex}"/>
        <c:set var="ads" value='${msg.subject} ${msg.fragment}'/>
    </c:if>
</app:handleError>
<app:view mailbox="${mailbox}" title="${title}" selected='mail' folders="true" tags="true" searches="true" context="${context}" keys="true">
<zm:currentResultUrl var="currentUrl" value="/h/search" context="${context}"/>
<form name="zform" action="${fn:escapeXml(currentUrl)}" method="post">
<table width="100%" cellpadding="0" cellspacing="0">
<tr>
    <td class='TbTop'>
        <app:convListViewToolbar context="${context}" keys="true"/>
    </td>
</tr>
<tr>
    <td>
        <table width="100%" cellpadding="0" cellspacing="0">
        <tr>
        <td class='List' valign="top" width="45%">
        <table width="100%" cellpadding="2" cellspacing="0">
            <tr class='Header'>
                <th class='CB' nowrap='nowrap'><input id="OPCHALL" onClick="checkAll(document.zform.id,this)" type="checkbox" name="allids"/></th>
                <th><fmt:message key="arrangedBy"/>: <fmt:message key="date"/></th>
                <th width="1%" nowrap><app:img src="startup/ImgAttachment.gif" altkey="ALT_ATTACHMENT"/>
            </tr>
        </table>
        <table width="100%" cellpadding="2" cellspacing="0">
            <tbody id="mess_list_tbody">
                <c:forEach items="${context.searchResult.hits}" var="hit" varStatus="status">
                    <c:set var="convHit" value="${hit.conversationHit}"/>
                    <c:choose>
                        <c:when test="${convHit.isDraft}">
                            <zm:currentResultUrl var="convUrl" value="search" index="${status.index}" context="${context}" usecache="true" id="${fn:substringAfter(convHit.id,'-')}" action="compose"/>
                        </c:when>
                        <c:otherwise>
                            <zm:currentResultUrl var="convUrl" value="search" cid="${hit.id}" action='view' index="${status.index}" context="${context}" usecache="true"/>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${empty selectedRow and convHit.id == context.currentItem.id}"><c:set var="selectedRow" value="${status.index}"/></c:if>
                    <c:set var="aid" value="A${status.index}"/>
                    <tr onclick='zSelectRow(event,"${aid}")' id="R${status.index}" class='${status.index mod 2 eq 1 ? 'ZhRowOdd' :'ZhRow'} ${convHit.isUnread ? ' Unread':''}${selectedRow eq status.index ? ' RowSelected' : ''}'>
                        <td class='CB' nowrap><input  id="C${status.index}" type="checkbox" name="id" value="${convHit.id}"></td>
                        <td><%-- allow this column to wrap --%>
                            <c:set var="dispRec" value="${zm:truncate(convHit.displayRecipients,20,true)}"/>${fn:escapeXml(empty dispRec ? unknownRecipient : dispRec)} &nbsp;&nbsp; <c:if test="${convHit.messageCount > 1}">(${convHit.messageCount})&nbsp;</c:if>
                            <br>
                            <a href="${fn:escapeXml(convUrl)}" id="${aid}">
                                <c:set var='subj' value="${empty convHit.subject ? unknownSubject : zm:truncate(convHit.subject,100,true)}"/>
                                <span class="Fragment"><c:out value="${zm:truncate(subj,45,true)}"/></span>
                                <c:if test="${mailbox.prefs.showFragments and not empty convHit.fragment and fn:length(subj) lt 90}">
                                    <!-- <span class='Fragment'> - <c:out value="${zm:truncate(convHit.fragment,100-fn:length(subj),true)}"/></span> -->
                                </c:if>
                            </a>
                            <c:if test="${convHit.id == context.currentItem.id}">
                                <zm:computeNextPrevItem var="cursor" searchResult="${context.searchResult}" index="${context.currentItemIndex}"/>
                                <c:if test="${cursor.hasPrev}">
                                    <zm:prevItemUrl var="prevItemUrl" value="search" cursor="${cursor}" context="${context}" usecache="true"/>
                                    <a href="${fn:escapeXml(prevItemUrl)}" id="PREV_ITEM"></a>
                                </c:if>
                                <c:if test="${cursor.hasNext}">
                                    <zm:nextItemUrl var="nextItemUrl" value="search" cursor="${cursor}" context="${context}" usecache="true"/>
                                    <a href="${fn:escapeXml(nextItemUrl)}" id="NEXT_ITEM"></a>
                                </c:if>
                            </c:if>
                        </td>
                        <td nowrap align="right">
                                ${fn:escapeXml(zm:displayMsgDate(pageContext, convHit.date))}
                            <br>
                            <c:if test="${mailbox.features.mailPriority}">
                                <app:priorityImage high="${convHit.isHighPriority}" low="${convHit.isLowPriority}"/>
                            </c:if>
                            <c:if test="${mailbox.features.tagging}">
                                <app:miniTagImage ids="${convHit.tagIds}"/>
                            </c:if>
                            <c:if test="${mailbox.features.flagging}">
                            <app:flagImage flagged="${convHit.isFlagged}"/>
                        </c:if>
                        </td>
                        <td class='Img'><app:attachmentImage attachment="${convHit.hasAttachment}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${context.searchResult.size == 0}">
            <div class='NoResults'><fmt:message key="noResultsFound"/></div>
        </c:if>
     </td>
     <c:if test="${mailbox.prefs.readingPaneEnabled and not empty msg}">
         <td class='ZhAppColContent' valign="top" width="55%">
             <table width="100%" cellpadding="2" cellspacing="0" class='List'>
                 <tr class='Header'>
                     <!-- <th class='CB' nowrap='nowrap'><input id="OPCHCOLALL" onClick="checkAll(document.zform.idcv,this)" type="checkbox" name="allids"/></th> -->
                     <th><fmt:message key="arrangedBy"/>: <fmt:message key="date"/></th>
                     <th width="1%" nowrap><app:img src="startup/ImgAttachment.gif" altkey="ALT_ATTACHMENT"/>
                 </tr>
             </table>
             <table width=100% cellpadding=0 cellspacing=0>
                 <tr valign="top">
                     <td class=List>
                         <table width=100% height=100% cellpadding=0 cellspacing=0>
                             <c:forEach items="${convSearchResult.hits}" var="hit" varStatus="status">
                                 <zm:currentResultUrl var="msgUrl" value="search" action="view2" context="${context}" cso="${convSearchResult.offset}" csi="${status.index}" css="${param.css}"/>

                                 <tr class='ZhRow${(hit.messageHit.isUnread and (hit.id != msg.id)) ? ' Unread':''}${hit.id eq msg.id ? ' RowSelected' : ((context.showMatches and hit.messageHit.messageMatched) ? ' RowMatched' : '')}'>
                                    <!-- <td class='CB' nowrap><input <c:if test="${hit.id eq msg.id}">checked</c:if> type=checkbox name="idcv" value="${hit.id}"/></td> -->
                                    <td class='MsgStatusImg' align="center"><app:img src="${hit.messageHit.statusImage}" altkey='${hit.messageHit.statusImageAltKey}'/></td>

                                    <td><%-- allow wrap --%>
                                        <a href="${msgUrl}">${fn:escapeXml(hit.messageHit.displaySender)}</a>
                                        <br>
                                        <a href="${fn:escapeXml(msgUrl)}" id="A${status.index}">
                                            <c:if test="${mailbox.prefs.showFragments and not empty hit.messageHit.fragment}">
                                                <span class='Fragment'>${fn:escapeXml(empty hit.messageHit.fragment ? noFragment : zm:truncate(hit.messageHit.fragment,50, true))}</span>
                                            </c:if>
                                        </a>
                                    </td>
                                    <td nowrap align="right">
                                       ${fn:escapeXml(zm:displayMsgDate(pageContext, hit.messageHit.date))}
                                       <br>
                                        <c:if test="${mailbox.features.mailPriority}">
                                           <app:priorityImage high="${hit.messageHit.isHighPriority}" low="${hit.messageHit.isLowPriority}"/>
                                        </c:if>
                                        <c:if test="${mailbox.features.tagging}">
                                           <app:miniTagImage ids="${hit.messageHit.tagIds}"/>
                                        </c:if>
                                        <c:if test="${mailbox.features.flagging}">
                                            <app:flagImage flagged="${hit.messageHit.isFlagged}"/>
                                        </c:if>
                                    </td>
                                    <td class='Img'><app:attachmentImage attachment="${hit.messageHit.hasAttachment}"/></td>
                                </tr>
                             </c:forEach>
                         </table>
                     </td>
                 </tr>
                 <tr>
                     <td class='ZhAppContent2' valign="top">
                         <c:set var="extImageUrl" value=""/>
                         <c:if test="${empty param.xim}">
                             <zm:currentResultUrl var="extImageUrl" value="search" action="view" context="${context}" xim="1"/>
                         </c:if>
                         <zm:currentResultUrl var="composeUrl" value="search" context="${context}"
                                              action="compose" paction="view" id="${msg.id}"/>
                         <zm:currentResultUrl var="newWindowUrl" value="message" context="${context}" id="${msg.id}"/>
                         <app:displayMessage mailbox="${mailbox}" message="${msg}"externalImageUrl="${extImageUrl}" showconvlink="true" composeUrl="${composeUrl}" newWindowUrl="${newWindowUrl}"/>
                     </td>
                 </tr>
             </table>
         </td>
    </c:if>
        </tr>
        </table>
    </td>
</tr>

<tr>
    <td class='TbBottom'>
        <app:convListViewToolbar context="${context}" keys="false"/>
    </td>
</tr>
</table>
<input type="hidden" name="doConvListViewAction" value="1"/>
<input type="hidden" name="crumb" value="${fn:escapeXml(mailbox.accountInfo.crumb)}"/>
<input id="sr" type="hidden" name="selectedRow" value="${empty selectedRow ? 0 : zm:cook(selectedRow)}"/>

</form>

<SCRIPT TYPE="text/javascript">
    <!--
    var zrc = ${context.searchResult.size};
    var zsr = ${zm:cookInt(selectedRow, 0)};
    var zss = function(r,s) {
        var e = document.getElementById("R"+r);
        if (e == null) return;
        if (s) {
            if (e.className.indexOf(" RowSelected") == -1) e.className = e.className + " RowSelected";
            var e2 = document.getElementById("sr"); if (e2) e2.value = r;
        }
        else { if (e.className.indexOf(" RowSelected") != -1) e.className = e.className.replace(" RowSelected", "");}
    }
    var zsn = function() {if (zrc == 0 || (zsr+1 == zrc)) return; zss(zsr, false); zss(++zsr, true);}
    var zsp = function() {if (zrc == 0 || (zsr == 0)) return; zss(zsr, false); zss(--zsr, true);}
    var zos = function() {if (zrc == 0) return; var e = document.getElementById("A"+zsr); if (e && e.href) window.location = e.href;}
    var zcs = function(c) {if (zrc == 0) return; var e = document.getElementById("C"+zsr); if (e) e.checked = c ? c : !e.checked;}
    var zcsn = function () { zcs(true); zsn(); }
    var zcsp = function () { zcs(true); zsp(); }
    var zclick = function(id) { var e2 = document.getElementById(id); if (e2) e2.click(); }
    var zaction = function(a) { var e = document.getElementById(a); if (e) { e.selected = true; zclick("SOPGO"); }}
    var zmove = function(a) { var e = document.getElementById(a); if (e) { e.selected = true; zclick("SOPMOVE"); }}
    var zunflag = function() { zaction("OPUNFLAG"); }
    var zflag = function() { zaction("OPFLAG"); }
    var zread = function() { zaction("OPREAD"); }
    var zunread = function() { zaction("OPUNREAD"); }
    var zjunk = function() { zclick("SOPSPAM"); }
    function zSelectRow(ev,id) {var t = ev.target || ev.srcElement;if (t&&t.nodeName != 'INPUT'){var a = document.getElementById(id); if (a) window.location = a.href;} }

    var zprint = function(){
        try{
            var idex = 0;
            var c ="";
            while (idex <= zrc )
            {
                if(document.getElementById("C"+idex).checked) {
                    cid = document.getElementById("C"+idex).value;
                    c += cid + ",";
                }
                idex++ ;
            }
        }catch(ex){
        }
        window.open("/h/printconversations?id="+c);
    }

   //-->
</SCRIPT>

<app:keyboard cache="mail.convListView" globals="true" mailbox="${mailbox}" tags="true" folders="true">
    <c:if test="${mailbox.features.flagging}">
        <zm:bindKey message="mail.Flag" func="zflag"/>
        <zm:bindKey message="mail.UnFlag" func="zunflag"/>
    </c:if>
    <zm:bindKey message="mail.MarkRead" func="zread"/>
    <zm:bindKey message="mail.MarkUnread" func="zunread"/>
    <zm:bindKey message="mail.Spam" func="zjunk"/>
    <zm:bindKey message="mail.Delete" func="function() { zclick('SOPDELETE')}"/>
    <zm:bindKey message="global.CheckCheckBox" func="zcs"/>

    <zm:bindKey message="mail.GoToInbox" id="FLDR2"/>
    <zm:bindKey message="mail.GoToDrafts" id="FLDR6"/>
    <zm:bindKey message="mail.GoToSent" id="FLDR5"/>
    <zm:bindKey message="mail.GoToTrash" id="FLDR3"/>

    <zm:bindKey message="global.SelectAllCheckBoxes" func="function() { zclick('OPCHALL')}"/>
    <zm:bindKey message="conversation.Open" func="zos"/>
    <zm:bindKey message="global.CheckAndPreviousItem" func="zcsp"/>
    <zm:bindKey message="global.CheckAndNextItem" func="zcsn"/>
    <zm:bindKey message="global.PreviousItem" func="zsp"/>
    <zm:bindKey message="global.NextItem" func="zsn"/>
    <zm:bindKey message="global.PreviousPage" id="PREV_PAGE"/>
    <zm:bindKey message="global.NextPage" id="NEXT_PAGE"/>
    <c:if test="${mailbox.features.tagging}">
        <zm:bindKey message="global.Tag" func="function() {zaction('OPTAG{TAGID}')}" alias="tag"/>
    </c:if>
    <zm:bindKey message="mail.MoveToFolder" func="function() {zmove('OPFLDR{FOLDERID}')}" alias="folder"/>
</app:keyboard>
<script type="text/javascript">
(function() {

    var target = [], lastTarget = false;
    YAHOO.util.DDM.mode = YAHOO.util.DDM.INTERSECT;

    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $ = $D.get;
    //YAHOO.util.Event.onDOMReady(onReady)
    // setTimeout(onReady, 2000);

    function init() {
        var rowId, rowObj, rowNo, mesgId, endDr = false;

    <c:set var="ids" value="" />
    <zm:forEachFolder var="folder">
    <c:if test="${(folder.isConversationMoveTarget) and not context.folder.isDrafts}">
    <c:set var="ids" value="${ids}${folder.id}," />
    </c:if>

    </zm:forEachFolder>

        var ids_str = "${ids}";
        var ids  = ids_str.split(",");
        for(var i=0;i<ids.length; i++){
            if(ids[i] != ""){
                if ($D.get("folder_"+ids[i])) {
                    target[target.length] = new YAHOO.util.DDTarget("folder_"+ids[i]);
                }
            }
        }


        var tBody = $("mess_list_tbody");
        var drop = new YAHOO.util.DDProxy(tBody, 'default', { dragElId: "ddProxy", resizeFrame: false, centerFrame: false });

        drop.onMouseDown = function(ev) {
            /*get TR el. from event obj */
            var target = $E.getTarget(ev);
            var parentNode = target.parentNode;
            while (parentNode.nodeName != "TR"){
                parentNode = parentNode.parentNode;
            }
            rowId = parentNode.id;
            rowObj = parentNode;
            rowNo = rowId.substring(1);
            mesgId = document.getElementById("C"+rowNo).value;
            this.deltaY = 15;
            this.deltaX = (YAHOO.util.Event.getPageX(ev) - $D.getXY(document.getElementById(rowId))[0]);

        };

        drop.startDrag= function(){
            var dragEl = this.getDragEl();
            var clickEl = document.getElementById(rowId);
            /*proxy is a clone of row el. with few extra styles */
            dragEl.innerHTML = clickEl.innerHTML;

            $D.setStyle(dragEl, "color", $D.getStyle(clickEl, "color"));
            $D.setStyle(dragEl, "height", $D.getStyle(clickEl, "offsetHeight")+"px");
            $D.setStyle(dragEl, "width", "70%");
            $D.addClass(dragEl, "proxy");
        };

        drop.endDrag = function(){
            /* on proper drop dont animate it back to its place */
            if(!endDr){
                //var srcEl = this.getEl();
                var srcEl = document.getElementById(rowId);
                var proxy  = this.getDragEl();
                /* Show the proxy element and animate it to the src element's location */
                $D.setStyle(proxy, "visibility", "");
                var a = new YAHOO.util.Motion(
                        proxy, {
                    points: {
                        to: $D.getXY(srcEl)
                    }
                },0.6,YAHOO.util.Easing.easeOut )
                var proxyid = proxy.id;
                var thisid = this.id;

                /* Hide the proxy and show the source element when finished with the animation */
                a.onComplete.subscribe(function() {
                    $D.setStyle(proxyid, "visibility", "hidden");
                    $D.setStyle(thisid, "visibility", "");
                });
                a.animate();
            }
        };

        drop.onDragOver= function(ev, id){
            if (lastTarget) {
                $D.removeClass(lastTarget,'dragoverclass');
            }
            lastTarget = id[0].id;
            $D.addClass(lastTarget,'dragoverclass');
        };

        drop.onDragOut= function(ev, id){
            id = id[0].id;
            $D.removeClass(id,'dragoverclass');
        };

        drop.onDragDrop= function(ev, id){
            var proxyId  = this.getDragEl().id;
            id=id[0].id;
            /*remove class after a little delay to make user sure of wher he dropped*/
            window.setTimeout( function() { $D.removeClass(id,'dragoverclass'); }, 800 );
            $D.setStyle(proxyId, "visibility", "hidden");
            YAHOO.util.DragDropMgr.stopDrag(ev,true);

            endDr = true ;
            targId=id.split("_")[1];
            $("drag_target_folder").value="m:"+targId;
            $("drag_msg_id").value = mesgId;
            zclick('SOPMOVE');

        };
    }

    YAHOO.util.Event.addListener(window, 'load', init);

})();

</script>
</app:view>
