package gutlag.authservice20;

import gutlag.authservice20.model.User;
import gutlag.authservice20.service.LoginValidation;
import gutlag.authservice20.service.PasswordHashAndSalt;
import gutlag.authservice20.service.PasswordValidation;
import gutlag.authservice20.storageDB.DBstorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginInAuth;

    @FXML
    private Button loginSignIn;

    @FXML
    private Button loginSignIn1;

    @FXML
    private TextField passwordInAuth;

    @FXML
    void initialize() {
        PasswordHashAndSalt psd = new PasswordHashAndSalt();
        LoginValidation loginValidation = new LoginValidation();
        PasswordValidation passwordValidation = new PasswordValidation();

        loginSignIn.setOnAction(actionEvent -> {
            String loginTxt = loginInAuth.getText().trim();
            String passTxt = passwordInAuth.getText().trim();
            log.info(String.format("Получение данных от приложения с логином %s ", loginTxt));
            User user = new User();

            if (loginValidation.validate(loginTxt) && passwordValidation.validate(passTxt)) {
                log.info(String.format("Успешная валидация пароля и логина - %s ", loginTxt));
                user = new User(loginTxt, passTxt);
                log.info(String.format("Хэширования пароля пользователя с логином %s ", loginTxt));
                user = psd.hashedAndSalt(user);
            } else {
                log.info(String.format("Ошибка в валидации пользователя с логином - %s ", loginTxt));
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Information");
                alert.setHeaderText("Bad Password and Login");
                alert.setContentText("Try use correctly password");
                alert.showAndWait();
            }
            try {
                DBstorage dBstorage = new DBstorage();
                log.info("Новое подключение к Базе Данных");
                if (user.getLogin() == null) {
                    log.debug("Логин [null] - выброс ошибки");
                    throw new SQLException();
                }
                log.info(String.format("Успешная регистрация пользователя с логином %s ", loginTxt));
                dBstorage.regUser(user);
                openScene();

            } catch (SQLException | ClassNotFoundException e) {
                log.debug("SqlException, пароль или логин не прошли валидацию.");
            }
        });

        loginSignIn1.setOnAction(actionEvent -> {
            log.info("------- Переход на страницу Входа -------");
            loginSignIn1.getScene().getWindow().getOnCloseRequest();
            Stage stage1 = (Stage) loginSignIn1.getScene().getWindow();
            stage1.close();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloController.class.getResource("hello-view.fxml"));
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Parent parent = fxmlLoader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("RegistrationApp");
            stage.showAndWait();

        });
    }

    private void openScene() {

        log.info("------- Переход на страницу Входа после успешной регистрации -------");
        loginSignIn1.getScene().getWindow().getOnCloseRequest();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(HelloController.class.getResource("hello-view.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Parent parent = fxmlLoader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.showAndWait();
    }
}
