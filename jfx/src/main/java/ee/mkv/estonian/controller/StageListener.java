package ee.mkv.estonian.controller;

import ee.mkv.estonian.event.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageListener implements ApplicationListener<StageReadyEvent> {
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        System.out.println("Stage is ready");
        Stage stage = event.getStage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ee/mkv/estonian/MainView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("JavaFX Application");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
