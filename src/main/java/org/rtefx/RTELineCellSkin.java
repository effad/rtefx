package org.rtefx;

import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.text.Text;

public class RTELineCellSkin extends CellSkinBase<RTELineCell> {

	private Text line;
	
	public RTELineCellSkin(RTELineCell control) {
		super(control);
		line = new Text("foobar");
		line.textProperty().bind(control.textProperty());
		line.textProperty().addListener((prop, oldValue, newValue) -> {
			System.out.println("Cell val:" + newValue);
		});
		getChildren().add(line);
	}

}
