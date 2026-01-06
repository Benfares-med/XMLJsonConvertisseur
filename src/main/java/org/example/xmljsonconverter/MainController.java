package org.example.xmljsonconverter;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.io.PrintWriter;

public class MainController {
    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private ComboBox<String> strategieCombo;

    private final ManualConverterService manualService = new ManualConverterService();
    private final JacksonConverterService jacksonService = new JacksonConverterService();

    @FXML
    public void initialize() {
        if (strategieCombo != null) {
            strategieCombo.getItems().setAll("Manuel (Regex Avancé)", "Automatique (Jackson Pro)");
            strategieCombo.setValue("Automatique (Jackson Pro)"); 
        }
    }

    @FXML
    protected void handleXmlToJson() {
        String input = inputArea.getText();
        if (input == null || input.isEmpty()) return;

        String strategy = strategieCombo.getValue();

        if (strategy.contains("Jackson")) {
            outputArea.setText(jacksonService.xmlToJsonAuto(input));
        } else {
            outputArea.setText(manualService.xmlToJsonManual(input));
        }
    }

    @FXML
    protected void handleJsonToXml() {
        String input = inputArea.getText();
        if (input == null || input.isEmpty()) return;

        String strategy = strategieCombo.getValue();

        if (strategy.contains("Jackson")) {
            outputArea.setText(jacksonService.jsonToXmlAuto(input));
        } else {
            outputArea.setText(manualService.jsonToXmlManual(input));
        }
    }

    @FXML
    protected void handleImport() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML/JSON Files", "*.xml", "*.json", "*.txt"));
        File file = fc.showOpenDialog(inputArea.getScene().getWindow());
        if (file != null) {
            try {
                inputArea.setText(Files.readString(file.toPath()));
            } catch (Exception e) { outputArea.setText("Erreur lecture : " + e.getMessage()); }
        }
    }

    @FXML
    protected void handleSave() {
        if (outputArea.getText().isEmpty()) return;
        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(outputArea.getScene().getWindow());
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println(outputArea.getText());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
