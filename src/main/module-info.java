module PrivateMovieCollection {
    requires java.sql;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.naming;
    requires com.microsoft.sqlserver.jdbc;
    requires com.google.common;
    requires org.controlsfx.controls;
    requires java.desktop;

    opens Budgetflix to javafx.fxml;
    exports Budgetflix;
    opens Budgetflix.GUI.controller to javafx.fxml;
    exports Budgetflix.GUI.controller;
}