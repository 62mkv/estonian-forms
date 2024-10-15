package ee.mkv.estonian.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainViewController {

    @FXML
    private Label label;

    @FXML
    public void handleButtonClick(ActionEvent actionEvent) {
        label.setText("oh no, what have you just done...");
    }
}
