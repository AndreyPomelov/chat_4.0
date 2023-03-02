package com.example.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static constants.Constants.*;
import static constants.Commands.*;

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
     * Отправка введённого сообщения на сервер.
     *
     * @param actionEvent экземпляр события нажатия на клавишу
     */
    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String message = messageField.getText();
        messageField.clear();
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageField.requestFocus();
    }

    /**
     * Метод, автоматически вызываемый при старте приложения.
     * Отдаёт фокус полю ввода сообщения, затем соединяется с сервером.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> messageField.requestFocus());

        // Работу с сервером выполняем в отдельном потоке,
        // чтобы поток отрисовки окна приложения не зависал.
        new Thread(() -> {
            try {
                connect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Игнорируем ошибку закрытия сокета.
                }
                // Закрываем окно приложения.
                Platform.runLater(() -> ((Stage) messageArea.getScene().getWindow()).close());
            }
        }).start();
    }

    /**
     * Соединение с сервером и инициализация потоков ввода-вывода,
     * запуск режима приёма сообщений.
     *
     * @throws IOException ошибка ввода-вывода.
     */
    private void connect() throws IOException {
        socket = new Socket(IP_ADDRESS, PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        receive();
    }

    /**
     * Приём сообщений от сервера.
     *
     * @throws IOException ошибка ввода-вывода.
     */
    private void receive() throws IOException {
        while (true) {
            String message = in.readUTF();

            // Если пришла команда на отключение, прерываем цикл приёма сообщений.
            if (EXIT.equals(message)) {
                break;
            }

            addMessage(message);
        }
    }

    /**
     * Метод добавляет сообщение в область вывода сообщений.
     *
     * @param message добавляемое сообщение.
     */
    private void addMessage(String message) {
        messageArea.appendText(message + "\n");
    }
}