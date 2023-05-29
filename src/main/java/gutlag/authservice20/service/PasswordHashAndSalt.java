package gutlag.authservice20.service;

import gutlag.authservice20.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHashAndSalt {

    private static final Logger log = LoggerFactory.getLogger(PasswordValidation.class);

    public User hashedAndSalt(User user) {
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        log.info(String.format("Хэширования пароля пользователя с логином %s" , user.getLogin()));
        user.setCryptoPassword(hashed);
        return user;
    }

    public User checkPassword(User user) {
        try {
            if (BCrypt.checkpw(user.getPassword(), user.getCryptoPassword())) {
                log.info(String.format("Проверка пароля 'success' пользователя с логином %s" , user.getLogin()));
                return user;
            } else {
                log.info(String.format("Проверка пароля 'denied' пользователя с логином %s" , user.getLogin()));
                return null;
            }
        } catch (NullPointerException e) {
            log.debug(String.format("NullPointer exc. при проверке " +
                    "пароля пользователя с логином %s" , user.getLogin()));
            return null;
        }
    }
}
