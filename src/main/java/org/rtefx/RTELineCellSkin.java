package org.rtefx;

import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.text.Text;

public class RTELineCellSkin extends CellSkinBase<RTELineCell> {

	private Text line;
	
	public RTELineCellSkin(RTELineCell control) {
		super(control);
		line = new Text();
		line.textProperty().bind(control.itemProperty().asString());
		getChildren().addAll(control.getLineFlow(), control.getOverlay());
	}

}
