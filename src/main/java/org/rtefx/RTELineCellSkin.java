package org.rtefx;

import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.text.Text;

public class RTELineCellSkin extends CellSkinBase<RTELineCell> {

	public RTELineCellSkin(RTELineCell control) {
		super(control);
		getChildren().add(new Text("Blubbl"));
	}

}
