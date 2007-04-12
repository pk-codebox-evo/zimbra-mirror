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
 * @constructor
 * @class
 * DwtDragEvent is generated by the Drag and Drop framework when a drag operation is
 * in process. The drag event is dispatched to the registered <i>DwtDragSource</i>
 * instance
 * 
 * @author Ross Dargahi
 * 
 * @see DwtDragSource
 */
function DwtDragEvent() {
	/** Type of drag operation. One of: <ul>
	 * <li><i>DwtDragEvent.DRAG_START</i></li>
	 * <li><i>DwtDragEvent.SET_DATA</i></li>
	 * <li><i>DwtDragEvent.DRAG_END</i></li>
	 * </ul>
	 * @type number	*/
	this.operation = null;
	
	/** Drag source control
	 * @type DwtControl */
	this.srcControl = null;
	
	/**
	 * Action being performed. One of: <ul>
	 * <li><i>Dwt.DND_DROP_NONE</i></li>
	 * <li><i>Dwt.DND_DROP_COPY</i></li>
	 * <li><i>Dwt.DND_DROP_MOVE</i></li>
	 * </ul>
	 * @type number */
	this.action = null;
	
	/**
	 * Whether the DnD framework should perform the operation. The application is
	 * responsible for setting this value based on whatever business logic it is
	 * implementing
	 * @type boolean */
	this.doIt = false;
	
	/* Drag source data. This is the application data associated with the item being
	 * dragged
	 * @type any*/
	this.srcData = null;
}

/** Drag is starting
 * @type number*/
DwtDragEvent.DRAG_START = 1;

/** Set the <code>srcData</code> field of the event
 * @type number*/
DwtDragEvent.SET_DATA = 2;

/** Drag has ended
 * @type number*/
DwtDragEvent.DRAG_END = 3;

/** Drag cancelled (i.e. dropped on invalid target)
 * @type number */
DwtDragEvent.DRAG_CANCEL = 4;
