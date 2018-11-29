package org.rtefx;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;
import javafx.scene.text.TextFlow;

public class RTELineCell extends IndexedCell<String> {
	
	private RTEView view;	
	private TextFlow lineFlow = new TextFlow();
	
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
    	view.fillLineFlow(i, lineFlow);
    	setItem(view.getLine(i));
    	super.updateIndex(i);
    }
    
    public TextFlow getLineFlow() {
		return lineFlow;
	}
}
