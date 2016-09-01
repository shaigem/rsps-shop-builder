package org.bitbucket.shaigem.rssb.model.item;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import org.bitbucket.shaigem.rssb.store.ItemImageStore;
import org.bitbucket.shaigem.rssb.store.ItemNameStore;


/**
 * Created on 2015-08-11.
 */
public class Item {

    private IntegerProperty idProperty;
    private IntegerProperty amountProperty;
    private StringProperty nameProperty;
    private ObjectProperty<Image> imageObjectProperty;

    public Item(int id, int amount) {
        this.idProperty = new SimpleIntegerProperty(id);
        this.amountProperty = new SimpleIntegerProperty(amount);
        this.nameProperty = new SimpleStringProperty(ItemNameStore.getItemName(id));
        this.imageObjectProperty = new SimpleObjectProperty<>();
    }

    public Item(int id) {
        this(id, 1);
    }

    public IntegerProperty idProperty()

    {
        return idProperty;
    }

    public IntegerProperty amountProperty() {
        return amountProperty;
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public int getId() {
        return idProperty.get();
    }

    public int getAmount() {
        return amountProperty.get();
    }

    public void setAmount(int amt) {
        if (amt < 0) {
            return;
        }
        amountProperty().set(amt);
    }

    public String getName() {
        return nameProperty.get();
    }

    public Image getImage() {
        if (imageObjectProperty.get() == null) {
            imageObjectProperty.set(ItemImageStore.getImageForId(getId()));
        }
        return imageObjectProperty.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return imageObjectProperty;
    }

}
