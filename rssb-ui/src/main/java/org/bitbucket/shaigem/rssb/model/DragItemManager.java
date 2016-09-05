package org.bitbucket.shaigem.rssb.model;

import javafx.collections.FXCollections;
import org.bitbucket.shaigem.rssb.model.item.Item;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 * Manages the dragging and dropping of items from the item list.
 * Created on 2016-03-14.
 */
public class DragItemManager {

    private List<Item> items;

    @PostConstruct
    public void init() {

        items = FXCollections.observableArrayList();
    }

    public void addAll(Collection<Item> collection) {
        items.addAll(collection);
    }

    public void onDropComplete() {
        clearItems();
    }

    public void onDragFailure() {
        clearItems();
    }

    public List<Item> getItems() {
        return items;
    }

    private void clearItems() {
        items.clear();
    }

}
