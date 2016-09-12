package org.bitbucket.shaigem.rssb.ui.builder.shop.toolbar;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.bitbucket.shaigem.rssb.event.shop.item.*;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.sejda.eventstudio.DefaultEventStudio;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 11/09/16.
 */
public class ShopToolBarPresenter implements Initializable {

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

    @Inject
    ShopTabManager tabManager;
    @Inject
    DefaultEventStudio eventStudio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.CONTENT_SAVE, "1.2em"));
        addByIdButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.DOTS_HORIZONTAL, "1.2em"));
        selectAllButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.SELECT_ALL, "1.2em"));
        deleteButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REMOVE, "1.2em"));
        deleteAllButton.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.DELETE_VARIANT, "1.2em"));
    }


    @FXML
    public void onSaveAction() {
        eventStudio.broadcast(new SaveShopRequest());
    }

    @FXML
    public void onAddByIdAction() {
        eventStudio.broadcast(new AddItemByIdRequest());
    }

    @FXML
    public void onSelectAllAction() {
        eventStudio.broadcast(new SelectAllShopItemsRequest());
    }

    @FXML
    public void onDeleteAction() {
        eventStudio.broadcast(new DeleteSelectedShopItemsRequest());
    }

    @FXML
    public void onDeleteAllAction() {
        eventStudio.broadcast(new DeleteAllShopItemsRequest());
    }

}
