package org.bitbucket.shaigem.rssb.ui.dashboard.toolbar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 03/09/16.
 */
public class DashboardToolbarPresenter implements Initializable {

    @FXML
    Region spacer;

    @FXML
    ToolBar toolBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(spacer, Priority.ALWAYS);
        addToolbarItems();
    }

    private final Button aboutButton = button("About");

    private void addToolbarItems() {
        toolBar.getItems().addAll(aboutButton);
    }

    private Button button(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("dashboard-toolbar-button");
        return button;
    }
}
