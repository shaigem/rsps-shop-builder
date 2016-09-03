package org.bitbucket.shaigem.rssb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bitbucket.shaigem.rssb.model.shop.Shop;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * A repository that holds and manages shop definitions
 */
public final class ShopRepository {

    private ObservableList<Shop> masterShopDefinitions;

    @PostConstruct
    public void init() {
        masterShopDefinitions = FXCollections.observableArrayList();
    }

    public void populate(Collection<Shop> collection) {
        masterShopDefinitions.clear();
        masterShopDefinitions.addAll(collection);
    }

    public ObservableList<Shop> getMasterShopDefinitions() {
        return masterShopDefinitions;
    }
}
