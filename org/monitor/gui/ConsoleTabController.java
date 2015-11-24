package org.monitor.gui;

/**
 * Created by Gilles on 24/11/2015.
 */

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleTabController extends VBox {

    @FXML
    private TextArea consoleArea;

    @FXML
    private Button sendBtn;

    @FXML
    private TextField commandField;

    public ConsoleTabController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConsoleTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendText(String.valueOf((char) b));
            }
        };
        System.setOut(new PrintStream(out, true));

        sendBtn.setOnAction(event -> {
            if(commandField.getCharacters().length() == 0)
                return;
            System.out.println(commandField.getCharacters());   //TODO make this send messages to the server
            commandField.clear();
        });
    }

    public void appendText(String str) {
        Platform.runLater(() -> consoleArea.appendText(str));
    }
}
