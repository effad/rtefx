package org.rtefx;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;

public class RTELineCell extends IndexedCell<String> {
    @Override protected Skin<?> createDefaultSkin() {
        return new RTELineCellSkin(this);
    }

}
