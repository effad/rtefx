package org.rtefx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Color;

public class OTestRTEView extends Application {
	
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Rich Text Editor for JavaFX");
        Scene scene = new Scene(new VBox(), 400, 350);
 
        REDText text = new REDText("/home/rli/test.txt");
        RTEView view = new RTEView(text);
        text.setStyle(5,  10, new REDStyle(Color.BLACK, Color.WHITE, REDLining.DOUBLETHROUGH, "Helvetica", "plain", 22, null));

        ((VBox) scene.getRoot()).getChildren().addAll(view);
 
        stage.setScene(scene);
        stage.show();    
        
        Platform.runLater(() -> { view.requestFocus(); });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
	

}
