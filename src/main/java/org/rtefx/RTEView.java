package org.rtefx;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

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
}
