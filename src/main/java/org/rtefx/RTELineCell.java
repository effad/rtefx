package org.rtefx;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;

public class RTELineCell extends IndexedCell<String> {
	
	RTEView view;
	
    public RTELineCell(RTEView view) {
    	this.view = view; 
    	itemProperty().addListener((item, oldVal, newVal) -> {
    		System.out.println("Cell " + this + " now has val " + newVal);
    	});
	}

	@Override protected Skin<?> createDefaultSkin() {
        return new RTELineCellSkin(this);
    }
    
    @Override
    public void updateIndex(int i) {
    	setItem(view.getLine(i));
    	super.updateIndex(i);
    }
    
}
