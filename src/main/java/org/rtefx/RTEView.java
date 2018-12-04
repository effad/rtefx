package org.rtefx;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.scene.Cursor;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class RTEView extends Control {
	
	private REDText text;
	private int cursorPos = 0;
	private Path caret = null;
	private Map<Integer, TextFlow> textFlows = new TreeMap<>();
	
	public RTEView(REDText text) {
		this.text = text;
		setCursor(Cursor.TEXT);
		setOnMouseClicked(this::mouseClicked);
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new RTEViewSkin(this);
	}
	
	public int getLines() {
		return text.getNrOfLines();
	}

	public String getLine(int i) {
		char[] lineChars = text.getLine(i, null); // TODO :: check interface into text, UTF-8?
		String s = lineChars == null ? "" : new String(lineChars);
		return s;	
	}		
	
	public void fillLineFlow(int i, TextFlow flow, Pane overlay) {
		flow.getChildren().clear();
		REDViewStretch stretch = new REDViewStretch();
		int pos = text.getLineStart(i);
		textFlows.put(pos, flow);
		text.getViewStretch(pos, stretch);
		while (stretch.fType != REDViewStretch.EOF && stretch.fType != REDViewStretch.LINEBREAK) {
			byte [] buf = new byte[stretch.fLength];
			int len = stretch.fRunSpec.fRun.copyInto(buf, 0, stretch.fLength, stretch.fRunSpec.fOff);			
			String str = new String(buf, 0, len);
			Text t = new Text(str);
			t.setFont(stretch.fStyle.getFont());
			flow.getChildren().add(t);
			pos += stretch.fLength;
			text.getViewStretch(pos, stretch);
		}
	}
	
	private void updateCaret() {
//		if (caret != null) {
//			((TextFlow) caret.getParent()).getChildren().remove(caret);
//		}
		drawCaret();
	}
	
	private void drawCaret() {
		Iterator<Entry<Integer, TextFlow>> it = textFlows.entrySet().iterator();
		TextFlow flow = null;
		int offset = 0;
		while (it.hasNext()) {
			Entry<Integer, TextFlow> entry = it.next();
			if (entry.getKey() <= cursorPos) {
				flow = entry.getValue();
				offset = cursorPos - entry.getKey();
			} else {
				break; 
			}
		}
		
		if (flow != null) {
			PathElement[] pes = flow.caretShape(offset, true);
			Path p = new Path(pes);
			flow.getChildren().add(p);
		}
	}

	private void mouseClicked(MouseEvent e) {
		cursorPos ++;
		updateCaret();
	}	
}
