package server.authentication;

/**
 * Сервис авторизации.
 */
public interface AuthService {

    /**
     * Получить никнейм пользователя по его логину и паролю.
     *
     * @param login     логин.
     * @param password  пароль.
     * @return          никнейм, если авторизация успешна.
     *                  null, если авторизация неуспешна (ошибка в логине или пароле).
     */
    String getNickname(String login, String password);
}