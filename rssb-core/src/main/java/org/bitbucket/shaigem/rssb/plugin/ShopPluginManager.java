package org.bitbucket.shaigem.rssb.plugin;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginInformation;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

/**
 * Created on 30/08/16.
 */
public class ShopPluginManager {

    public static final ShopPluginManager INSTANCE = new ShopPluginManager();
    public static final boolean DEBUG = true;

    private PluginManagerUtil pluginManager;

    private PluginInformation pluginInformation;

    public void initialize(URI... pluginURIs) {
        final net.xeoh.plugins.base.PluginManager pm = PluginManagerFactory.createPluginManager();
        for (URI pluginURI : pluginURIs) {
            pm.addPluginsFrom(pluginURI, new OptionReportAfter());
        }
        pluginManager = new PluginManagerUtil(pm);
        pluginInformation = pluginManager.getPlugin(PluginInformation.class);
    }

    public final Optional<String> getAuthorForPlugin(Plugin plugin) {
        return pluginInformation.getInformation
                (PluginInformation.Information.AUTHORS, plugin).stream().findFirst();
    }

    public final Optional<String> getVersionForPlugin(Plugin plugin) {
        return pluginInformation.getInformation
                (PluginInformation.Information.VERSION, plugin).stream().findFirst();
    }

    public final Collection<ShopFormatPlugin> getLoadedPlugins() {
        return pluginManager.getPlugins(ShopFormatPlugin.class);
    }

    public void shutdown() {
        if (pluginManager != null)
            pluginManager.shutdown();
    }

}
