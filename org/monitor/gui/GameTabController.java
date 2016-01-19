package org.monitor.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Gilles on 24/11/2015.
 */
public class GameTabController extends HBox {
    @FXML
    private AreaChart<?, ?> cpuChart;

    @FXML
    private GridPane infoGrid;

    @FXML
    private Label lblUptime;

    @FXML
    private Label lblCpuDisplay;

    @FXML
    private Button btnDumpTasks;

    @FXML
    private Label lblTasksDisplay;

    @FXML
    private Label lblUptimeDisplay;

    @FXML
    private Label lblEventsDisplay;

    @FXML
    private Label lblEvents;

    @FXML
    private Label lvbTasks;

    @FXML
    private Label lblMemUse;

    @FXML
    private Button btnDumpEvents;

    @FXML
    private VBox chartBox;

    @FXML
    private Label lblMemDisplay;

    @FXML
    private AreaChart<Integer, String> memoryChart;

    @FXML
    private Label lblCpuUse;

    @FXML
    private Label lblTicks;

    @FXML
    private Label lblTicksDisplay;

    public GameTabController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
