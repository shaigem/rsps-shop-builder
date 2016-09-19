package org.bitbucket.shaigem.rssb.model.item;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import org.bitbucket.shaigem.rssb.store.ItemImageStore;
import org.bitbucket.shaigem.rssb.store.ItemNameStore;

import java.util.Objects;


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
    public Image getImageOrFetch() {
        if (imageObjectProperty.get() == null) {
            setImage(ItemImageStore.getImageForId(getId()));
        }
        return imageObjectProperty.get();
    }

    public Image getImage() {
        return imageObjectProperty.get();
    }

    public void setImage(Image image) {
        imageObjectProperty.set(image);
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

    public Item copy() {
        Item item = new Item(this.getId(), this.getAmount());
        if (this.getImage() != null) {
            // if the image exists and was fetched already from file
            // then we can set the image to the copy and cache the image for later use
            // if the image does not exist there is no need to fetch the image from zip now as it is a waste
            // the image will get fetched when it is actually needed
            item.setImage(ItemImageStore.getAndCacheImageForId(this.getId(), this.getImage()));
        }
        return item;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getAmount());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        final Item other = (Item) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getAmount(), other.getAmount());
    }
}
