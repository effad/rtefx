package org.rtefx;

import javafx.scene.control.skin.VirtualContainerBase;
import javafx.scene.control.skin.VirtualFlow;

public class RTEViewSkin extends VirtualContainerBase<RTEView, RTELineCell> {

	private VirtualFlow<RTELineCell> flow;

	protected RTEViewSkin(RTEView control) {
		super(control);		
		flow = getVirtualFlow();
        flow.setCellFactory(flow -> createCell());
        getChildren().add(flow);
        markItemCountDirty();
	}
	
    private RTELineCell createCell() {
    	RTELineCell cell = new RTELineCell(getSkinnable());
        return cell;
    }
	

	@Override
	protected int getItemCount() {
		return getSkinnable().getLines();
	}

	@Override
	protected void updateItemCount() {
        if (flow == null) return;
        flow.setCellCount(getItemCount());
        flow.requestLayout();
	}
	
	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		super.layoutChildren(x, y, w, h);
		flow.resizeRelocate(x, y, w, h);
	}
	
    /** {@inheritDoc} */
    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(-1, topInset, rightInset, bottomInset, leftInset) * 0.618033987; // TODO :: calculate real width
    }

    /** {@inheritDoc} */
    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 600;
    }        
}
