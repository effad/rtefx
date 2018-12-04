package org.rtefx.learning;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TextAreaTest extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane pane = new StackPane();
		TextArea textarea = new TextArea();
		textarea.setText("blubb");
		textarea.setTooltip(new Tooltip("blabb"));
		pane.getChildren().addAll(textarea);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.UNIFIED);
		primaryStage.setWidth(900);
		primaryStage.setHeight(700);
		primaryStage.show();
	}
	
	static String readFile(String path) throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, StandardCharsets.UTF_8);
			}	
		
	public static void main(String[] args) {
		launch(args);
	}

}
