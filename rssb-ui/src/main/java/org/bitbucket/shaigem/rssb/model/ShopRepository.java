package org.bitbucket.shaigem.rssb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bitbucket.shaigem.rssb.event.ActiveFormatPluginChangedEvent;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.sejda.eventstudio.DefaultEventStudio;
import org.sejda.eventstudio.annotation.EventListener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;

/**
 * A repository that holds and manages shop definitions
 */
public final class ShopRepository {

    @Inject
    DefaultEventStudio eventStudio;

    private ObservableList<Shop> masterShopDefinitions;

    @PostConstruct
    public void init() {
        masterShopDefinitions = FXCollections.observableArrayList();
        eventStudio.addAnnotatedListeners(this);
    }

    public void populate(Collection<Shop> collection) {
        masterShopDefinitions.clear();
        masterShopDefinitions.addAll(collection);
    }

    @EventListener
    private void onActiveFormatPluginChanged(ActiveFormatPluginChangedEvent event) {
        masterShopDefinitions.clear();
    }

    public ObservableList<Shop> getMasterShopDefinitions() {
        return masterShopDefinitions;
    }
}
