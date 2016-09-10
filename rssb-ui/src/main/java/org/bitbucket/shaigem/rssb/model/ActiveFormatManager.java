package org.bitbucket.shaigem.rssb.model;

import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.plugin.ShopFormat;

/**
 * Created on 10/09/16.
 */
public class ActiveFormatManager {

    private BaseShopFormatPlugin formatPlugin;


    public void setFormatPlugin(BaseShopFormatPlugin formatPlugin) {
        this.formatPlugin = formatPlugin;
    }

    public BaseShopFormatPlugin getFormatPlugin() {
        return formatPlugin;
    }

    public ShopFormat getFormat() {
        return formatPlugin.getFormat();
    }
}
