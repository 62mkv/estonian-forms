package ee.mkv.estonian;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class JfxApplication extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("This is a simple popup with an OK button.");

        // Show the alert and wait for the user to close it
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
