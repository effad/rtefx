//    RTEFX - Rich Text Editor for JavaFX
//    Copyright (C) 2003, 2018  Robert Lichtenberger
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
package org.rtefx.plugins.brmatcher;

import org.rtefx.plugins.brmatcher.REDBracketMatcherDefinition;
import org.rtefx.plugins.brmatcher.REDBracketMatcherManager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** Regression test for REDBracketMatcherManager
  * @author rli@chello.at
  * @tier test
  */
public class RTestREDBracketMatcherManager extends TestCase {
	public RTestREDBracketMatcherManager(String name) {
		super(name);
	}
	
	public void testDefinitionManagement() {
		assertEquals(null, REDBracketMatcherManager.createMatcher("maynotexist"));
		assertTrue(REDBracketMatcherManager.createMatcher("C++") != null);
		assertTrue(REDBracketMatcherManager.createMatcher("Java") != null);
		REDBracketMatcherDefinition def1 = new REDBracketMatcherDefinition();
		def1.setName("foo");
		REDBracketMatcherManager.addDefinition(def1);
		assertNotNull(REDBracketMatcherManager.createMatcher("foo"));
	}
		
	public static Test suite() {
		return new TestSuite(RTestREDBracketMatcherManager.class);
	}
}
