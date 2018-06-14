package sample.model;

import javafx.beans.property.SimpleStringProperty;

// определяем корневой элемент для содержимого XML
public class InternetStream {

    private SimpleStringProperty description = new SimpleStringProperty("");;
    private SimpleStringProperty urlData = new SimpleStringProperty("");

    public InternetStream() {
    }

    public InternetStream(String description, String urlData) {
        this.description = new SimpleStringProperty(description);
        this.urlData = new SimpleStringProperty(urlData);
    }

    public SimpleStringProperty descriptionProperty() { return description; }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public SimpleStringProperty urlDataProperty() { return urlData; }

    public String getUrlData() {
        return urlData.get();
    }

    public void setUrlData(String urlData) {
        this.urlData.set(urlData);
    }
}
