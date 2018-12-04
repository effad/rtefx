package org.rtefx.learning;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Font;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class TextFlowTest extends Application {

	private TextFlow flow;
	private int cursorPos = 0;
	private StackPane stackPane;
	private Pane cursorPane;
	private Path caret = new Path();
	private ScrollPane root;
	
	@Override
	public void start(Stage primaryStage) {
		cursorPane = new Pane();
		cursorPane.getChildren().add(caret);
		Timeline cursorBlink = new Timeline();
		cursorBlink.getKeyFrames().add(new KeyFrame(javafx.util.Duration.millis(500), null, (e) -> {
			caret.setVisible(!caret.isVisible());
		}));
		cursorBlink.setCycleCount(Animation.INDEFINITE);
		cursorBlink.play();
		
		flow = new TextFlow();
		
		stackPane = new StackPane(flow, cursorPane);
		
        root = new ScrollPane();		
		root.setContent(stackPane);
		root.setOnMouseClicked(this::setCursorPos);
		
		flow.prefWidthProperty().bind(root.widthProperty().subtract(10));
		flow.widthProperty().addListener((o) -> updateCursor());
		
		addText("Big italic red text ", Color.RED, 26.0);
		addText("little bold\nblue text", Color.BLUE, 12);
		addText("In a hole in the earth there lived a hobbit\n", Color.BROWN, 14);
		addText("This is a very long line which can demonstrate line wrapping within textflows. ", Color.BLACK, 14);

		Scene scene = new Scene(root, 300, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		updateCursor();
	}
	
	private void setCursorPos(MouseEvent e) {
		Point2D p = flow.sceneToLocal(e.getSceneX(), e.getSceneY());
		HitInfo hit = flow.hitTest(p);
		cursorPos = hit.getInsertionIndex();
		updateCursor();
	}
	
	private void updateCursor() {
		PathElement[] pe = flow.caretShape(cursorPos, true);
//		PathElement[] pe = flow.rangeShape(cursorPos, cursorPos + 4);
		caret.getElements().setAll(pe);
	}

	private void addText(String str, Color color, double size) {
		Text text = new Text(str);
		text.setFill(color);
		text.setFont(Font.font(size));
		flow.getChildren().add(text);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
