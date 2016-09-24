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

    private int id;
    private Integer _amount;
    private IntegerProperty amount;
    private String name;
    private Image image;

    public Item(int id, int amount) {
        this.id = id;
        this._amount = amount;
    }

    public Item(int id) {
        this(id, 1);
    }

    public Item(int id, Image image) {
        this(id, 1);
        this.image = image;
    }

    public IntegerProperty amountProperty() {
        if (amount == null) {
            amount = new SimpleIntegerProperty(this, "amount", _amount);
            _amount = null;
        }
        return amount;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount == null ? _amount : amount.get();
    }

    public void setAmount(int amt) {
        if (amt < 0) {
            return;
        }
        if (amount == null) {
            _amount = amt;
        } else {
            amount.set(amt);
        }
    }

    public String getName() {
        if (name == null) {
            name = (ItemNameStore.getItemName(getId()));
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (image == null) {
            setImage(ItemImageStore.getImageForId(getId()));
        }
        return image;
    }


    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Lazily load the image from the store or zip file.
     * <p>
     * Note that images loaded by this method are not cached globally in the store and
     * can result to unnecessary reads from the zip file if the image was not already present in the
     * store.</p>
     *
     * @return the item's image
     */
    public Image getImageFromFile() {
        if (image == null) {
            image = (ItemImageStore.getImageIfPresent(getId()));
        }
        return image;
    }

    public Item copy() {
        Item item = new Item(this.getId(), this.getAmount());
        item.setName(this.getName());
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
        return image;
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
