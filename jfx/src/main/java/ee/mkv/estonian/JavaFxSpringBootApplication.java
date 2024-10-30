package ee.mkv.estonian;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaFxSpringBootApplication {
    public static void main(String[] args) {
        Application.launch(JfxApplication.class, args);
    }
}
