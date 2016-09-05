package org.bitbucket.shaigem.rssb.event;

import org.bitbucket.shaigem.rssb.ui.shop.ShopPresenter;

/**
 * Created on 02/09/16.
 */
public class ShopCloseEvent {

    private ShopPresenter closingShop;

    public ShopCloseEvent(ShopPresenter closingShop) {
        this.closingShop = closingShop;
    }

    public ShopPresenter getClosingShopPresenter() {
        return closingShop;
    }
}
