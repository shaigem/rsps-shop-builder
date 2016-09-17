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


    public Item(int id, Image image) {
        this(id, 1);
        imageObjectProperty.set(image);
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

    /**
     * Lazily loads the image from the store.
     * <p>The difference between this method and <code>getImageFromFile</code>
     * is that the image will be retrieved from the file
     * and cached in the store for reuse purposes.</p>
     *
     * @return the item's image.
     */
    public Image getImage() {
        if (imageObjectProperty.get() == null) {
            imageObjectProperty.set(ItemImageStore.getImageForId(getId()));
        }
        return imageObjectProperty.get();
    }

    /**
     * Lazily load the image from a file. This should <B>ONLY</B> be used for the item list images.
     *
     * @return the item's image
     */
    public Image getImageFromFile() {
        if (imageObjectProperty.get() == null) {
            imageObjectProperty.set(ItemImageStore.retrieveImageFromFile(getId()));
        }
        return imageObjectProperty.get();
    }

    //public ObjectProperty<Image> imageProperty() {
    //  return imageObjectProperty;
    // }

}
