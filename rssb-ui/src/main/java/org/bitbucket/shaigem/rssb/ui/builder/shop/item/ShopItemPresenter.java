package org.bitbucket.shaigem.rssb.ui.builder.shop.item;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import org.bitbucket.shaigem.rssb.fx.control.ShopDisplayRadioButton;
import org.bitbucket.shaigem.rssb.model.item.Item;
import org.bitbucket.shaigem.rssb.ui.builder.shop.ShopPresenter;
import org.bitbucket.shaigem.rssb.util.AlertDialogUtil;
import org.bitbucket.shaigem.rssb.util.ItemAmountUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created on 2015-08-13.
 */
public final class ShopItemPresenter {

    private ShopItemView shopItemView;
    private ObjectProperty<Item> itemObjectProperty;
    private ShopPresenter shopPresenter;
    private BooleanProperty finishedInitializing;


    private static final DataFormat SERIALIZED_DATA_FORMAT = new DataFormat
            ("application/x-java-serialized-object");


    ShopItemPresenter(ShopItemView shopItemView) {
        this.shopItemView = shopItemView;
        itemObjectProperty = new SimpleObjectProperty<>();
        finishedInitializing = new SimpleBooleanProperty();
        finishedInitializing.bind(Bindings.isNotNull(itemObjectProperty));
        listenForFinishInitializing();
        listenForItemChange();
        onChangeAmountMenuAction();
        onClickAction();
        onDoubleClickAction();
        onDeleteAction();
        onCopyAction();
        handleItemSwapping();
    }

    public boolean openEditIndexDialog() {
        Item item = shopItemView.getPresenter().getItem();
        TextInputDialog dialog = new TextInputDialog(item.getId() + "");
        dialog.setTitle("Change Item");
        dialog.setHeaderText("Editing Item: " + item.getName());
        dialog.setContentText("Please enter the id of the item:");
        dialog.setGraphic(new ImageView(item.getImage())); //can't reuse images view here
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int id = Integer.parseInt(result.get());
                if (id != item.getId() && id > -1) {
                    setItem(new Item(id, item.getAmount()));
                    getShopPresenter().updateSelectionInformation();
                    getShopPresenter().markAsModified();
                    return true;
                }
            } catch (NumberFormatException exc) {
                ButtonType tryAgainButton = new ButtonType("Try Again", ButtonBar.ButtonData.OK_DONE);
                ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert
                        = AlertDialogUtil.createExceptionDialog(exc);
                alert.setTitle("Error With Input");
                alert.getButtonTypes().setAll(tryAgainButton, closeButton);
                alert.setHeaderText("Invalid Input");
                alert.setContentText("A exception occurred while trying to parse your input. Remember," +
                        " the input must be a number!");
                Optional<ButtonType> response = alert.showAndWait();
                if (response.isPresent()) {
                    ButtonType buttonType = response.get();
                    if (buttonType == tryAgainButton) {
                        openEditIndexDialog();
                    }
                }
                return false;
            }
        }
        return false;
    }


    public void onRemoval() {
        shopItemView = null;
    }

    public void setShopPresenter(ShopPresenter shopPresenter) {
        this.shopPresenter = shopPresenter;
    }

    public ShopPresenter getShopPresenter() {
        return this.shopPresenter;
    }

    public void setItem(Item item) {
        itemObjectProperty.set(item);
    }

    public Item getItem() {
        return itemObjectProperty.get();
    }

    private void listenForFinishInitializing() {
        finishedInitializing.addListener((observable, oldValue, finished) -> {
            if (finished) {
                //does stuff when initializing is complete, prevents null bs
                listenForDisplayModeChange();
                refreshDisplayMode(shopPresenter.getCurrentDisplayMode());
            }
        });
    }

    private void listenForDisplayModeChange() {
        shopPresenter.displayModeProperty().addListener(((observable, oldValue, newValue) ->
                refreshDisplayMode(newValue)));
    }

    private void refreshDisplayMode(ShopDisplayRadioButton.DisplayMode mode) {
        if (shopItemView != null)
            shopItemView.updateDisplayMode(mode);

    }

    private void listenForItemChange() {
        itemObjectProperty.addListener(((observable, oldValue, newItemValue) -> {
            if (Objects.nonNull(newItemValue)) {
                shopItemView.getItemImageView().setImage(newItemValue.getImage());
                shopItemView.updateAmountLabel(newItemValue.getAmount());
                shopItemView.updateNameLabel(newItemValue.getName());
                shopItemView.updateIdLabel(newItemValue.getId());
                shopItemView.setTooltipText("Id: " + newItemValue.getId() + ", " + newItemValue.getName());
                listenForItemAmountChange(newItemValue);
            }
        }));

    }


    private void listenForItemAmountChange(Item item) {
        item.amountProperty().addListener(((observable1, oldValue1, newValue1) -> {
            shopItemView.updateAmountLabel(Objects.isNull(newValue1) ? 0 : newValue1.intValue());
            shopPresenter.markAsModified();
        }));
    }

    private void onClickAction() {
        shopItemView.setOnMouseClicked((event -> {
            if (event.getClickCount() == 1) {
                boolean multiSelect = event.isControlDown();

                if (multiSelect && event.getButton() == MouseButton.PRIMARY) {
                    shopPresenter.getSelectionModel().addToSelection(shopItemView, true);
                } else if (!multiSelect) {
                    if (shopPresenter.getSelectionModel().hasMultipleSelected() &&
                            event.getButton() == MouseButton.SECONDARY) {
                        if (shopPresenter.getSelectionModel().isAlreadySelected(shopItemView)) {
                            // Selected items won't change when the user requests for
                            // context menu of the item
                            return;
                        } else {
                            shopPresenter.getSelectionModel().setSelected(shopItemView);
                        }
                    }
                    shopPresenter.getSelectionModel().setSelected(shopItemView);
                }

            }

        }));

    }

    /**
     * Snapshot parameters for taking a snapshot of the shop item used as a visual for drag and drop.
     */
    private final SnapshotParameters snapshotParameters = new SnapshotParameters();

    private void handleItemSwapping() {
        shopItemView.setOnDragDetected((event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                shopPresenter.getSelectionModel().setSelected(shopItemView);//select dat shit first man
                Dragboard db = shopItemView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                TilePane parent = shopPresenter.getShopItemPane();
                Integer index = parent.getChildren().indexOf(shopItemView);
                content.put(SERIALIZED_DATA_FORMAT, index);
                snapshotParameters.setFill(new Color(0, 0, 0, 0.5)); // fix transparency for linux
                db.setDragView(shopItemView.snapshot(snapshotParameters, null));
                db.setContent(content);
            }
            event.consume();
        }));

        shopItemView.setOnDragOver((event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_DATA_FORMAT)) {
                TilePane parent = shopPresenter.getShopItemPane();
                Integer index = parent.getChildren().indexOf(shopItemView);
                if (index != ((Integer) db.getContent(SERIALIZED_DATA_FORMAT)).intValue()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            } else if (db.hasString() &&
                    !shopPresenter.getDragItemManager().getItems().isEmpty()) {
                // supports items dragged from the item list
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }

        });

        shopItemView.setOnDragDropped((event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasContent(SERIALIZED_DATA_FORMAT)) {
                TilePane parent = shopPresenter.getShopItemPane();
                Integer toIndex = parent.getChildren().indexOf(shopItemView);
                int fromIndex = (Integer) db.getContent(SERIALIZED_DATA_FORMAT);
                shopPresenter.swapItems(fromIndex, toIndex);
                success = true;
            } else if (db.hasString()) { //items dragged from the item list on top of a shop item
                List<Item> dragList = shopPresenter.getDragItemManager().getItems();
                final boolean multipleItems = dragList.size() > 1;
                if (multipleItems) {
                    if (shopPresenter.getSelectionModel().hasAnySelection()) {
                        // clears any selection if we are adding multiple items
                        // Reason is so we can select just the items that are being dragged
                        shopPresenter.getSelectionModel().clearSelection();
                    }
                }
                dragList.forEach((item -> shopPresenter.addItem(item, multipleItems)));
                shopPresenter.getDragItemManager().onDropComplete();
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        }));

    }

    private int openChangeAmountDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Amount");
        dialog.setHeaderText("Change Amount for Multiple Items");
        dialog.setContentText("Please enter the new amount:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (!result.get().isEmpty())
                return ItemAmountUtil.getUnformattedAmount(result.get());
        }
        return -1;
    }

    private void onCopyAction() {
        shopItemView.getDuplicateMenuItem().setOnAction((event -> shopPresenter.copyItem(shopItemView)));
    }

    private void onChangeAmountMenuAction() {
        shopItemView.getChangeAmountMenuItem().setOnAction((event -> {
            int result = openChangeAmountDialog();
            if (result > -1) {
                shopPresenter.getSelectionModel().getSelectedShopItems().forEach((shopItem) -> {
                    Item itm = shopItem.getPresenter().getItem();
                    itm.setAmount(result);
                });
            }
        }));
    }

    private void onDoubleClickAction() {
        shopItemView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                shopItemView.showInfoPopOver();
            }
        });
    }

    private void onDeleteAction() {
        shopItemView.getDeleteMenuItem().setOnAction((event ->
                shopPresenter.deleteItem(shopPresenter.getSelectionModel().getSelectedShopItems())));
    }
}
