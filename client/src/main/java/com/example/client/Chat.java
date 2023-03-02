package com.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Клиентская часть сетевого чата.
 *
 * @version 4.0
 * @author  Andrey Pomelov
 */
public class Chat extends Application {

    /**
     * Инициализация окна приложения.
     *
     * @param stage         экземпляр окна приложения
     * @throws IOException  ошибка ввода-вывода
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Chat.class.getResource("chat.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Chat");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Точка старта приложения.
     * Запускает отрисовку окна приложения.
     */
    public static void main(String[] args) {
        launch();
    }
}