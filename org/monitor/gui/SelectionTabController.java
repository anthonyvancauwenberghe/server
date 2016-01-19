package org.monitor.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.hyperion.rs2.model.World;

/**
 * Created by Gilles on 24/11/2015.
 */
public class SelectionTabController extends TabPane {
    private final World gameWorld;
    private final ConsoleTabController consoleTabController = new ConsoleTabController();
    private final GameTabController gameTabController = new GameTabController();
    private final PlayerTabController playerTabController = new PlayerTabController();

    @FXML
    private Tab gameTab;

    @FXML
    private Tab consoleTab;

    @FXML
    private Tab playerTab;

    public SelectionTabController(World gameWorld) {
        this.gameWorld = gameWorld;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectionTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        consoleTab.setContent(consoleTabController);
        gameTab.setContent(gameTabController);
        playerTab.setContent(playerTabController);
    }
}
