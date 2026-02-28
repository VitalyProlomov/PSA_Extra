module psa.app.psawindowapp {
    // Export your main package
    exports web;
    exports appinterface;

    // Open for reflection (Spring needs this)
    opens web to spring.core, spring.beans, spring.context, spring.boot;
    opens appinterface to javafx.fxml, spring.core;

    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Spring Boot modules
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.web;
    requires spring.webmvc;

    // Other dependencies
    requires jakarta.annotation;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires org.controlsfx.controls;

    // Allow access to these packages
    opens appinterface.controllers to javafx.fxml;
    opens pokerlibrary.models to javafx.base, com.fasterxml.jackson.databind;

}