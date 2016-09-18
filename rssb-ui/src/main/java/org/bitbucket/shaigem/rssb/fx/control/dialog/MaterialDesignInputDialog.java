package org.bitbucket.shaigem.rssb.fx.control.dialog;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * A very basic input dialog that has the material design style applied to it.
 * <p>This input dialog was created only to combat the crashing bug when using key events in dialogs in Linux. </p>
 * <p>Refer to the
 * <a href="http://stackoverflow.com/questions/18512654/jvm-crashes-on-pressing-press-enter-key-in-a-textfield">
 * StackOverflow - JVM crashes on pressing enter in text field</a> for this issue.</p>
 */
public class MaterialDesignInputDialog extends Stage {
    private StringProperty responseType = new SimpleStringProperty("CANCEL");

    private final MaterialDesignInputDialogPresenter presenter;

    public MaterialDesignInputDialog(String initialInput) {
        initModality(Modality.APPLICATION_MODAL);
        final MaterialDesignInputDialogView view = new MaterialDesignInputDialogView();
        presenter = (MaterialDesignInputDialogPresenter) view.getPresenter();
        StackPane root = (StackPane) view.getView();
        setScene(new Scene(root));
        getContentPane().inputTextField.setText(initialInput);
        getContentPane().finishButton.setOnAction(event -> {
            setResponseType("FINISH");
            Platform.runLater(this::close); // MUST have Platform.runLater!!! Otherwise, crash on Linux
        });
        getContentPane().cancelButton.setOnAction(event -> close());
    }

    public MaterialDesignInputDialog() {
        this("");
    }

    public final Optional<String> showAndWaitWithInput() {
        showAndWait();
        if (getResponseType().equals("CANCEL")) {
            return Optional.empty();
        }
        return Optional.of(getContentPane().getInputTextField().getText());
    }

    public final void setPromptText(String promptText) {
        getContentPane().getInputTextField().setPromptText(promptText);
    }

    public final void setHeaderText(String headerText) {
        getContentPane().headerLabel.setText(headerText);
    }

    public final MaterialDesignInputDialogPresenter getContentPane() {
        return presenter;
    }

    private void setResponseType(String responseType) {
        this.responseType.set(responseType);
    }

    private String getResponseType() {
        return responseType.get();
    }
}