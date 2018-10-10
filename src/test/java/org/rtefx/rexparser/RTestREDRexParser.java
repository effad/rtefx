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
 
package org.rtefx.rexparser;

import junit.framework.*;
import org.rtefx.*;
import java.util.*;

/** Regression test for RED regular expression parser.
  * @author rli@chello.at
  * @tier test
  */
public class RTestREDRexParser extends TestCase implements REDRexParserStopper {
	public RTestREDRexParser(String name) {
		super(name);
	}
	
	public void setUp() {
		fParser = new REDRexParser();
		fEditor = new REDEditor();
	}
	
	/** Tests the (simple) state management */
	public void testState() {
		assertEquals(0, fParser.defaultState());
		assertEquals(1, fParser.createState());
		assertEquals(2, fParser.createState());
		assertEquals(3, fParser.createState());
	}
	
	class LogEntry {
		int line, from, to;
		Object emitObj;
	}

	public class TestAction extends REDRexAction {
		public void patternMatch(REDRexParser parser, int line, REDRexParserMatch match) {
			assertSame(parser, fParser);
			LogEntry entry = new LogEntry(); entry.line = line; entry.from = match.getStart(0); 
			entry.to = match.getEnd(0); entry.emitObj = match.getEmitObj();
			fLog.add(entry);
		}
		
		String getLog() {
			StringBuffer buf = new StringBuffer();
			Iterator iter = fLog.iterator();
			while (iter.hasNext()) {
				LogEntry e = (LogEntry) iter.next();
				buf.append(e.line + "/" + e.from + "/" + e.to + "/" + e.emitObj + ", ");
			}
			return new String(buf);
		}
		
		void clearLog() {
			fLog = new ArrayList();
		}
		
		ArrayList fLog = new ArrayList();
	}

	public void testRules() throws REDRexMalformedPatternException {
		TestAction act = new TestAction();
		fEditor.replace("yabba dabba doobba", 0, 0, null);

		fParser.addRule(fParser.defaultState(), "ab", true, fParser.defaultState(), "X", act, false);
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/1/3/X, 0/7/9/X, ", act.getLog()); act.clearLog();

		fParser.addRule(fParser.defaultState(), "bba", true, fParser.defaultState(), "Y", act, false);
		fParser.parse(fEditor.getLineSource());		
		assertEquals("0/1/3/X, 0/7/9/X, 0/15/18/Y, ", act.getLog()); act.clearLog();
		
		fParser.addRule(fParser.defaultState(), "o+", true, fParser.defaultState(), "Z", act, false);
		fParser.parse(fEditor.getLineSource());		
		assertEquals("0/1/3/X, 0/7/9/X, 0/13/15/Z, 0/15/18/Y, ", act.getLog()); act.clearLog();
	}
	
	public void testReverse() throws REDRexMalformedPatternException {
		TestAction act = new TestAction();
		fEditor.replace("yabba dabba doobba", 0, 0, null);

		fParser.addRule(fParser.defaultState(), "ab", true, fParser.defaultState(), "X", act, false);
		fParser.parse(fEditor.getLineSource(), 0, fParser.defaultState(), null, true);
		assertEquals("0/7/9/X, 0/1/3/X, ", act.getLog()); act.clearLog();

		fParser.addRule(fParser.defaultState(), "bba", true, fParser.defaultState(), "Y", act, false);
		fParser.parse(fEditor.getLineSource(), 0, fParser.defaultState(), null, true);
		assertEquals("0/15/18/Y, 0/8/11/Y, 0/2/5/Y, ", act.getLog()); act.clearLog();
	}

	
	public void testStates() throws REDRexMalformedPatternException {
		TestAction act = new TestAction();
		fEditor.replace("foo = 'foo is cool'; // do it", 0, 0, null);

		fParser.addRule(fParser.defaultState(), "o+", true, fParser.defaultState(), "X", act, false);
		assertEquals(1, fParser.getNrOfRules());
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/1/3/X, 0/8/10/X, 0/15/17/X, 0/25/26/X, ", act.getLog()); act.clearLog();
		
		fParser.addRule(fParser.defaultState(), "i", true, fParser.defaultState(), "I", act, false);
		assertEquals(2, fParser.getNrOfRules());
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/1/3/X, 0/8/10/X, 0/11/12/I, 0/15/17/X, 0/25/26/X, 0/27/28/I, ", act.getLog()); act.clearLog();
		
		int stringState = fParser.createState();
		fParser.addRule(fParser.defaultState(), "'", true, stringState, "SS", act, false);
		fParser.addRule(stringState, "'", true, fParser.defaultState(), "SE", act, false);
		assertEquals(4, fParser.getNrOfRules());
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/1/3/X, 0/6/7/SS, 0/18/19/SE, 0/25/26/X, 0/27/28/I, ", act.getLog()); act.clearLog();
		
		fParser.addRule(stringState, "%'", true, stringState, "Q", act, false);
		assertEquals(5, fParser.getNrOfRules());
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/1/3/X, 0/6/7/SS, 0/18/19/SE, 0/25/26/X, 0/27/28/I, ", act.getLog()); act.clearLog();
		
		fEditor.replace("%'", 8, 8, null);
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/1/3/X, 0/6/7/SS, 0/8/10/Q, 0/20/21/SE, 0/27/28/X, 0/29/30/I, ", act.getLog()); act.clearLog();
		
		fParser.clearRules();
		assertEquals(0, fParser.getNrOfRules());
	}
	
	public void testMultiline() throws REDRexMalformedPatternException {
		TestAction act = new TestAction();
		fEditor.replace("Don't meddle in the affairs of wizards,\nfor they are subtle and quick to anger.", 0, 0, null);
		fParser.addRule(fParser.defaultState(), "n", true, fParser.defaultState(), "N", act, false);
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/2/3/N, 0/14/15/N, 1/21/22/N, 1/34/35/N, ", act.getLog()); act.clearLog();
	}
	
	public boolean mustStop(REDRexParser parser, int line, int offset, int state) {
		return line > 1;
	}

	public void testStopper() throws REDRexMalformedPatternException {
		TestAction act = new TestAction();
		fEditor.replace("aaa\naaa\naaa", 0, 0, null);
		fParser.addRule(fParser.defaultState(), "a+", true, fParser.defaultState(), "A", act, false);
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/0/3/A, 1/0/3/A, 2/0/3/A, ", act.getLog()); act.clearLog();
		fParser.parse(fEditor.getLineSource(), 0, fParser.defaultState(), this, false);
		assertEquals("0/0/3/A, 1/0/3/A, ", act.getLog()); act.clearLog();
	}

	public void testClientProperties() {
		assertEquals(null, fParser.getClientProperty("foo"));
		fParser.putClientProperty("foo", "bar");
		assertEquals("bar", fParser.getClientProperty("foo"));
	}
	
	/** Tests greediness and order of rules 
	  * <UL>
	  * <LI>If two rules match the one more to the left is taken
	  * <LI>If both rules start at the same position the longer match is taken
	  * <LI>If both rules match over the same length, the rule inserted first is taken
	  * </UL>
	  */
	public void testGreedy() throws REDRexMalformedPatternException {
		TestAction act = new TestAction();
		fEditor.replace("boolean privateCopy", 0, 0, null);
		fParser.addRule(fParser.defaultState(), "private", true, fParser.defaultState(), "KW", act, false);
		fParser.addRule(fParser.defaultState(), "[A-Za-z0-9_]+", true, fParser.defaultState(), "NW", act, false);
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/0/7/NW, 0/8/19/NW, ", act.getLog()); act.clearLog();
		fEditor.replace("boolean private", 0, fEditor.length(), null);
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/0/7/NW, 0/8/15/KW, ", act.getLog()); act.clearLog();
	}
	
	public void testCaseSensitive() throws REDRexMalformedPatternException {
		TestAction act = new TestAction();
		fEditor.replace("AbrakAdabRA", 0, 0, null);
		fParser.addRule(fParser.defaultState(), "a", false, fParser.defaultState(), "A", act, false);
		fParser.parse(fEditor.getLineSource());
		assertEquals("0/0/1/A, 0/3/4/A, 0/5/6/A, 0/7/8/A, 0/10/11/A, ", act.getLog()); act.clearLog();
	}
	
	public void testMalformedPatternException() {
		try {
			fParser.addRule(fParser.defaultState(), "a[bc", false, fParser.defaultState(), "C", null, false);
			assertTrue("Exception not raised as expected", false);
		}
		catch (REDRexMalformedPatternException mpe) {
		}
	}
		
	public static Test suite() {
		return new TestSuite(RTestREDRexParser.class);
	}
	
	REDRexParser fParser;
	REDEditor fEditor;
}
