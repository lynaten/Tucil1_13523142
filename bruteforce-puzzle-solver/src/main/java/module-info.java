module id.ac.itb.stima {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens id.ac.itb.stima to javafx.fxml;
    exports id.ac.itb.stima;
}