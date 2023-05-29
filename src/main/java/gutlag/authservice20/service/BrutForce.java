package gutlag.authservice20.service;

import gutlag.authservice20.storageDB.DBstorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * BrutForceDefend class made by AndreyS + DanyaLegger(Pokemonchick)
 */
public class BrutForce {

    private final DBstorage dBstorage = new DBstorage();
    private static final Logger log = LoggerFactory.getLogger(BrutForce.class);

    int MAX_COUNT_FAIL_LOGIN = 5;  //Максимальное количество неудачных попыток входа
    int TIME_FRAME_MINUTES = 10;  //Время за которое были сделаны попытки входа проверка
    private HashMap<Timestamp, String> loginFails = new HashMap<>(); //Мапа для хранения неудачных попыток входа

    public LocalDateTime bruteForceDefend (String login, Timestamp time) {

        loginFails.put(time, login);

        int count_logs = 0;
        int m = 0;

        for (String s: loginFails.values()) {
            if(s.equals(login)) {
                m++;
            }
            count_logs = m;
        }

        /** Возвращаем время на которое блокируем доступ к попытке входа, т.е. на 1,2,3,4 минуты соответственно и в
         * случае неудачной 5 попытки мы возвращаем null, что будет тригером для нас заблокировать учетную запись
         * передаем мы время, в которое будет снова доступна попытка входа
         */


        switch (count_logs) {
            case 1:
                log.info(String.format("Запрет на ввод пароля на 1 минуту для пользователя с логином %s ", login));
                return time.toLocalDateTime().plusMinutes(1);
            case 2:
                log.info(String.format("Запрет на ввод пароля на 2 минуты для пользователя с логином %s ", login));
                return time.toLocalDateTime().plusMinutes(2);
            case 3:
                log.info(String.format("Запрет на ввод пароля на 3 минуты для пользователя с логином %s ", login));
                return time.toLocalDateTime().plusMinutes(3);
            case 4:
                log.info(String.format("Запрет на ввод пароля на 4 минуты для пользователя с логином %s ", login));
                return time.toLocalDateTime().plusMinutes(4);
            case 5:
                log.info(String.format("Блокировка после 5 неудачных попыток пользователя с логином %s ", login));

                ArrayList<Timestamp> ts = new ArrayList<>(loginFails.keySet());
                if (ts.get(4).toLocalDateTime().isAfter(ts.get(0).toLocalDateTime().plusMinutes(10))) {
                    loginFails.remove(ts.get(0));
                    log.info(String.format("Запрет на ввод пароля на 4 минуты для пользователя с логином %s ", login));
                    return time.toLocalDateTime().plusMinutes(4);
                }
                try {
                    dBstorage.BlockUser(login);
                } catch (SQLException | ClassNotFoundException e) {
                    log.info(String.format("Пользователя не существует с логином %s ", login));
                    throw new RuntimeException(e);
                }
                return null;

            default:
                return time.toLocalDateTime();
        }
    }

    public void resetBruteForceCount() {
        log.info("Ресет класса BruteForce");
        for (Timestamp t: loginFails.keySet()) {
            loginFails.remove(t);
        }

    }
}
