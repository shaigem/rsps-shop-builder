package org.bitbucket.shaigem.rssb.event;

import org.bitbucket.shaigem.rssb.plugin.BaseShopFormatPlugin;

/**
 * Event to notify when the active format plugin gets changed.
 */
public class ActiveFormatPluginChangedEvent {

    private BaseShopFormatPlugin formatPlugin;

    public ActiveFormatPluginChangedEvent(BaseShopFormatPlugin formatPlugin) {
        this.formatPlugin = formatPlugin;
    }

    public BaseShopFormatPlugin getFormatPlugin() {
        return formatPlugin;
    }
}
