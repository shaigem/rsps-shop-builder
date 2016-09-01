package org.bitbucket.shaigem.rssb.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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

    private static final Image DEFAULT = new Image(
            ItemImageStore.class.getClassLoader().getResourceAsStream("images/question.png"));

    //TODO we might not even have to cache the images at all
    private static final Cache<Integer, Image> imageCache =
            CacheBuilder.newBuilder().maximumSize(3500).expireAfterAccess
                    (2, TimeUnit.MINUTES).
                    build();


    public static void setupStoreArchiveDetecter() {
        //we are only using truezip for images so we can just set it up here.
        TConfig.get().setArchiveDetector(
                new TArchiveDetector(
                        "abyss", new ZipDriver(IOPoolLocator.SINGLETON)
                ));
    }

    public static Image getImageForId(int id) {
        if (imageCache.getIfPresent(id) == null) {
            return retrieveImageFromStore(id);
        }
        return imageCache.getIfPresent(id);
    }


    private static Image retrieveImageFromStore(int id) {
        try {
            try (TFileInputStream tFileInputStream =
                         new TFileInputStream("./data/store.abyss/" + id + ".png")) {
                Image image = new Image(tFileInputStream);
                imageCache.put(id, image);
                return image;
            }
        } catch (IOException e) {
            return DEFAULT;

        }
    }
}
