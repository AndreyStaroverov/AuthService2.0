package gutlag.authservice20;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import gutlag.authservice20.model.User;
import gutlag.authservice20.service.BrutForce;
import gutlag.authservice20.service.PasswordHashAndSalt;
import gutlag.authservice20.storageDB.DBstorage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    private BrutForce brutForce = new BrutForce();
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private LocalDateTime ld = timestamp.toLocalDateTime();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginInAuth;

    @FXML
    private Button loginSignIn;

    @FXML
    private Button loginSignUpButton;

    @FXML
    private TextField passwordInAuth;

    @FXML
    void initialize() {
        //Проверка данных в случае успеха переброс в AuthPage

        loginSignIn.setOnAction(actionEvent -> {
            String loginTxt = loginInAuth.getText().trim();
            String passTxt = passwordInAuth.getText().trim();
            log.info(String.format("Получение данных от приложения с логином %s ", loginTxt));

            try {
                timestamp = new Timestamp(System.currentTimeMillis());
                log.info(String.format("Попытка входа пользователя с логином %s ", loginTxt));
                loginUser(brutForce, loginTxt, passTxt, timestamp);
            } catch (SQLException e) {
                log.debug("SqlException, ошибка в логине или пароле, необходима проверка.");
                log.debug(e.getMessage());
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                log.debug("ClassNotFoundException, необходима проверка.");
                log.debug(e.getMessage());
                throw new RuntimeException(e);
            }
        });
        //Переход на RegPage
        loginSignUpButton.setOnAction(actionEvent -> {
            loginSignUpButton.getScene().getWindow().hide();
            log.info("------- Переход на страницу регистрации -------");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloController.class.getResource("RegPage.fxml"));
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                log.debug(e.getMessage());
                throw new RuntimeException(e);
            }
            Parent parent = fxmlLoader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        });

    }

    private void loginUser(BrutForce brutForce,
                           String loginTxt,
                           String passTxt,
                           Timestamp timestamp) throws SQLException, ClassNotFoundException {

        PasswordHashAndSalt psd = new PasswordHashAndSalt();

        boolean access = false;

        ResultSet resultSet;
        User user;
        DBstorage dBstorage = new DBstorage();
        try {
            user = new User();
            user.setLogin(loginTxt);
            user.setPassword(passTxt);

            if (LocalDateTime.now().isAfter(ld)) {
                log.info(String.format("Попытка получить пароль из БД для проверки с логином %s ", loginTxt));
                String psw = dBstorage.getCryptoPassByLog(user).getCryptoPassword();
                user.setCryptoPassword(psw);
                log.info(String.format("Проверка пароля пользователя с логином %s ", loginTxt));
                user = psd.checkPassword(user);
                if (user != null) {
                    access = true;
                }
            } else {
                access = false;
                log.info(String.format("Запрет на ввод пароля с логином %s ", loginTxt));
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error message");
                int m = ld.getMinute() - LocalDateTime.now().getMinute();
                alert.setHeaderText(String.format("Запрет на ввод пароля еще %s минуты", m));
                alert.setContentText("По истечению срока блокировку попробуйте снова");
                alert.showAndWait();
                loginSignIn.setDisable(true);
                final Timeline animation = new Timeline(
                        new KeyFrame(Duration.seconds(60),
                                actionEvent -> loginSignIn.setDisable(false)));
                animation.setCycleCount(1);
                animation.play();
            }

        } catch (SQLException | ClassNotFoundException e) {
            log.info(String.format("Проверка пароля пользователя неуспешна с логином %s ", loginTxt));
            log.debug(e.getMessage());
            throw new RuntimeException("Error in DB query SQLExc");
        }

        if (access) {
            try {
                log.info(String.format("Получаем пользователя с логином %s из БД", user.getLogin()));
                resultSet = dBstorage.getMapUser(user);
            } catch (SQLException e) {
                log.info(String.format("Пользователя не существует с логином %s ", loginTxt));
                log.debug(e.getMessage());
                resultSet = null;
            }
            if (resultSet != null) {
                log.info(String.format("Доступ для пользователя с логином %s разрешен", loginTxt));
                loginSignUpButton.getScene().getWindow().hide();
                log.info("------ Переход на страницу доступа после авторизации -------");
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(HelloController.class.getResource("AuthPage.fxml"));
                try {
                    log.info("Загрузка новой сцены...");
                    fxmlLoader.load();
                } catch (IOException e) {
                    log.info(e.getMessage());
                    throw new RuntimeException(e);
                }
                Parent parent = fxmlLoader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(parent));
                stage.showAndWait();
            }
        } else {
            log.debug(String.format("Пароли не совпали с логином %s ", loginTxt));
            log.info(String.format("Запрет на ввод пароля с логином %s ", loginTxt));
            ld = brutForce.bruteForceDefend(loginTxt, timestamp);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error message");
            int m = ld.getMinute() - LocalDateTime.now().getMinute();
            alert.setHeaderText(String.format("Запрет на ввод пароля еще %s минуты", m));
            alert.setContentText("По истечению срока блокировку попробуйте снова");
            alert.showAndWait();
        }
    }

}