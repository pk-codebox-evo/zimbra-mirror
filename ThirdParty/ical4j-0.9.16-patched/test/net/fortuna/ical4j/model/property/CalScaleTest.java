/*
 * Created on 16/03/2005
 *
 * $Id: CalScaleTest.java,v 1.1 2005/03/18 05:33:59 fortuna Exp $
 *
 * Copyright (c) 2005, Ben Fortuna
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.fortuna.ical4j.model.property;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.parameter.Value;

/**
 * @author Ben
 *
 * Tests related to the property CALSCALE
 */
public class CalScaleTest extends TestCase {

    /*
     * Test that the constant GREGORIAN is immutable.
     */
    public void testGregorianImmutable() {
        try {
            CalScale.GREGORIAN.getParameters().add(Value.DATE);
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
        
        try {
            CalScale.GREGORIAN.setValue("LUNAR");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
    }

}
