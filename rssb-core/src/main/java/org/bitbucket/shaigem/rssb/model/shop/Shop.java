package org.bitbucket.shaigem.rssb.model.shop;

import com.google.common.collect.ObjectArrays;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.bitbucket.shaigem.rssb.model.item.Item;

import java.util.List;
import java.util.Objects;

/**
 * Created on 2015-08-11.
 */
public abstract class Shop {

    private final StringProperty name;
    private final ObservableList<Item> items;

    /**
     * Holds a boolean that determines if items can be sold to this shop or not.
     * Mostly used for general stores.
     */
    private BooleanProperty canSellTo;

    public Shop(String name, Item[] items, boolean canSellTo) {
        this.name = new SimpleStringProperty(name);
        this.items = Objects.isNull(items) ? FXCollections.observableArrayList() :
                FXCollections.observableArrayList(items);
        this.canSellTo = new SimpleBooleanProperty(canSellTo);
    }

    public Shop(String name, List<Item> items, boolean canSellTo) {
        this.name = new SimpleStringProperty(name);
        this.items = Objects.isNull(items) ? FXCollections.observableArrayList() :
                FXCollections.observableArrayList(items);
        this.canSellTo = new SimpleBooleanProperty(canSellTo);
    }

    public abstract Shop copy();

    /**
     * Gets any custom properties that can be observed for changes.
     * <p>
     * This is important for plugins because if a plugin implements a custom {@link Shop}, it will
     * most likely have extra properties that will be editable.
     * </p>
     * <p>
     * Include the extra properties in this list if they are editable.
     * </p>
     *
     * @return <code>Observable</code> array of properties to observe
     */
    public Observable[] getCustomPropertiesToObserve() {
        return new Observable[0]; // empty
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setCanSellTo(boolean canSellTo) {
        this.canSellTo.set(canSellTo);
    }

    public String getName() {
        return Objects.isNull(name.get()) ? "NO-NAME" : name.get();
    }

    public ObservableList<Item> getItems() {
        return items;
    }

    public boolean getCanSellTo() {
        return canSellTo.get();
    }

    public BooleanProperty canSellToProperty() {
        return canSellTo;
    }

    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Default properties to be observed.
     *
     * @return <code>Observable</code> array of shop properties
     */
    private Observable[] getDefaultPropertiesToObserve() {
        return new javafx.beans.Observable[]{canSellToProperty()};
    }

    /**
     * Gets the properties from default and custom from plugin.
     *
     * @return <code>Observable</code> array that includes the custom ones from plugin
     */
    private Observable[] propertiesToObserve() {
        return ObjectArrays.concat(getDefaultPropertiesToObserve(),
                getCustomPropertiesToObserve(), Observable.class);
    }

    /**
     * The extractor callback used in <code>FXCollections.observableArrayList</code>.
     * Allows the provided properties to be observed for any changes in a observable array list.
     *
     * @return <code>Observable</code> array that includes the custom ones from plugin
     */
    public static Callback<Shop, Observable[]> extractor() {
        return Shop::propertiesToObserve;
    }


    @Override
    public String toString() {
        return getName();
    }
}