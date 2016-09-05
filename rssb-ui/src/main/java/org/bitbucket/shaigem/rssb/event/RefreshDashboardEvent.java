package org.bitbucket.shaigem.rssb.event;

import org.bitbucket.shaigem.rssb.plugin.ShopFormatPlugin;

/**
 * Event to refresh the dashboard with a given format plugin.
 */
public class RefreshDashboardEvent {

    private ShopFormatPlugin addedPlugin;

    public RefreshDashboardEvent(ShopFormatPlugin addedPlugin) {
        this.addedPlugin = addedPlugin;
    }

    public ShopFormatPlugin getAddedPlugin() {
        return addedPlugin;
    }
}
