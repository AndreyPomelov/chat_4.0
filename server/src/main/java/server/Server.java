package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.PORT;

/**
 * Сервер
 */
public class Server {

    /**
     * Серверный сокет.
     * Служит для обработки первичного запроса от клиента и инициализации клиентского сокета.
     */
    private ServerSocket server;

    /**
     * Список клиентов онлайн.
     */
    private final List<ClientManager> CLIENTS = new ArrayList<>();

    /**
     * Конструктор.
     * При создании экземпляра сервера он автоматически запускается
     * в режим ожидания соединения клиентов.
     */
    public Server() {
        try {
            runServer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (Exception e) {
                // Игнорируем ошибку закрытия серверного сокета
            }
        }
    }

    /**
     * Метод запускает сервер в режим ожидания подключения клиентов,
     * каждому подключившемуся клиенту выдаётся свой сокет и экземпляр менеджера.
     *
     * @throws IOException ошибка ввода-вывода
     */
    private void runServer() throws IOException {
        server = new ServerSocket(PORT);
        System.out.printf("Сервер успешно запущен, порт %d.\n", PORT);

        // Цикл подключения клиентов (для каждого клиента создаётся свой экземпляр менеджера)
        while (true) {
            ClientManager client = new ClientManager(server.accept(), this);
            System.out.println(client + "успешно подключен.");
        }
    }

    /**
     * Подключение клиента к рассылке сообщений.
     *
     * @param client подключаемый клиент.
     */
    public void subscribe(ClientManager client) {
        CLIENTS.add(client);
        System.out.println(client + "успешно подписан на рассылку сообщений.");
    }

    /**
     * Отключение клиента от рассылки сообщений.
     *
     * @param client отключаемый клиент.
     */
    public void unsubscribe(ClientManager client) {
        CLIENTS.remove(client);
        System.out.println(client + "успешно отключён от рассылки сообщений.");
    }

    /**
     * Рассылка сообщения всем клиентам онлайн.
     *
     * @param message       текст сообщения.
     * @throws IOException  ошибка ввода-вывода
     */
    public void broadcastMessage(String message) throws IOException {
        for (ClientManager client : CLIENTS) {
            client.sendMessage(message);
        }
    }
}