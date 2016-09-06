package org.bitbucket.shaigem.rssb.event;

import org.bitbucket.shaigem.rssb.ui.builder.shop.ShopPresenter;

/**
 * Created on 02/09/16.
 */
public class ShopSaveEvent {

    private final ShopPresenter shopEditorPresenter;
    private final boolean success;

    public ShopSaveEvent(ShopPresenter shopEditorPresenter, boolean success) {
        this.shopEditorPresenter = shopEditorPresenter;
        this.success = success;
    }

    public ShopPresenter getSavedShopPresenter() {
        return shopEditorPresenter;
    }

    /**
     * Successfully saved something.
     *
     * @return true if the shop had changes to be saved
     */
    public boolean onSuccess() {
        return success;
    }
}
