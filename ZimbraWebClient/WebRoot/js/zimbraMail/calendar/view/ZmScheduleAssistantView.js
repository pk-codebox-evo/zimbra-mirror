/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
 * Creates a left pane view for suggesting time/locations
 * @constructor
 * @class
 * This class displays suggested free time/location for sending invites to attendees
 *
 *  @author Sathishkumar Sugumaran
 *
 * @param parent			[ZmApptComposeView]			the appt compose view
 * @param controller		[ZmApptComposeController]	the appt compose controller
 */
ZmScheduleAssistantView = function(parent, controller, apptEditView) {

	DwtComposite.call(this, {parent: parent, posStyle: DwtControl.ABSOLUTE_STYLE});

	this._controller = controller;
	this._editView = apptEditView;

	this._rendered = false;
	this._kbMgr = appCtxt.getKeyboardMgr();

    this.type = ZmCalBaseItem.LOCATION;
    this._attendees = [];

    this.initialize();
};

ZmScheduleAssistantView.prototype = new DwtComposite;
ZmScheduleAssistantView.prototype.constructor = ZmScheduleAssistantView;

ZmScheduleAssistantView.ATTRS = {};
ZmScheduleAssistantView.ATTRS[ZmCalBaseItem.LOCATION] =
	["fullName", "email", "zimbraCalResLocationDisplayName",
	 "zimbraCalResCapacity", "zimbraCalResContactEmail", "notes", "zimbraCalResType"];

ZmScheduleAssistantView.prototype.initialize =
function() {
    appCtxt.getAppViewMgr().showTreeFooter(false);
    this._createWidgets();
};

ZmScheduleAssistantView.prototype.cleanup =
function() {
    this._attendees = [];
    this._timeFrame = null;

};

ZmScheduleAssistantView.prototype._createWidgets =
function() {
    this._createMiniCalendar();
    this._suggestBtn = new DwtButton({parent:this, style:DwtLabel.IMAGE_RIGHT, className: 'ZButton SuggestBtn'});
    this._suggestBtn.setImage("SelectPullDownArrow");
    this._suggestBtn.setSize('100%', Dwt.DEFAULT);
    this._suggestBtn.addSelectionListener(new AjxListener(this, this._suggestionListener));

    this._setSuggestionLabel();

    this._timeSuggestions = new ZmTimeSuggestionView(this, this._controller, this._editView);
};

ZmScheduleAssistantView.prototype._setSuggestionLabel =
function(date) {

    if(!this._suggestBtn) return;

    date = date || new Date();
    var dateStr = AjxDateUtil.computeDateStrNoYear(date);
    var dateLabel =  AjxMessageFormat.format(ZmMsg.suggestTimeLabel, [dateStr]);
    this._suggestBtn.setText(dateLabel);    
};

ZmScheduleAssistantView.prototype._createMiniCalendar =
function(date) {
	date = date ? date : new Date();

	var firstDayOfWeek = appCtxt.get(ZmSetting.CAL_FIRST_DAY_OF_WEEK) || 0;

    //todo: need to use server setting to decide the weekno standard
    var serverId = AjxTimezone.getServerId(AjxTimezone.DEFAULT);
    var useISO8601WeekNo = (serverId && serverId.indexOf("Europe")==0 && serverId != "Europe/London");

	this._miniCalendar = new DwtCalendar({parent: this, posStyle:DwtControl.RELATIVE_STYLE,
										  firstDayOfWeek: firstDayOfWeek, showWeekNumber: appCtxt.get(ZmSetting.CAL_SHOW_CALENDAR_WEEK), useISO8601WeekNo: useISO8601WeekNo});
	this._miniCalendar.setDate(date);
	this._miniCalendar.setScrollStyle(Dwt.CLIP);
	this._miniCalendar.addSelectionListener(new AjxListener(this, this._miniCalSelectionListener));
	this._miniCalendar.addDateRangeListener(new AjxListener(this, this._miniCalDateRangeListener));
	this._miniCalendar.setMouseOverDayCallback(new AjxCallback(this, this._miniCalMouseOverDayCallback));
	this._miniCalendar.setMouseOutDayCallback(new AjxCallback(this, this._miniCalMouseOutDayCallback));

	var workingWeek = [];
	for (var i = 0; i < 7; i++) {
		var d = (i + firstDayOfWeek) % 7;
		workingWeek[i] = (d > 0 && d < 6);
	}
	this._miniCalendar.setWorkingWeek(workingWeek);

	var app = appCtxt.getApp(ZmApp.CALENDAR);
	var show = app._active || appCtxt.get(ZmSetting.CAL_ALWAYS_SHOW_MINI_CAL);
	this._miniCalendar.setSkipNotifyOnPage(show && !app._active);
	if (!app._active) {
		this._miniCalendar.setSelectionMode(DwtCalendar.DAY);
	}
};

ZmScheduleAssistantView.prototype._suggestionListener =
function() {
    if(!this._resources) {
        this.searchCalendarResources(new AjxCallback(this, this._findFreeBusyInfo));
    }else {
        this._findFreeBusyInfo();
    }
    
};

ZmScheduleAssistantView.prototype._getTimeFrame =
function() {
	var di = {};
	ZmApptViewHelper.getDateInfo(this._editView, di);
	var startDate = this._date || AjxDateUtil.simpleParseDateStr(di.startDate);
    var endDate = new Date(startDate);
    endDate.setHours(23, 59, 0, 0);
    startDate.setHours(0, 0, 0, 0);
	return {start:startDate, end:endDate};
};

ZmScheduleAssistantView.prototype._miniCalSelectionListener =
function(ev) {
	if (ev.item instanceof DwtCalendar) {
        var date = ev.detail;
        this.reset(date, this._attendees);
        //set edit view start/end date
        var duration = this._editView.getDuration();
        var endDate = new Date(date.getTime() + duration);
        this._editView.setDate(date, endDate, true);
	}
};

ZmScheduleAssistantView.prototype.updateTime =
function(clearSelection) {
    if(clearSelection) this._date = null;
    var tf = this._getTimeFrame();
    this._miniCalendar.setDate(tf.start);
    this.reset(tf.start, this._attendees);    
};

ZmScheduleAssistantView.prototype.updateAttendees =
function(attendees) {

    if(attendees instanceof AjxVector) attendees = attendees.getArray();

    this._attendees = [];
    var attendee;
    for (var i = 0; i < attendees.length; i++) {
            attendee = attendees[i].getEmail();
            if (attendee instanceof Array) {
                attendee = attendee[i][0];
            }
            this._attendees.push(attendee);
    }
    this.reset(this._date, this._attendees);
};

ZmScheduleAssistantView.prototype.reset =
function(date, attendees) {
    this.resizeTimeSuggestions();
    var newKey = this.getFormKey(date, attendees);
    this._date = date;
    if(newKey != this._key) {
        this._setSuggestionLabel(date);
        if(this._timeSuggestions) this._timeSuggestions.removeAll();
    }
};

ZmScheduleAssistantView.prototype._miniCalDateRangeListener =
function(ev) {
    //todo: change scheduler suggestions
};

ZmScheduleAssistantView.prototype._miniCalMouseOverDayCallback =
function(control, day) {
	this._currentMouseOverDay = day;
    //todo: add code if tooltip needs to be supported
};

ZmScheduleAssistantView.prototype._miniCalMouseOutDayCallback =
function(control) {
	this._currentMouseOverDay = null;
};


//smart scheduler suggestion modules

ZmScheduleAssistantView.prototype.searchCalendarResources =
function(callback, sortBy) {
	var currAcct = this._editView.getCalendarAccount();
	var value = (this.type == ZmCalBaseItem.LOCATION) ? "Location" : "Equipment";
	var conds = [{attr: "zimbraCalResType", op: "eq", value: value}];
	var params = {
		sortBy: sortBy,
		offset: 0,
		limit: ZmContactsApp.SEARCHFOR_MAX,
		conds: conds,
		attrs: ZmScheduleAssistantView.ATTRS[this.type],
		accountName: appCtxt.isOffline ? currAcct.name : null
	};
	var search = new ZmSearch(params);
	search.execute({callback: new AjxCallback(this, this._handleResponseSearchCalendarResources, callback)});
};

ZmScheduleAssistantView.prototype._handleResponseSearchCalendarResources =
function(callback, result) {
	var resp = result.getResponse();
	var items = resp.getResults(ZmItem.RESOURCE).getVector();
    this._resources = (items instanceof AjxVector) ? items.getArray() : (items instanceof Array) ? items : [items];
    if(callback) callback.run();
};

ZmScheduleAssistantView.prototype._findFreeBusyInfo =
function() {

    var currAcct = this._editView.getCalendarAccount();
	// Bug: 48189 Don't send GetFreeBusyRequest for non-ZCS accounts.
	if (appCtxt.isOffline && (!currAcct.isZimbraAccount || currAcct.isMain)) {
        //todo: avoid showing smart scheduler button for non-ZCS accounts - offline client
        return;
	}

	var tf = this._timeFrame = this._getTimeFrame();
	var list = this._resources;
	var emails = [];
	var itemsById = {};
	for (var i = list.length; --i >= 0;) {
		var item = list[i];
		emails[i] = item.getEmail();

		// bug: 30824 - Don't list all addresses/aliases of a resource in
		// GetFreeBusyRequest.  One should suffice.
		if (emails[i] instanceof Array) {
			emails[i] = emails[i][0];
		}

		itemsById[emails[i]] = item;
		item.__fbStatus = { txt: ZmMsg.unknown };
	}

    var attendees = this._controller.getAttendees(ZmCalBaseItem.PERSON).getArray();
    var attendee;
    this._attendees = [];
    for (var i = 0; i < attendees.length; i++) {
            attendee = attendees[i].getEmail();
            if (attendee instanceof Array) {
                attendee = attendee[i][0];
            }
            itemsById[attendee] = attendees[i];
            emails.push(attendee);
            this._attendees.push(attendee);
    }

    this._key = this.getFormKey(tf.start, this._attendees);

    if(this._attendees.length == 0) {
        this.resizeTimeSuggestions();
        this._timeSuggestions.setNoResultsHtml();
        return;
    }

	if (this._freeBusyRequest) {
		appCtxt.getRequestMgr().cancelRequest(this._freeBusyRequest, null, true);
	}
	this._freeBusyRequest = this._controller.getFreeBusyInfo(tf.start.getTime(),
															 tf.end.getTime(),
															 emails.join(","),
															 new AjxCallback(this, this._handleResponseFreeBusy, [itemsById]),
															 null,
															 true);
};

ZmScheduleAssistantView.prototype.getFormKey =
function(startDate, attendees) {
    return startDate.getTime() + "-" + attendees.join(",");    
};

ZmScheduleAssistantView.prototype._handleResponseFreeBusy =
function(itemsById, result) {

    this._freeBusyRequest = null;

    this._schedule = {};

	var args = result.getResponse().GetFreeBusyResponse.usr;
    for (var i = 0; i < args.length; i++) {
		var usr = args[i];
        var id = usr.id;
        if (!id) {
            continue;
        }
        this._schedule[id] = usr;                
    };

    var startDate = this._timeFrame.start;
    startDate.setHours(0, 0, 0, 0);
    var startTime = startDate.getTime();
    var endDate = new Date(startTime);
    endDate.setHours(23, 59, 0, 0);
    var endTime = endDate.getTime();
    var duration = this._editView.getDuration();

    this._fbStat = new AjxVector();
    this._fbStatMap = {};
    this._totalUsers = this._attendees.length;
    this._totalLocations =  this._resources.length;

    while(startTime < endTime) {
        this.computeAvailability(startTime, startTime + duration);
        startTime += AjxDateUtil.MSEC_PER_HALF_HOUR;
    }

    this._fbStat.sort(ZmScheduleAssistantView._slotComparator);
    DBG.dumpObj(this._fbStat);
    this.renderSuggestions(itemsById);
};

ZmScheduleAssistantView.prototype.computeAvailability =
function(startTime, endTime, fbStat, fbStatMap) {

    var key = this.getKey(startTime, endTime);
    var fbInfo;

    if(this._fbStatMap[key]) {
        fbInfo = this._fbStatMap[key];
    }else {
        fbInfo = {
            startTime: startTime,
            endTime: endTime,
            availableUsers: 0,
            availableLocations: 0,
            totalUsers: this._attendees.length,
            totalLocations:  this._resources.length,            
            attendees: {},
            locations: {}
        };
    }

    for(var i=0; i < this._attendees.length; i++) {
        var attendee = this._attendees[i];
        var sched = this._schedule[attendee];
        var isFree = true;
        if(sched.b) isFree = isFree && this.isBooked(sched.b, startTime, endTime);
        if(sched.t) isFree = isFree && this.isBooked(sched.t, startTime, endTime);
        if(sched.u) isFree = isFree && this.isBooked(sched.u, startTime, endTime);
        var key = startTime + "-" + endTime;

        fbInfo.attendees[attendee] = isFree;
        if(isFree) fbInfo.availableUsers++;
    }

    var list = this._resources;
	for (var i = list.length; --i >= 0;) {
		var item = list[i];
		var resource = item.getEmail();

		if (resource instanceof Array) {
			resource = resource[0];
		}
        var sched = this._schedule[resource];
        var isFree = true;
        if(sched.b) isFree = isFree && this.isBooked(sched.b, startTime, endTime);
        if(sched.t) isFree = isFree && this.isBooked(sched.t, startTime, endTime);
        if(sched.u) isFree = isFree && this.isBooked(sched.u, startTime, endTime);

        fbInfo.locations[resource] = isFree;
        if(isFree) fbInfo.availableLocations++;
	}

    this._fbStat.add(fbInfo);
    this._fbStatMap[key] = fbInfo;
};

ZmScheduleAssistantView._slotComparator =
function(slot1, slot2) {
	if(slot1.availableUsers < slot2.availableUsers) {
        return 1;
    }else if(slot1.availableUsers > slot2.availableUsers) {
        return -1;
    }else {
        return slot1.availableLocations < slot2.availableLocations ? 1 : (slot1.availableLocations > slot2.availableLocations ? -1 : 0);
    }
};

ZmScheduleAssistantView.prototype.getKey =
function(startTime, endTime) {
    return startTime + "-" + endTime;
};

ZmScheduleAssistantView.prototype.isBooked =
function(slots, startTime, endTime) {
    for (var i = 0; i < slots.length; i++) {
        var startConflict = startTime >= slots[i].s && startTime < slots[i].e;
        var endConflict = endTime >= slots[i].s && endTime < slots[i].e;
        if(startConflict || endConflict) {
            return false;
        }
    };
    return true;
};

ZmScheduleAssistantView.prototype.renderSuggestions =
function(itemsById) {
    this.resizeTimeSuggestions();
    this._timeSuggestions.set(this._fbStat, itemsById);
};

ZmScheduleAssistantView.prototype.resizeTimeSuggestions =
function() {

    if(!this._timeSuggestions) return;

    var calSize = Dwt.getSize(this._miniCalendar.getHtmlElement());
    var btnSize = Dwt.getSize(this._suggestBtn.getHtmlElement());
    var contSize = Dwt.getSize(this.getHtmlElement());
    var newHeight = contSize.y - btnSize.y - calSize.y -2;
    this._timeSuggestions.setSize('100%', newHeight);
};