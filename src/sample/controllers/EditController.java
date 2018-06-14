package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.model.InternetStream;

public class EditController {
    @FXML
    private TextField textDescription;
    @FXML
    private TextField textUrl;

    private InternetStream internetStream;
    private Stage editStage;

    public void setInternetStream(InternetStream internetStream) {
        this.internetStream = internetStream;
    }
    public InternetStream getInternetStream() {
        return internetStream;
    }

    public void btnCancel(ActionEvent actionEvent) {
        editStage.hide();
    }

    public void btnOk(ActionEvent actionEvent) {
        internetStream.setDescription(textDescription.getText());
        internetStream.setUrlData(textUrl.getText());
        editStage.hide();
    }

    public void setStage(Stage editStage) {
        this.editStage = editStage;
    }

    public String getTextDescription() {
        return textDescription.getText();
    }

    public void setTextDescription(String textDescription) {
        this.textDescription.setText(textDescription);
    }

    public String getTextUrl() {
        return textUrl.getText();
    }

    public void setTextUrl(String textUrl) {
        this.textUrl.setText(textUrl);
    }
}
