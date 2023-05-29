package gutlag.authservice20.model;

import java.time.LocalDateTime;

public class User {

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCryptoPassword() {
        return cryptoPassword;
    }

    public void setCryptoPassword(String cryptoPassword) {
        this.cryptoPassword = cryptoPassword;
    }

    public User(String login, String password, String cryptoPassword) {
        this.login = login;
        this.password = password;
        this.cryptoPassword = cryptoPassword;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    private String login;
    private String password;
    private String cryptoPassword;

}
