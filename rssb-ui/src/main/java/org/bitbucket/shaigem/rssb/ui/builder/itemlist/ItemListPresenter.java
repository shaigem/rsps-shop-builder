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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import org.bitbucket.shaigem.rssb.model.DragItemManager;
import org.bitbucket.shaigem.rssb.model.ShopTabManager;
import org.bitbucket.shaigem.rssb.model.item.Item;
import org.bitbucket.shaigem.rssb.store.ItemNameStore;
import org.bitbucket.shaigem.rssb.ui.search.SearchPresenter;
import org.bitbucket.shaigem.rssb.ui.search.SearchView;

import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;
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

    @FXML
    ToolBar itemToolBar;
    @FXML
    StackPane itemPane;
    @FXML
    StackPane itemSearchPane;

    public void initialize(URL location, ResourceBundle resources) {
        itemList = FXCollections.observableArrayList();
        ItemNameStore.getNamesMap().keySet().forEach((id) -> itemList.add(new Item(id)));
        filteredList = new FilteredList<>(itemList, l -> true);
        itemListView.setItems(filteredList);
        itemListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setSearchField();
        setupItemListCellFactory();
        listenForDoubleClick();
        registerDragAndDropEvents();
        addSelectedMenuItem.disableProperty().bind(Bindings.isNull
                (tabManager.currentShopProperty()));
    }

    @FXML
    public void onAddSelectedAction() {
        tabManager.getCurrentViewingShop().ifPresent(currentViewing -> {
            ObservableList<Item> selectedItemsList = itemListView.getSelectionModel().getSelectedItems();
            currentViewing.addItems(selectedItemsList);
        });
    }


    private void setSearchPattern(String pattern) {
        this.searchPattern = pattern;
        searchPatternChange();
    }


    private void setSearchField() {
        SearchView itemSearchView = new SearchView();
        SearchPresenter shopListSearchPresenter = (SearchPresenter) itemSearchView.getPresenter();
        shopListSearchPresenter.setPromptText("Search Items");
        shopListSearchPresenter.textProperty().addListener(
                ((observable, oldValue, newValue) -> setSearchPattern(newValue)));
        HBox.setHgrow(itemSearchPane, Priority.ALWAYS);
        itemSearchPane.getChildren().add(itemSearchView.getView());
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
            if (!selectedItemsList.isEmpty()) {
                Dragboard db = itemListView.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString("");
                Optional<Item> firstItem = selectedItemsList.stream().findFirst();
                firstItem.ifPresent(item -> db.setDragView(item.getImage()));
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

    private class ItemListCell extends ListCell<Item> {
        private ImageView imageView;


        @Override
        protected void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty && item != null) {
                setText("[" + item.getId() + "] " + item.getName());
                // getImageFromFile() not getImage()
                // Items from the item list are not cached in the global store in this case
                // These are only cached when they are actually used (added to shop)
                final Image itemImage = item.getImageFromFile();
                if (imageView == null) {
                    imageView = new ImageView(itemImage);
                } else {
                    imageView.setImage(itemImage);
                }
                if (getGraphic() == null)
                    setGraphic(imageView);
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}

