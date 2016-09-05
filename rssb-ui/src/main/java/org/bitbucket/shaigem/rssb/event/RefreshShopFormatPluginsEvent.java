package org.bitbucket.shaigem.rssb.event;

/**
 * Event that notifies when the user has refreshed the dashboard for any added plugins.
 */
public class RefreshShopFormatPluginsEvent {

    /**
     * If the refresh added any more plugins.
     */
    private boolean added;

    public RefreshShopFormatPluginsEvent(boolean added) {
        this.added = added;
    }

    public boolean anyAdded() {
        return added;
    }

}
