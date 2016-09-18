package org.bitbucket.shaigem.rssb.fx.control.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 17/09/16.
 */
public class MaterialDesignInputDialogPresenter implements Initializable{

    @FXML
    StackPane root;
    @FXML
    Label headerLabel;
    @FXML
    Button finishButton;
    @FXML
    Button cancelButton;
    @FXML
    TextField inputTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public TextField getInputTextField() {
        return inputTextField;
    }
}
