package gutlag.authservice20.service;

import gutlag.authservice20.RegistrationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidation {

    private static final Logger log = LoggerFactory.getLogger(PasswordValidation.class);
    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%]).{8,20})";

    public boolean validate(final String password){
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        log.info("Проверка пароля...");
        return matcher.matches();

    }
}
