package org.rtefx;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class RTEView extends Control {
	
	private REDText text;

	public RTEView(REDText text) {
		this.text = text;
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
	
	public void fillLineFlow(int i, TextFlow flow) {
		flow.getChildren().clear();
		REDViewStretch stretch = new REDViewStretch();
		int pos = text.getLineStart(i);
		text.getViewStretch(pos, stretch);
		while (stretch.fType != REDViewStretch.EOF && stretch.fType != REDViewStretch.LINEBREAK) {
			byte [] buf = new byte[stretch.fLength];
			int len = stretch.fRunSpec.fRun.copyInto(buf, 0, stretch.fLength, stretch.fRunSpec.fOff);			
			String str = new String(buf, 0, len);
			Text t = new Text(str);
			t.setFont(convert(stretch.fStyle.getFont()));
			flow.getChildren().add(t);
			pos += stretch.fLength;
			text.getViewStretch(pos, stretch);
		}
	}

	// TODO :: REDStyle should contain javafx.scene.text.Font, this method should not be necessary 
	private Font convert(java.awt.Font font) {
		return Font.font(font.getFamily(), font.getSize());
	}
}
