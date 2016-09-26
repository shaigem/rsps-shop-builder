package org.bitbucket.shaigem.rssb.store;

import com.github.benmanes.caffeine.cache.Caffeine;
import javafx.scene.image.Image;
import org.bitbucket.shaigem.rssb.persistence.ItemDatabase;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2015-08-13.
 */
public final class ItemImageStore {

    public static final Image DEFAULT = new Image(
            ItemImageStore.class.getClassLoader().getResourceAsStream("images/question.png"));

    /**
     * A cache that holds the most commonly used item's image.
     */
    private static final com.github.benmanes.caffeine.cache.LoadingCache<Integer, Image>
            commonItemImageCache =
            Caffeine.newBuilder().
                    maximumSize(512).expireAfterAccess(5, TimeUnit.MINUTES).softValues().
                    removalListener((key, value, cause) ->
                            System.out.println("Removed: " + key + " : " + value + " " + cause)).
                    build(k -> getImageFromDatabase((Integer) k));

    /**
     * Gets the image for the item's id from the cache.
     * If it does not exist, then fetch it from the database and cache it in the store.
     *
     * @param id the item's identifier
     * @return the item's image
     */
    public static Image getImageForId(int id) {
        return retrieveImageFromCache(id);
    }

    /**
     * If the specified item id is not present in the cache,
     * then cache it with the provided image and returns with the given image else returns the cached image.
     *
     * @param id    the item's identifier
     * @param image the item's image
     * @return the cached image or the <code>image</code> that is already provided
     */
    public static Image cacheIfAbsent(int id, Image image) {
        return commonItemImageCache.get(id, k -> image);
    }

    /**
     * Retrieves the image from the database. This does not cache the image!
     *
     * @param id the item's identifier
     * @return the item's image
     */
    public static Image getImageFromDatabase(int id) {
        return ItemDatabase.getInstance().getItemImage(id);
    }

    /**
     * If the image is present in the cache, then load it. Otherwise retrieve the image from the database.
     * Note that when checking if the image is present in the image store, it will not compute a new value for it.
     *
     * @param id the item's identifier
     * @return the item's image
     */
    public static Image getImageIfPresent(int id) {
        Image image = commonItemImageCache.getIfPresent(id);
        if (image == null) {
            image = getImageFromDatabase(id);
        }
        return image;
    }

    /**
     * Tries to retrieve the requested item's image from the cache.
     * If the requested item's image is not already cached then
     * it will be retrieved and cached.
     *
     * @param id the item's identifier
     * @return the item's image
     */
    private static Image retrieveImageFromCache(int id) {
        System.out.println("Cache hit: " + id);
        return commonItemImageCache.get(id);
    }

}
