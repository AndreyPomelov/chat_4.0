package com.example.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер окна чата.
 */
public class ChatController implements Initializable {

    /**
     * Область вывода сообщений.
     */
    @FXML
    public TextArea messageArea;

    /**
     * Поле ввода сообщения.
     */
    @FXML
    public TextField messageField;

    /**
     * Форма авторизации.
     */
    @FXML
    public HBox authForm;

    /**
     * Поле для ввода логина.
     */
    @FXML
    public TextField loginField;

    /**
     * Поле для ввода пароля.
     */
    @FXML
    public PasswordField passwordField;

    /**
     * Форма отправки сообщений.
     */
    @FXML
    public HBox messageForm;

    /**
     * Экземпляр доставщика сообщений с сервера.
     */
    private Messenger messenger;

    /**
     * Метод, автоматически вызываемый при старте приложения.
     * Отдаёт фокус полю ввода сообщения,
     * затем инициализирует экземпляр доставщика для работы с сервером.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> messageArea.requestFocus());
        messenger = new Messenger(this);
        setAuthenticated(false);
    }

    /**
     * Отправка введённого сообщения на сервер.
     */
    @FXML
    public void sendMessage() {
        String message = messageField.getText();
        messageField.clear();
        try {
            messenger.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageField.requestFocus();
    }

    /**
     * Попытка авторизации.
     */
    @FXML
    public void tryToAuth() {
        String login = loginField.getText();
        String password = passwordField.getText();
        passwordField.clear();
        try {
            messenger.tryToAuthorise(login, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Изменение параметров окна в зависимости от состояния авторизации пользователя.
     * Авторизован:     отображаем форму отправки сообщения
     *                  скрываем форму авторизации
     *                  очищаем область вывода сообщений
     * Не авторизован:  скрываем форму отправки сообщения
     *                  отображаем форму авторизации
     *                  отображаем "Chat" в заголовке окна вместо ника
     *
     * @param authenticated состояние авторизации пользователя: true, если авторизован
     */
    public void setAuthenticated(boolean authenticated) {
        messageForm.setVisible(authenticated);
        messageForm.setManaged(authenticated);
        authForm.setVisible(!authenticated);
        authForm.setManaged(!authenticated);

        if (authenticated) {
            Platform.runLater(() -> messageArea.clear());
        } else {
            setWindowTitle("Chat");
        }
    }

    /**
     * Метод добавляет сообщение в область вывода сообщений.
     *
     * @param message добавляемое сообщение.
     */
    public void addMessage(String message) {
        messageArea.appendText(message + "\n");
    }

    /**
     * Установка заголовка окна приложения.
     *
     * @param title текст заголовка окна.
     */
    public void setWindowTitle(String title) {
        Platform.runLater(() -> ((Stage) messageArea.getScene().getWindow()).setTitle(title));
    }

    /**
     * Переинициализация доставщика сообщений.
     * Т.к. доставщик работает в отдельном потоке,
     * и уже отработавший поток мы не можем запустить заново,
     * то нам необходим новый доставщик для каждой новой
     * сессии подключения клиента к серверу.
     */
    public void resetMessenger() {
        messenger = new Messenger(this);
    }
}