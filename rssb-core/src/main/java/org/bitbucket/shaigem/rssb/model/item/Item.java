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

    private IntegerProperty id;
    private IntegerProperty amount;
    private StringProperty name;
    private ObjectProperty<Image> imageObjectProperty;

    public Item(int id, int amount) {
        this.id = new SimpleIntegerProperty(id);
        this.amount = new SimpleIntegerProperty(amount);
        this.name = new SimpleStringProperty();
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
        return id;
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getId() {
        return id.get();
    }

    public int getAmount() {
        return amount.get();
    }

    public void setAmount(int amt) {
        if (amt < 0) {
            return;
        }
        amount.set(amt);
    }

    public String getName() {
        if (name.get() == null) {
            name.set(ItemNameStore.getItemName(getId()));
        }
        return name.get();
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
            setImage(ItemImageStore.getImageForId(getId()));
        }
        return imageObjectProperty.get();
    }


    public void setImage(Image image) {
        imageObjectProperty.set(image);
    }

    /**
     * Lazily load the image from a file.
     * <p>
     * Note that images loaded by this method are not cached globally in the store and
     * can result to unnecessary reads from the zip file.</p>
     *
     * @return the item's image
     */
    public Image getImageFromFile() {
        if (imageObjectProperty.get() == null) {
            imageObjectProperty.set(ItemImageStore.getImageFromZipFile(getId()));
        }
        return imageObjectProperty.get();
    }

    public Item copy() {
        Item item = new Item(this.getId(), this.getAmount());
        item.name.set(this.getName());
        if (this.getImageNoFetch() != null) {
            // if the image exists and was fetched already from file
            // then we can set the image to the copy and cache the image for later use
            // if the image does not exist there is no need to fetch the image from zip now as it is a waste
            // the image will get fetched when it is actually needed
            item.setImage(ItemImageStore.cacheIfAbsent(this.getId(), this.getImageNoFetch()));
        }
        return item;
    }

    /**
     * Gets the image. If the image does not exist then this does not load it.
     *
     * @return the image
     */
    private Image getImageNoFetch() {
        return imageObjectProperty.get();
    }

    @Override
    public String toString() {
        return "[" + getId() + "] " + getName();
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
