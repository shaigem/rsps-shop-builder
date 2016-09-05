package org.bitbucket.shaigem.rssb.plugin;

import com.google.common.collect.ImmutableSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import net.xeoh.plugins.base.PluginInformation;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import java.io.File;
import java.net.URI;

/**
 * Created on 30/08/16.
 */
public final class RSSBPluginManager {

    private RSSBPluginManager() {

    }

    public static final RSSBPluginManager INSTANCE = new RSSBPluginManager();

    public static final URI DEFAULT_SHOP_PLUGINS_URI = RSSBPluginManager.DEBUG ?
            new File("rssb-plugin-matrix/target/classes/").toURI() :
            new File(System.getProperty("user.dir") + "/plugins/").toURI();


    public static final boolean DEBUG = true;

    private PluginManagerUtil pluginManager;

    private PluginInformation pluginInformation;

    private ObservableSet<ShopFormatPlugin> pluginSet = FXCollections.observableSet();

    public void initialize(URI... pluginURIs) {
        final net.xeoh.plugins.base.PluginManager pm = PluginManagerFactory.createPluginManager();
        for (URI pluginURI : pluginURIs) {
            pm.addPluginsFrom(pluginURI, new OptionReportAfter());
        }
        pluginManager = new PluginManagerUtil(pm);
        pluginInformation = pluginManager.getPlugin(PluginInformation.class);
        pluginSet.addAll(getLoadedPlugins());
    }

    /**
     * Loads a plugin given its URI. Note that this should only be used to load individual plugins.
     *
     * @param pluginURI the plugin's URI
     * @return true if plugins were found
     */
    public boolean load(URI pluginURI) {
        pluginManager.addPluginsFrom(pluginURI, new OptionReportAfter());
        return pluginSet.addAll(getLoadedPlugins());
    }

    /**
     * Scans the shop plugins directory for any new plugins.
     * * @return true if plugins were found
     */
    public boolean refreshShopFormatPlugins() {
        pluginManager.addPluginsFrom(DEFAULT_SHOP_PLUGINS_URI, new OptionReportAfter());
        return pluginSet.addAll(getLoadedPlugins());
    }


    public void initializeShopFormatPlugins() {
        initialize(DEFAULT_SHOP_PLUGINS_URI);

    }

    public void addListenerToPluginSet(SetChangeListener<ShopFormatPlugin> changeListener) {
        pluginSet.addListener(changeListener);
    }

    public ImmutableSet<ShopFormatPlugin> getLoadedPlugins() {
        return ImmutableSet.copyOf(pluginManager.getPlugins(ShopFormatPlugin.class));
    }

    public void shutdown() {
        if (pluginManager != null)
            pluginManager.shutdown();
    }

}
