package org.rtefx;

import javafx.scene.control.skin.CellSkinBase;

public class RTELineCellSkin extends CellSkinBase<RTELineCell> {

	public RTELineCellSkin(RTELineCell control) {
		super(control);
		getChildren().addAll(control.getLineFlow(), control.getOverlay());
	}

}
