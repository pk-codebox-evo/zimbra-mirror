/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

ZmMobileDevicesPage = function(parent, section, controller) {
	ZmPreferencesPage.apply(this, arguments);

	this._deviceController = controller.getMobileDevicesController();
};

ZmMobileDevicesPage.prototype = new ZmPreferencesPage;
ZmMobileDevicesPage.prototype.constructor = ZmMobileDevicesPage;

ZmMobileDevicesPage.prototype.toString =
function () {
    return "ZmMobileDevicesPage";
};


// ZmPreferencesPage methods

ZmMobileDevicesPage.prototype.showMe =
function() {
	ZmPreferencesPage.prototype.showMe.apply(this, arguments);

	if (!this._rendered) {
		var params = {
			parent:this,
			buttons:this._deviceController.getToolbarButtons(),
			posStyle:Dwt.STATIC_STYLE,
			context:ZmId.VIEW_MOBILE_DEVICES,
			parentElement:(this._htmlElId+"_deviceToolbar")
		};
		this._toolbar = new ZmButtonToolBar(params);

		params = {
			parent:this,
			parentElement:(this._htmlElId+"_deviceList")
		};
		this.listView = new ZmMobileDeviceListView(params);
		this.listView.setMultiSelect(false);

		this._deviceController.initialize(this._toolbar, this.listView);
		this._rendered = true;
	}

	this._deviceController.loadDeviceInfo();
};

ZmMobileDevicesPage.prototype.reset =
function(useDefaults) {
	ZmPreferencesPage.prototype.reset.apply(this, arguments);

	if (this._controller.getTabView().getActiveView() == this) {
		this._deviceController.loadDeviceInfo();
	}
};


/**
 * A list view that displays user's mobile devices. The data is in the form of a
 * list of ZmDevices.
 *
 * @param params	[hash]			hash of params:
 */
ZmMobileDeviceListView = function(params) {

	params.headerList = this._getHeaderList();
	DwtListView.call(this, params);
};

ZmMobileDeviceListView.prototype = new DwtListView;
ZmMobileDeviceListView.prototype.constructor = ZmMobileDeviceListView;


// Consts

ZmMobileDeviceListView.F_DEVICE			= "de";
ZmMobileDeviceListView.F_STATUS			= "st";
ZmMobileDeviceListView.F_ID				= "id";
ZmMobileDeviceListView.F_PROTOCOL		= "pr";
ZmMobileDeviceListView.F_PROVISIONABLE	= "pv";


// Public methods

ZmMobileDeviceListView.prototype.toString =
function() {
	return "ZmMobileDeviceListView";
};

ZmMobileDeviceListView.prototype._getHeaderList =
function() {

	var headerList = [];
	headerList.push(new DwtListHeaderItem({field:ZmMobileDeviceListView.F_DEVICE, text:ZmMsg.mobileDevice}));
	headerList.push(new DwtListHeaderItem({field:ZmMobileDeviceListView.F_ID, text:ZmMsg.mobileDeviceId, width:ZmMsg.COLUMN_WIDTH_ID_MDL}));
	headerList.push(new DwtListHeaderItem({field:ZmMobileDeviceListView.F_STATUS, text:ZmMsg.status, width:ZmMsg.COLUMN_WIDTH_STATUS_MDL}));
	headerList.push(new DwtListHeaderItem({field:ZmMobileDeviceListView.F_PROTOCOL, text:ZmMsg.mobileProtocolVersion, width:ZmMsg.COLUMN_WIDTH_PROTOCOL_MDL}));
	headerList.push(new DwtListHeaderItem({field:ZmMobileDeviceListView.F_PROVISIONABLE, text:ZmMsg.mobileProvisionable, width:ZmMsg.COLUMN_WIDTH_PROVISIONABLE_MDL}));

	return headerList;
};

ZmMobileDeviceListView.prototype._getCellContents =
function(html, idx, item, field, colIdx, params) {

	if (field == ZmMobileDeviceListView.F_DEVICE) {
		html[idx++] = "<nobr>";
		html[idx++] = item.type;
		if (item.ua) {
			html[idx++] = " (";
			html[idx++] = item.ua;
			html[idx++] = ")";
		}
		html[idx++] = "</nobr>";
	} else if (field == ZmMobileDeviceListView.F_STATUS) {
		html[idx++] = item.getStatusString();
	} else if (field == ZmMobileDeviceListView.F_ID) {
		html[idx++] = item.id;
	} else if (field == ZmMobileDeviceListView.F_PROTOCOL) {
		html[idx++] = item.protocol;
	} else if (field == ZmMobileDeviceListView.F_PROVISIONABLE) {
		html[idx++] = item.provisionable ? AjxMsg.yes : AjxMsg.no;
	}

	return idx;
};
