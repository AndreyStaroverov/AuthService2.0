module gutlag.authservice20 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jbcrypt;
    requires slf4j.api;
    requires log4j;

    opens gutlag.authservice20 to javafx.fxml;
    exports gutlag.authservice20;
}