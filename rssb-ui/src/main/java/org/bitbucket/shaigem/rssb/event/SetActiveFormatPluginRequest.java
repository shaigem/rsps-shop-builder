package org.bitbucket.shaigem.rssb.event;

import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;
import org.bitbucket.shaigem.rssb.ui.dashboard.container.FormatDashboardTile;

/**
 * Created on 03/09/16.
 */
public class SetActiveFormatPluginRequest {

    private FormatDashboardTile source;
    private BaseShopFormatPlugin shopFormatPlugin;

    public SetActiveFormatPluginRequest(FormatDashboardTile source, BaseShopFormatPlugin shopFormatPlugin) {
        this.source = source;
        this.shopFormatPlugin = shopFormatPlugin;
    }

    public FormatDashboardTile getSource() {
        return source;
    }

    public BaseShopFormatPlugin getShopFormatPlugin() {
        return shopFormatPlugin;
    }
}
