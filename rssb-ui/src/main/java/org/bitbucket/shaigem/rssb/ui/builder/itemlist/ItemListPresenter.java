package org.bitbucket.shaigem.rssb.ui.builder.itemlist;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.bitbucket.shaigem.rssb.model.DragItemManager;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.bitbucket.shaigem.rssb.model.item.Item;
import org.bitbucket.shaigem.rssb.store.ItemNameStore;
import org.bitbucket.shaigem.rssb.ui.builder.shop.ShopPresenter;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 2015-08-11.
 */
public class ItemListPresenter implements Initializable {


    private ObservableList<Item> itemList;
    private FilteredList<Item> filteredList;
    private String searchPattern;

    @Inject
    DragItemManager dragItemManager;

    @Inject
    ShopTabManager tabManager;

    @FXML
    ListView<Item> itemListView;

    @FXML
    MenuItem addSelectedMenuItem;


    public void initialize(URL location, ResourceBundle resources) {
        itemList = FXCollections.observableArrayList();
        ItemNameStore.getNamesMap().keySet().forEach((id) -> itemList.add(new Item(id)));
        filteredList = new FilteredList<>(itemList, l -> true);
        itemListView.setItems(filteredList);
        itemListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setupItemListCellFactory();
        listenForDoubleClick();
        registerDragAndDropEvents();
        addSelectedMenuItem.disableProperty().bind(Bindings.isNull
                (tabManager.currentShopProperty()));
    }

    @FXML
    public void onAddSelectedAction() {
        ShopPresenter currentViewing = tabManager.getCurrentViewingShop();
        if (currentViewing != null) {
            ObservableList<Item> selectedItemsList = itemListView.getSelectionModel().getSelectedItems();
            currentViewing.addItems(selectedItemsList);
        }
    }


    public void setSearchPattern(String pattern) {
        this.searchPattern = pattern;
        searchPatternChange();
    }

    private void searchPatternChange() {
        filteredList.setPredicate(item ->
                searchPattern == null || searchPattern.isEmpty() || item.getName().toLowerCase().contains(searchPattern.toLowerCase()) || searchPattern.equals(Integer.toString(item.getId())));
    }

    private void listenForDoubleClick() {
        itemListView.setOnMousePressed((event -> {
            if (event.getClickCount() == 2) {
                onAddSelectedAction();
            }
        }));
    }

    private void registerDragAndDropEvents() {
        registerOnDragDetected();
    }


    private void registerOnDragDetected() {
        itemListView.setOnDragDetected((t) -> {
            if (!t.getButton().equals(MouseButton.PRIMARY)) {
                t.consume();
                return;
            }
            ObservableList<Item> selectedItemsList = itemListView.getSelectionModel().getSelectedItems();
            if (selectedItemsList != null) {
                Dragboard db = itemListView.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString("");
                Image firstItemImage = selectedItemsList.get(0).getImage();
                db.setDragView(firstItemImage);
                db.setContent(content);
                dragItemManager.addAll(selectedItemsList);

            }
            t.consume();
        });

        itemListView.setOnDragDone((e) -> {
            if (e.getTransferMode() == null) // if transfer failed
                dragItemManager.onDragFailure();

            e.consume();
        });

    }

    private void setupItemListCellFactory() {
        itemListView.setCellFactory((c) -> new ItemListCell());
    }
}

class ItemListCell extends ListCell<Item> {


    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty || item != null) {
            //TODO item list needs a better design
            HBox root = new HBox();
            HBox area = new HBox();

            area.setSpacing(5);
            ImageView imageView = new ImageView(item.getImage());
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            Label infoLabel = new Label("[" + item.getId() + "] " + item.getName());

            area.getChildren().addAll(imageView, infoLabel);
            root.getChildren().add(area);
            setGraphic(root);

        } else {
            setGraphic(null);
        }

    }
}
