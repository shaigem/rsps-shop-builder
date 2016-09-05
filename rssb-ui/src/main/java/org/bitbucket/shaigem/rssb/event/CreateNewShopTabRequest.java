package org.bitbucket.shaigem.rssb.event;

import org.bitbucket.shaigem.rssb.model.shop.Shop;

/**
 * Created on 04/09/16.
 */
public class CreateNewShopTabRequest {

    private Shop shop;

    public CreateNewShopTabRequest(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }
}
