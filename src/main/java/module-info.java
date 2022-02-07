module com.example.projeterasmus {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.projeterasmus to javafx.fxml;
    exports com.example.projeterasmus;
}