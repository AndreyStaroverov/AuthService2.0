package gutlag.authservice20;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(HelloApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("RegistrationApp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        PropertyConfigurator
                .configure("C:/Users/andre/dev/AuthService2.0" +
                        "/src/main/resources/gutlag/authservice20/log4j.properties");
        log.info("Запуск приложения, подгрузка файлов, проверка конфигурации...");
        launch();
    }
}