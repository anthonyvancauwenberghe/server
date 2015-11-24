package org.monitor.domain;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.hyperion.Server;
import org.hyperion.rs2.model.World;
import org.monitor.gui.SelectionTabController;

/**
 * Created by Gilles on 24/11/2015.
 */
public class Controller extends Application {
    private World gameWorld;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        gameWorld = Server.launchServer();
        scene = new Scene(new SelectionTabController(gameWorld));
        stage.setScene(scene);

        stage.setOnShown((WindowEvent t) -> {
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        });
        stage.show();
    }

    public World getGameWorld() {
        return gameWorld;
    }
}
