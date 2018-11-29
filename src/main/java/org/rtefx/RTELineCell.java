package org.rtefx;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

public class RTELineCell extends IndexedCell<String> {
	
	private RTEView view;	
	private TextFlow lineFlow = new TextFlow();
	private Pane overlay = new Pane();
	
    public RTELineCell(RTEView view) {
    	lineFlow.setPadding(new Insets(3, 0, 3, 0));
    	this.view = view; 
	}

	@Override protected Skin<?> createDefaultSkin() {
        return new RTELineCellSkin(this);
    }
    
    @Override
    public void updateIndex(int i) {
    	view.fillLineFlow(i, lineFlow, overlay);
    	setItem(view.getLine(i));
    	super.updateIndex(i);
    }
    
    public TextFlow getLineFlow() {
		return lineFlow;
	}
    
    public Node getOverlay() {
		return overlay;
	}
}
