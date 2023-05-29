package gutlag.authservice20.storageDB;


import gutlag.authservice20.constants.Config;
import gutlag.authservice20.model.User;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBstorage extends Config {

    private Connection connection;
    private static final Logger log = LoggerFactory.getLogger(DBstorage.class);

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        String connect = "jdbc:h2:file:./db/registrationApp";
        Statement stmt;

        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(connect, dbUser, dbPass);

        log.info("Использование подключения к базе данных...");
        return connection;
    }

    public void regUser(User user) throws SQLException, ClassNotFoundException {
        String sqlQuery = "INSERT INTO USERS (username, password) " +
                "values(?, ?)";

        log.info(String.format("Регистрация пользователя с логином [ %s ] в базу данных", user.getLogin()));
        PreparedStatement prs = getConnection().prepareStatement(sqlQuery);
        prs.setString(1, user.getLogin());
        prs.setString(2, user.getCryptoPassword());
        prs.executeUpdate();
    }

    public ResultSet getMapUser(User user) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = null;
        String sqlQuery = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";

        log.info(String.format("Получение данных пользователя с логином [ %s ] из БД", user.getLogin()));
        PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, user.getCryptoPassword());
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    public User getCryptoPassByLog(User user) throws SQLException, ClassNotFoundException {
        String sqlQuery = "SELECT password FROM USERS WHERE USERNAME = '" + user.getLogin() + "'";

        log.info(String.format("Получение хешпароля пользователя с логином [ %s ] из БД", user.getLogin()));
        PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String pass = resultSet.getString("PASSWORD");
            user.setCryptoPassword(pass);
        }
        return user;
    }

    public void BlockUser(String login) throws SQLException, ClassNotFoundException {
        String sqlQuery = "UPDATE users SET status = 'block' WHERE USERNAME = '" + login + "'";
        log.info(String.format("Блокировка пользователя с логином [ %s ] в базу данных", login));
        PreparedStatement prs = getConnection().prepareStatement(sqlQuery);
        prs.setString(1, login);
        prs.executeUpdate();
    }

    public String getStatusOfUser(String login) throws SQLException, ClassNotFoundException {
        String sqlQuery = "SELECT status FROM USERS WHERE USERNAME = '" + login + "'";

        log.info(String.format("Получение status пользователя с логином [ %s ] из БД", login));
        PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery();

        String status = "";
        while (resultSet.next()) {
            status = resultSet.getString("STATUS");
        }
        return status;
    }

}
