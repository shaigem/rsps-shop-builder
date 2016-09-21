package org.bitbucket.shaigem.rssb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.bitbucket.shaigem.rssb.model.item.Item;

import java.util.Collection;

/**
 * Manages items that are copied from a shop.
 * <p>This allows for items from shops to be copied and pasted between different shops with ease.
 * Similar to a copy and paste clipboard for text and files but with shop items instead.</p>
 */
public final class ShopItemClipboardManager {

    private static ShopItemClipboardManager instance = null;

    private final ObservableSet<Item> itemHashSet = FXCollections.observableSet();

    /**
     * Sets the collection of items to the {@link ObservableSet}.
     *
     * @param collection the collection of items that are used to set the {@link ObservableSet}
     */
    public final void setItems(Collection<Item> collection) {
        if (!itemHashSet.isEmpty()) {
            itemHashSet.clear();
        }
        collection.forEach(item -> itemHashSet.add(item.copy()));
    }

    public final boolean hasItems() {
        return !itemHashSet.isEmpty();
    }

    /**
     * Gets the copied items that are ready for pasting.
     *
     * @return the set of items
     */
    public final ObservableSet<Item> getItems() {
        return itemHashSet;
    }

    public static ShopItemClipboardManager getInstance() {
        if (instance == null) {
            instance = new ShopItemClipboardManager();
        }
        return instance;
    }

    private ShopItemClipboardManager() {

    }
}
