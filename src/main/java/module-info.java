module psa.app.psawindowapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.fasterxml.jackson.databind;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens appinterface to javafx.fxml;
    opens pokerlibrary.models to javafx.base, com.fasterxml.jackson.databind;
    exports appinterface;
    exports appinterface.controllers;
    opens appinterface.controllers to javafx.fxml;

    exports pokerlibrary.models to com.fasterxml.jackson.databind;
    exports pokerlibrary.analizer to com.fasterxml.jackson.databind;
}