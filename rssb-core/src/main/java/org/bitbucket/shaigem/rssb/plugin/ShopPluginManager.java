package org.bitbucket.shaigem.rssb.plugin;

import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import java.net.URI;

/**
 * Created on 30/08/16.
 */
public class ShopPluginManager {

    public static final ShopPluginManager INSTANCE = new ShopPluginManager();
    public static final boolean DEBUG = true;
    private AbstractShopPlugin plugin;
    private PluginManagerUtil pluginManager;

    public void initialize(URI... pluginURIs) {
        final net.xeoh.plugins.base.PluginManager pm = PluginManagerFactory.createPluginManager();
        for (URI pluginURI : pluginURIs) {
            pm.addPluginsFrom(pluginURI, new OptionReportAfter());
        }
        pluginManager = new PluginManagerUtil(pm);
        //TODO ref issue #4 - create a user interface to allow for people to select the plugin!
        // For now just load the first plugin as debug
        plugin = (AbstractShopPlugin)
                pluginManager.getPlugins(ShopPlugin.class).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    public void shutdown() {
        if (pluginManager != null)
            pluginManager.shutdown();
    }

    public AbstractShopPlugin getLoadedPlugin() {
        return plugin;
    }

    public ShopFormat getLoadedFormat() {
        return plugin.getFormat();
    }
}
