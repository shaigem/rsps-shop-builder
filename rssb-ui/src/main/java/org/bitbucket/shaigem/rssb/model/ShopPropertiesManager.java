package org.bitbucket.shaigem.rssb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Pair;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.bitbucket.shaigem.rssb.ui.shop.ShopPresenter;
import org.controlsfx.control.PropertySheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages the caching and retrieval of shop properties that are currently being edited
 * (open as a tab or current selected tab).
 * <p>When a shop has been opened to edit, its properties must be cached.
 * This is because the properties editor is not apart of the shop editor and
 * so every time a tab with a shop gets selected,
 * the properties view must be refreshed with the shop's properties. </p>
 * <p>
 * We cache the properties just in case there are unsaved changes to the properties
 * and so it won't cause those changes to be lost when the user selects another shop. </p>
 * Created on 02/09/16.
 */
public final class ShopPropertiesManager {

    private static final Logger LOG = LoggerFactory.getLogger(ShopPropertiesManager.class);

    private ObservableMap<ShopPresenter, Pair<Shop, ObservableList<PropertySheet.Item>>>
            propertiesMap = FXCollections.observableHashMap();

    /**
     * Caches the properties for later use. Note that normally we would use presenter.getShop()
     * but the properties editor uses a copied version so we would use that instead.
     *
     * @param presenter the editor instance used to edit the shop
     * @param shop      the shop to cache
     * @param items     the property sheet items containing the shop properties
     */
    public final void cacheProperties
    (ShopPresenter presenter, Shop shop, ObservableList<PropertySheet.Item> items) {
        // presenter is only used as a key to get the pair
        Pair<Shop, ObservableList<PropertySheet.Item>> shopPair = new Pair<>(shop, items);
        propertiesMap.put(presenter, shopPair);
        LOG.debug("Cached properties: " + presenter.getShop() + ". :" + propertiesMap);
    }

    /**
     * Remove the specified shop and its properties from cache.
     *
     * @param presenter the editor instance used to edit the shop
     */
    public final void removeProperties(ShopPresenter presenter) {
        propertiesMap.remove(presenter);
        LOG.debug("Removed properties for: " + presenter.getShop() + ". ");
    }

    /**
     * Gets the cached properties using the editor presenter as a key.
     *
     * @param presenter the shop editor presenter
     * @return a <code>Pair</code> with a shop linking with its properties
     */
    public final Pair<Shop, ObservableList<PropertySheet.Item>> getCachedProperties
    (ShopPresenter presenter) {
        LOG.debug("Retrieved properties from " + presenter.getShop());
        return propertiesMap.get(presenter);
    }

    public final void clear() {
        propertiesMap.clear();
    }

    public final boolean hasCached(ShopPresenter presenter) {
        return propertiesMap.containsKey(presenter);
    }

}

