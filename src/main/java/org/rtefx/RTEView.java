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
	

}
