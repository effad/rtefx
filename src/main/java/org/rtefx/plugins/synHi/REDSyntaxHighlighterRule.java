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
 
package org.rtefx.plugins.synHi;

import java.util.LinkedList;

import javax.swing.SwingUtilities;

import org.rtefx.REDEditor;
import org.rtefx.REDStyle;
import org.rtefx.REDStyleManager;
import org.rtefx.rexparser.REDRexMalformedPatternException;
import org.rtefx.rexparser.REDRexParser;
import org.rtefx.util.REDTracer;
import org.rtefx.xml.REDXMLCallbackError;
import org.rtefx.xml.REDXMLHandlerReader;
import org.rtefx.xml.REDXMLReadable;

/** Rule base class. 
  * @author rli@chello.at
  * @tier system
  */
abstract public class REDSyntaxHighlighterRule implements REDXMLReadable {
	public REDSyntaxHighlighterRule() {
	}

	public void setMappings(REDXMLHandlerReader handler) throws REDXMLCallbackError {
		handler.mapEnd("Style", "setStyle(#)");
	}
	
	public void innerProduction(REDXMLReadable obj, REDXMLHandlerReader inner, REDXMLHandlerReader outer) {
		REDTracer.warning("org.rtefx.plugins.synHi", "REDSyntaxHighlighterRule", "Inner production ignored: " + obj);
	}
	
	public void setStyle(String style) {
		REDTracer.info("org.rtefx.plugins.synHi", "REDSyntaxHighlighterRule", "Style set: " + style);
		if (!REDStyleManager.hasStyle(style)) {
			REDTracer.warning("org.rtefx.plugins.synHi", "REDSyntaxHighlighterRule", "No style named '" + style + "' found. Using default style.");
		}
		fStyle = REDStyleManager.getStyle(style);
	}

	abstract void installInParser(int state, boolean caseSensitive, REDRexParser parser, REDStyle envStyle) throws REDRexMalformedPatternException;

	static void updateLastLit(REDRexParser parser, int line, int updateTo, int newUpdatePoint) {
		REDEditor editor = (REDEditor) parser.getClientProperty("editor");
		REDStyle style = (REDStyle) parser.getClientProperty("envStyle");
		REDSyntaxHighlighterPosition pos = (REDSyntaxHighlighterPosition) parser.getClientProperty("lastLit");
		int lineStart = editor.getLineStart(line);
		LinkedList list = (LinkedList) parser.getClientProperty("batchQ");
		if (list == null) {
			editor.setStyle(pos.fPosition, lineStart + updateTo, style);
		}
		else {
			synchronized (list) {
				int changeCount = ((Integer) parser.getClientProperty("changeCount")).intValue();
				list.add(new REDSyntaxHighlighterBatchEntry(pos.fPosition, lineStart + updateTo, style, changeCount));
				Runnable executor = (Runnable) parser.getClientProperty("batchExecutor");
				if (executor != null) {
					parser.putClientProperty("batchExecutor", null);
					SwingUtilities.invokeLater(executor);
				}
			}
		}
		pos.fPosition = lineStart + newUpdatePoint;
	}

	protected REDStyle fStyle;
}
