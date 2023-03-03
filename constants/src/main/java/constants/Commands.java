package constants;

/**
 * Служебные команды для взаимодействия серверной и клиентской части
 *
 * @version 4.0
 * @author Andrey Pomelov
 */
public interface Commands {

    /**
     * Отключение от сервера
     */
    String EXIT = "/exit";

    /**
     * Запрос авторизации.
     */
    String AUTH = "/auth";

    /**
     * Авторизация успешна.
     */
    String AUTH_OK = "/auth_ok";

    /**
     * Авторизация неуспешна.
     */
    String AUTH_DENIED = "/auth_denied";
}