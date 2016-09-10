package org.bitbucket.shaigem.rssb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.bitbucket.shaigem.rssb.ui.builder.shop.item.ShopItemView;

/**
 * Created on 2016-03-14.
 */
public final class ShopItemSelectionModel {

    public ShopItemSelectionModel() {
        registerSelectionListener();
    }

    private final ObservableSet<ShopItemView> selectedShopItems = FXCollections.observableSet();

    public void setSelected(ShopItemView shopItemView) {
        if (isAlreadySelected(shopItemView)) { // if we try selecting the item when its already selected

            if (hasMultipleSelected()) {
                // if there are multiple items selected,
                // then we clear everything and select only the given item.
                clearSelection();
                selectedShopItems.add(shopItemView);
                return;
            }
            //otherwise we deselect the already selected item
            //   deselect(shopItemView);
            return;
        }

        // clears all selection and then sets the given item as the only selection.
        clearSelection();
        selectedShopItems.add(shopItemView);
    }

    public void addToSelection(ShopItemView shopItemView, boolean deselect) {
        if (deselect && isAlreadySelected(shopItemView) && hasMultipleSelected()) {
            deselect(shopItemView);
            return;
        }
        selectedShopItems.add(shopItemView);
    }


    public void deselect(ShopItemView view) {
        selectedShopItems.remove(view);
    }

    public boolean isAlreadySelected(ShopItemView shopItemView) {
        return selectedShopItems.contains(shopItemView);
    }

    public void clearSelection() {
        selectedShopItems.clear();
    }

    /**
     * Checks if there are at least one or more items selected.
     *
     * @return true if the item selection list is not empty.
     */
    public boolean hasAnySelection() {
        return !selectedShopItems.isEmpty();
    }

    /**
     * Checks if there are multiple items selected.
     *
     * @return true if there are multiple items selected.
     */
    public boolean hasMultipleSelected() {
        return selectedShopItems.size() > 1;
    }


    public ObservableSet<ShopItemView> getSelectedShopItems() {
        return selectedShopItems;
    }

    private void registerSelectionListener() {
        selectedShopItems.addListener(((SetChangeListener<ShopItemView>)
                change -> {
                    if (change.wasAdded()) {
                        ShopItemView view = change.getElementAdded();
                        if (view != null) {
                            view.getStyleClass().setAll("shop-item-selected");

                        }
                    } else if (change.wasRemoved()) {
                        ShopItemView view = change.getElementRemoved();
                        if (view != null) {
                            view.getStyleClass().setAll("shop-item-deselected");
                        }
                    }
                }));
    }
}
