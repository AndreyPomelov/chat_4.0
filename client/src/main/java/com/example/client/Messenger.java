package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static constants.Commands.*;
import static constants.Constants.IP_ADDRESS;
import static constants.Constants.PORT;

/**
 * Доставщик сообщений с сервера.
 */
public class Messenger extends Thread {

    /**
     * Клиентский сокет.
     * Инициализируется при помощи запроса на серверный сокет сервера.
     */
    private Socket socket;

    /**
     * Входящий поток данных для приёма сообщений от сервера.
     */
    private DataInputStream in;

    /**
     * Исходящий поток данных для отправки сообщений на сервер.
     */
    private DataOutputStream out;

    /**
     * Экземпляр контроллера окна приложения.
     * Нужен для взаимодействия с окном.
     */
    private final ChatController controller;

    /**
     * Конструктор.
     * Доставщик автоматически запускается в новом потоке при создании.
     *
     * @param controller экземпляр контроллера окна приложения.
     */
    public Messenger(ChatController controller) {
        this.controller = controller;
        this.start();
    }

    /**
     * Запуск доставщика в работу:
     * соединение с сервером, авторизация, режим приёма сообщений.
     */
    @Override
    public void run() {
        try {
            connect();
            authorisation();
            work();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                controller.resetMessenger();
            } catch (IOException e) {
                // Игнорируем ошибку закрытия сокета.
            }
        }
    }

    /**
     * Соединение с сервером.
     *
     * @throws IOException ошибка ввода-вывода.
     */
    private void connect() throws IOException {
        socket = new Socket(IP_ADDRESS, PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Авторизация пользователя.
     *
     * @throws IOException ошибка ввода-вывода.
     */
    private void authorisation() throws IOException {
        while (true) {
            String message = in.readUTF();

            // Авторизация успешна.
            if (message.startsWith(AUTH_OK)) {
                controller.setWindowTitle(message.split(" ")[1]);
                controller.setAuthenticated(true);
                break;
            }

            // Авторизация неуспешна.
            if (message.equals(AUTH_DENIED)) {
                controller.addMessage("Неверный логин или пароль.");
            }
        }
    }

    /**
     * Режим приёма сообщений с сервера.
     *
     * @throws IOException ошибка ввода-вывода.
     */
    private void work() throws IOException {
        while (true) {
            String message = in.readUTF();

            // Если пришла команда на отключение, прерываем цикл приёма сообщений.
            if (EXIT.equals(message)) {
                controller.setAuthenticated(false);
                break;
            }

            controller.addMessage(message);
        }
    }

    /**
     * Попытка авторизации.
     *
     * @param login         логин.
     * @param password      пароль.
     * @throws IOException  ошибка ввода-вывода.
     */
    public void tryToAuthorise(String login, String password) throws IOException {
        out.writeUTF(String.format("%s %s %s", AUTH, login, password));
    }

    /**
     * Отправка сообщения на сервер.
     *
     * @param message       текст сообщения.
     * @throws IOException  ошибка ввода-вывода.
     */
    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }
}