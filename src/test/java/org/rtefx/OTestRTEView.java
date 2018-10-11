package org.rtefx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OTestRTEView extends Application {
	
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Rich Text Editor for JavaFX");
        Scene scene = new Scene(new VBox(), 400, 350);
 
        REDText text = new REDText("/home/rli/test.txt");
        RTEView view = new RTEView(text);

        ((VBox) scene.getRoot()).getChildren().addAll(view);
 
        stage.setScene(scene);
        stage.show();    
        
        Platform.runLater(() -> { view.requestFocus(); });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
	

}
