package org.bitbucket.shaigem.rssb.plugin;

import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import java.net.URI;

/**
 * Created on 30/08/16.
 */
public class ShopPluginManager {

    public static final boolean DEBUG = true;

    private static PluginManagerUtil pluginManager;

    public static ShopFormat loadedShop() {
        return pluginManager.getPlugins(ShopFormat.class).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    public static void initialize(URI... pluginURIs) {
        final net.xeoh.plugins.base.PluginManager pm = PluginManagerFactory.createPluginManager();
        for (URI pluginURI : pluginURIs) {
            pm.addPluginsFrom(pluginURI, new OptionReportAfter());
        }
        pluginManager = new PluginManagerUtil(pm);
    }

    public static void shutdown() {
        if (pluginManager != null)
            pluginManager.shutdown();
    }
}
