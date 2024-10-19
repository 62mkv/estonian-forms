package ee.mkv.estonian.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MainViewController {

    @FXML
    private TextArea textArea;

    @FXML
    public void handleButtonClick(ActionEvent actionEvent) {
        textArea.appendText("oh no, what have you just done...\n");
    }
}
