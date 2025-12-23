module org.example.carrent {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.carrent to javafx.fxml;
    opens org.example.carrent.controller to javafx.fxml;
    opens org.example.carrent.model to javafx.base;
    opens org.example.carrent.db to javafx.fxml;
    opens org.example.carrent.utils to javafx.fxml;

    exports org.example.carrent;
    exports org.example.carrent.db;
    exports org.example.carrent.model;
    exports org.example.carrent.utils;
}
