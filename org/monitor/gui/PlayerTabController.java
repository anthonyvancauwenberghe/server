package org.monitor.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Created by Gilles on 24/11/2015.
 */
public class PlayerTabController extends HBox {
    @FXML
    private TextArea txtPlayerInfo;

    @FXML
    private TextField txtfieldFilter;

    @FXML
    private Button btnMute;

    @FXML
    private Button btnWhisper;

    @FXML
    private TableView<?> tblPlayers;

    @FXML
    private Button btnBan;

    @FXML
    private Button btnKick;

    @FXML
    private Button btnDemote;

    @FXML
    private TableColumn<?, ?> colStaff;

    @FXML
    private TableColumn<?, ?> colPlayers;

    public PlayerTabController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
