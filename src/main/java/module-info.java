module com.example.projeterasmus {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;


    opens com.example.projeterasmus to javafx.fxml;
    exports com.example.projeterasmus;
}