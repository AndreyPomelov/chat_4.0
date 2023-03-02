package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static constants.Commands.EXIT;

/**
 * Менеджер клиента.
 * Экземпляр данного класса создаётся при соединении клиента с сервером.
 * Каждый экземпляр менеджера взаимодействует только со своим клиентом
 * посредством клиентского сокета. Каждый экземпляр работает в отдельном потоке.
 */
public class ClientManager extends Thread {

    /**
     * Клиентский сокет.
     * Для отправки и приёма сообщений.
     */
    private final Socket SOCKET;

    /**
     * Экземпляр сервера.
     * Служит для вызова методов сервера, например,
     * для рассылки сообщения всем клиентам.
     * У всех менеджеров один и тот же экземпляр сервера.
     */
    private final Server SERVER;

    /**
     * Входящий поток данных для приёма сообщений от клиента.
     */
    private final DataInputStream IN;

    /**
     * Исходящий поток данных для отправки сообщений клиенту.
     */
    private final DataOutputStream OUT;

    /**
     * Конструктор.
     * При создании экземпляра менеджера он автоматически запускается в новом потоке.
     *
     * @param socket        клиентский сокет.
     * @param server        экземпляр сервера.
     * @throws IOException  ошибка ввода-вывода.
     */
    public ClientManager(Socket socket, Server server) throws IOException {
        this.SOCKET = socket;
        this.SERVER = server;
        IN = new DataInputStream(socket.getInputStream());
        OUT = new DataOutputStream(socket.getOutputStream());
        this.start();
    }

    /**
     * Запуск рабочего режима менеджера клиента.
     */
    @Override
    public void run() {
        try {
            work();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                SERVER.unsubscribe(this);
                SOCKET.close();
            } catch (Exception e) {
                // Игнорируем ошибку закрытия сокета
            }
        }
    }

    /**
     * Рабочий режим взаимодействия с клиентом, приём/отправка сообщений.
     *
     * @throws IOException ошибка ввода-вывода.
     */
    private void work() throws IOException {
        // Подписываем клиента на рассылку сообщений.
        SERVER.subscribe(this);

        // Цикл обмена сообщениями.
        while (true) {
            // Принимаем сообщение от клиента.
            String message = IN.readUTF();
            System.out.println(this + message);

            // Если пришла команда на отключение, отправляем клиенту обратно
            // команду на отключение и прерываем цикл обмена сообщениями.
            if (EXIT.equals(message)) {
                System.out.println(this + "отключился.");
                OUT.writeUTF(EXIT);
                break;
            }

            // Рассылаем сообщение всем клиентам.
            SERVER.broadcastMessage(message);
        }
    }

    /**
     * Отправка сообщения клиенту.
     *
     * @param message       текст сообщения.
     * @throws IOException  ошибка ввода-вывода.
     */
    public void sendMessage(String message) throws IOException {
        OUT.writeUTF(message);
    }

    /**
     * Реализация метода toString() для экземпляра менеджера клиента.
     *
     * @return строка, содержащая порт, по которому подключен данный клиент.
     */
    @Override
    public String toString() {
        return String.format("Клиент (порт %d) ", SOCKET.getPort());
    }
}