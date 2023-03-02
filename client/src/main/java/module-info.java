module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    // Импортируем пакет constants из модуля constants
    requires constants;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
}