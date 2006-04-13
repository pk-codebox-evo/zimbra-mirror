/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
* Creates a new AjxRpcRequest. The request object is an ActiveX object
* for IE, and an XMLHttpRequest object otherwise.
* @constructor
* @class
* This class represents an XML HTTP request, hiding differences between
* browsers. The internal request object depends on the browser.
*
* @param id		[string]	unique ID for this request
* @param ctxt	[_RpcCtxt]	owning context
**/
function AjxRpcRequest(id, ctxt) {
	if (!AjxRpcRequest._inited) {
		AjxRpcRequest._init();
    }

    this.id = id;
	this.ctxt = ctxt;
	if (AjxEnv.isIE) {
		this._httpReq = new ActiveXObject(AjxRpcRequest._msxmlVers);
	} else if (AjxEnv.isSafari || AjxEnv.isNav) {
		this._httpReq =  new XMLHttpRequest();
	}
}

AjxRpcRequest._inited = false;
AjxRpcRequest._msxmlVers = null;
AjxRpcRequest.TIMEDOUT = -1000;

AjxRpcRequest.prototype.toString = 
function() {
	return "AjxRpcRequest";
};

/**
* Sends this request to the target URL. If there is a callback, the request is
* performed asynchronously.
*
* @param requestStr		[string]		HTTP request string/document
* @param serverUrl		[string]		request target
* @param requestHeaders	[Array]*		HTTP request headers
* @param callback		[AjxCallback]*	callback (for async requests)
* @param useGet			[boolean]*		if true use GET method, else use POST
*/
AjxRpcRequest.prototype.invoke =
function(requestStr, serverUrl, requestHeaders, callback, useGet, timeout) {

	var asyncMode = (callback != null);
	
	// An exception here will be caught by AjxRpc.invoke
	this._httpReq.open((useGet) ? "get" : "post", serverUrl, asyncMode);

	if (asyncMode) {
		this._callback = callback;
        if(timeout) {
            var action = new AjxTimedAction(this, AjxRpcRequest._handleTimeout, {callback: callback, req: this});
            callback._timedActionId = AjxTimedAction.scheduleAction(action, timeout);
        }
        var tempThis = this;
		DBG.println(AjxDebug.DBG3, "Async RPC request");
		this._httpReq.onreadystatechange = function(ev) {AjxRpcRequest._handleResponse(tempThis, callback);};
	} else {
		// IE appears to run handler even on sync requests, so we need to clear it
		this._httpReq.onreadystatechange = function(ev) {};
	}

	if (requestHeaders) {
		for (var i in requestHeaders) {
			this._httpReq.setRequestHeader(i, requestHeaders[i]);
			DBG.println(AjxDebug.DBG3, "Async RPC request: Add header " + i + " - " + requestHeaders[i]);
		}
	}
	
	this._httpReq.send(requestStr);
	if (asyncMode) {
		return this.id;
	} else {
		if (this._httpReq.status == 200) {
			return {text: this._httpReq.responseText, xml: this._httpReq.responseXML, success: true};
		} else {
			return {text: this._httpReq.responseText, xml: this._httpReq.responseXML, success: false, status: this._httpReq.status};
		}
	}
};

/*
* Handler that runs when an asynchronous request timesout.
*
* @param callback	[AjxCallback]		callback to run after timeout
*/
AjxRpcRequest._handleTimeout =
function(args) {
    DBG.println(AjxDebug.DBG3, "Async RPC request: _handleTimeout");
    args.req.cancel();
    args.callback.run( {text: null, xml: null, success: false, status: AjxRpcRequest.TIMEDOUT} );
};

/*
* Handler that runs when an asynchronous response has been received. It runs a
* callback to initiate the response handling.
*
* @param req		[AjxRpcRequest]		request that generated the response
* @param callback	[AjxCallback]		callback to run after response is received
*/
AjxRpcRequest._handleResponse =
function(req, callback) {
	if (!req) {
		// If IE receives a 500 error, the object reference can be lost
		DBG.println(AjxDebug.DBG1, "Async RPC request: Lost request object!!!");
		callback.run( {text: null, xml: null, success: false, status: 500} );
		return;
	}
    DBG.println(AjxDebug.DBG3, "Async RPC request: ready state = " + req._httpReq.readyState);
	if (req._httpReq.readyState == 4) {
        if(callback._timedActionId !== null) {
            AjxTimedAction.cancelAction(callback._timedActionId);
        }
		DBG.println(AjxDebug.DBG3, "Async RPC request: HTTP status = " + req._httpReq.status);
        if (req._httpReq.status == 200) {
			callback.run( {text: req._httpReq.responseText, xml: req._httpReq.responseXML, success: true} );				
		} else {
			callback.run( {text: req._httpReq.responseText, xml: req._httpReq.responseXML, success: false, status: req._httpReq.status} );
		}
		req.ctxt.busy = false;
	}
};

AjxRpcRequest.prototype.cancel =
function() {
	DBG.println(AjxDebug.DBG1, "Aborting HTTP request");
	this._httpReq.abort();
};

AjxRpcRequest._init =
function() {
	if (AjxEnv.isIE) {
		var msxmlVers = ["MSXML2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				// search for the xml version on user's machine
				var x = new ActiveXObject(msxmlVers[i]);
				AjxRpcRequest._msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
				// do nothing
			}
		}
		if (!AjxRpcRequest._msxmlVers) {
			throw new AjxException("MSXML not installed", AjxException.INTERNAL_ERROR, "AjxRpc._init");
        }
    }
	AjxRpcRequest._inited = true;
};