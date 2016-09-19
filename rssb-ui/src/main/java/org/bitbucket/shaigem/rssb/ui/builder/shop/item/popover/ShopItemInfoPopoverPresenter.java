package org.bitbucket.shaigem.rssb.ui.builder.shop.item.popover;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.bitbucket.shaigem.rssb.model.item.Item;
import org.bitbucket.shaigem.rssb.ui.builder.shop.item.ShopItemView;
import org.bitbucket.shaigem.rssb.util.ItemAmountUtil;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 2015-09-04.
 */
public class ShopItemInfoPopoverPresenter implements Initializable {

    private final String validationRegex = "[\\dMmKk]+";

    private final Validator<String> validator = (control, string) -> {
        boolean condition = !string.matches(validationRegex);
        return ValidationResult.fromMessageIf(control,
                "Invalid Input! Valid inputs include: integers (0-9) or integers with a suffix. Eg. 50m or 50k",
                Severity.ERROR, condition);
    };

    private ShopItemView attachedItemView;

    @FXML
    AnchorPane rootPane;

    @FXML
    ImageView backgroundImageView;
    @FXML
    ImageView itemImageView;

    @FXML
    Label idLabel;
    @FXML
    Label nameLabel;
    @FXML
    Label amountLabel;

    @FXML
    TextField amountTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // We could set the background images through css, this is just a cheap way of doing it.
        backgroundImageView.setImage((new Image(getClass().getClassLoader().getResourceAsStream
                ("images/info.png"))));
        registerValidator();
        listenForAmountTextFieldChange();
    }

    public void refreshNodes() {
        Item item = attachedItemView.getPresenter().getItem();
        setIdLabelText(item.getId());
        setNameLabelText(item.getName());
        setAmountLabelText(item.getAmount());
        setAmountText(item.getAmount());
        itemImageView.setImage(item.getImageOrFetch());
    }

    public void refreshAmountText() {
        Item item = attachedItemView.getPresenter().getItem();
        String itemAmount = item.getAmount() + "";
        boolean hasChanged = !amountTextField.getText().equals(itemAmount);
        if (hasChanged) {
            setAmountText(item.getAmount());
        }
        amountTextField.selectAll();
        Platform.runLater(() -> amountTextField.requestFocus());
    }

    public TextField getAmountTextField() {
        return amountTextField;
    }

    public Label getAmountLabel() {
        return amountLabel;
    }


    private void listenForAmountTextFieldChange() {
        amountTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.matches(validationRegex) && !newValue.isEmpty()) {
                Item item = attachedItemView.getPresenter().getItem();
                item.setAmount(ItemAmountUtil.getUnformattedAmount(newValue));
                setAmountLabelText(item.getAmount());
                // setAmountLabelText(ItemAmountUtil.getUnformattedAmount(newValue));
            }
        }));
    }

    private void registerValidator() {
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.setErrorDecorationEnabled(true);
        validationSupport.registerValidator(amountTextField, false, validator);
    }

    private void setIdLabelText(int id) {
        idLabel.setText("Id: " + id);
    }

    private void setNameLabelText(String name) {
        nameLabel.setText("Name: " + name);
    }

    private void setAmountLabelText(int amount) {
        amountLabel.setText("Amount: " + amount);
    }

    private void setAmountText(int amount) {
        amountTextField.setText(amount + "");

    }
    public void setAttachedItemView(ShopItemView attachedItemView) {
        this.attachedItemView = attachedItemView;
    }
}
