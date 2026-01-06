package org.example.xmljsonconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 650); // Un peu plus grand
        stage.setTitle("XJ Converter Pro - XML/JSON avec CDATA & Namespaces");
        stage.setScene(scene);
        stage.show();
    }
}
