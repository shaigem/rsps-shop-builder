package org.bitbucket.shaigem.rssb.ui.builder.shop.toolbar;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.bitbucket.shaigem.rssb.ui.builder.shop.ShopPresenter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 11/09/16.
 */
public class ShopToolBarPresenter implements Initializable {
    /**
     * The attached shop. The toolbar will provide tools for this shop.
     */
    private ShopPresenter shopPresenter;

    @FXML
    Button saveButton;
    @FXML
    Button addByIdButton;
    @FXML
    Button selectAllButton;
    @FXML
    Button deleteButton;
    @FXML
    Button deleteAllButton;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set all graphics
        saveButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.CONTENT_SAVE, "1.2em"));
        addByIdButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.DOTS_HORIZONTAL, "1.2em"));
        selectAllButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.SELECT_ALL, "1.2em"));
        deleteButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REMOVE, "1.2em"));
        deleteAllButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.DELETE_VARIANT, "1.2em"));
    }

    @FXML
    public void onSaveAction() {
        shopPresenter.save();
    }

    @FXML
    public void onAddByIdAction() {
        shopPresenter.openAddItemByIndexDialog();
    }

    @FXML
    public void onSelectAllAction() {
        shopPresenter.selectAllItems();
    }

    @FXML
    public void onDeleteAction() {
        shopPresenter.deleteItem(shopPresenter.getSelectionModel().getSelectedShopItems());
    }

    @FXML
    public void onDeleteAllAction() {
        shopPresenter.deleteAllItems();
    }

    public void setShopPresenter(ShopPresenter shopPresenter) {
        this.shopPresenter = shopPresenter;
        shopAttached();
    }

    private void shopAttached() {
        bindDisableProperties();
        // change the delete button text if there are multiple items selected
        // multiple items selected -> Delete Items else Delete Item
        final StringBinding stringBinding =
                Bindings.when(Bindings.size(shopPresenter.getSelectionModel().getSelectedShopItems()).greaterThan(1)).
                        then("Delete Items").otherwise("Delete Item");
        deleteButton.textProperty().bind(stringBinding);

    }

    private void bindDisableProperties() {
        // these controls are disabled when there are no items selected
        final BooleanBinding hasSelectedShopItemsBinding =
                Bindings.isEmpty(shopPresenter.getSelectionModel().getSelectedShopItems());
        deleteButton.disableProperty().bind(hasSelectedShopItemsBinding);
        // these controls are disabled when there are no items present in the shop
        final BooleanBinding hasShopItemsBinding =
                Bindings.isEmpty(shopPresenter.getShopItemPane().getChildren());
        selectAllButton.disableProperty().bind(hasShopItemsBinding);
        deleteAllButton.disableProperty().bind(hasShopItemsBinding);
        // these controls are disabled when the shop does not need saving
        final BooleanBinding needsSavingBinding = shopPresenter.modifiedProperty().asObject().isEqualTo(false);
        saveButton.disableProperty().bind(needsSavingBinding);
    }
}
