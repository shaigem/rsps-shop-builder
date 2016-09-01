package org.bitbucket.shaigem.rssb.model.shop;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bitbucket.shaigem.rssb.model.item.Item;

import java.util.Objects;

/**
 * Created on 2015-08-11.
 */
public class Shop {

    private final IntegerProperty key;
    private final StringProperty name;
    private final ObservableList<Item> items;
    private final IntegerProperty currency; //TODO better implementation

    /**
     * Holds a boolean that determines if items can be sold to this shop or not.
     * Mostly used for general stores.
     */
    private BooleanProperty canSellToProperty;

    public Shop(int key, String name, Item[] items, int currency, boolean canSellTo) {
        this.key = new SimpleIntegerProperty(key);
        this.name = new SimpleStringProperty(name);
        this.items = Objects.isNull(items) ? FXCollections.observableArrayList() :
                FXCollections.observableArrayList(items);
        this.currency = new SimpleIntegerProperty(currency);
        this.canSellToProperty = new SimpleBooleanProperty(canSellTo);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setKey(int key) {
        this.key.set(key);
    }

    public int getKey() {
        return key.get();
    }

    public String getName() {
        return Objects.isNull(name.get()) ? "NO-NAME" : name.get();
    }

    public ObservableList<Item> getItems() {
        return items;
    }

    public boolean allowSelling() {
        return canSellToProperty.get();
    }

    public BooleanProperty canSellToProperty() {
        return canSellToProperty;
    }

    public IntegerProperty keyProperty() {
        return key;
    }

    public StringProperty nameProperty() {
        return name;
    }


    @Override
    public String toString() {
        return getName();
    }


}