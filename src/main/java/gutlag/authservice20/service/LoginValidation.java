package gutlag.authservice20.service;

import gutlag.authservice20.RegistrationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginValidation {

    private static final Logger log = LoggerFactory.getLogger(LoginValidation.class);
    private static final String PATTERN_LOGIN  = "[a-zA-Z0-9]{3,}";

    public boolean validate(String login) {
        Pattern pattern = Pattern.compile(PATTERN_LOGIN);
        Matcher matcher = pattern.matcher(login);
        log.info(String.format("Валидация пользователя с логином %s" , login));
        return matcher.matches();
    }
}
