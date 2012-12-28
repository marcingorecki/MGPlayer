/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mgorecki.cplayer;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author marcin
 */
public class MGPlayer extends Application {
    
    MainScreenController controller=null;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML.fxml"));
        Pane root = (Pane) loader.load();
        Scene scene = new Scene(root, 600, 314);
        
        controller = (MainScreenController)loader.getController();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent t) {
                controller.saveState();
            }
        });

        primaryStage.setTitle("MGPlayer v0.1");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
