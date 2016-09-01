package org.bitbucket.shaigem.rssb.ui.search;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 2015-08-10.
 */
public class SearchPresenter implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private StackPane searchIcon;


    public void initialize(URL location, ResourceBundle resources) {
        searchField.setPromptText("Search...");
    }

    public StringProperty textProperty() {
        return searchField.textProperty();
    }

}
