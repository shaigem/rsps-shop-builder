package org.bitbucket.shaigem.rssb.store;

import com.github.benmanes.caffeine.cache.Caffeine;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.fs.archive.zip.ZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2015-08-13.
 */
public final class ItemImageStore {

    private static final String STORE_PATH = "./data/store.abyss/";

    private static final Image DEFAULT = new Image(
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
                    build(k -> retrieveImageFromFile((Integer) k));


    public static void setupStoreArchiveDetecter() {
        //we are only using truezip for images so we can just set it up here.
        TConfig.get().setArchiveDetector(
                new TArchiveDetector(
                        "abyss", new ZipDriver(IOPoolLocator.SINGLETON)
                ));
    }

    /**
     * Gets the image for the item's id from the cache.
     * If it does not exist, then fetch it from the zip file and cache it in the store.
     *
     * @param id the item's identifier
     * @return the item's image
     */
    public static Image getImageForId(int id) {
        return retrieveImageFromCache(id);
    }

    /**
     * Gets the image for the supplied item id from the cache.
     * If the image is not already cached, then cache the image that is provided instead.
     *
     * @param id    the item's identifier
     * @param image the item's image
     * @return the cached image or the <code>image</code> that is already provided
     */
    public static Image getAndCacheImageForId(int id, Image image) {
        return commonItemImageCache.get(id, k -> image);
    }

    /**
     * Retrieves the image from the zip file. This does not cache the image!
     *
     * @param id the item's identifier
     * @return the item's image
     */
    public static Image retrieveImageFromFile(int id) {
        try (TFileInputStream tFileInputStream =
                     new TFileInputStream(STORE_PATH + id + ".png")) {
            System.out.println("Retrieved from zip: " + id);
            return new Image(tFileInputStream);
        } catch (IOException e) {
            return DEFAULT;
        }
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
