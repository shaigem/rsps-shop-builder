package org.bitbucket.shaigem.rssb.plugin;

import net.xeoh.plugins.base.Plugin;
import org.bitbucket.shaigem.rssb.model.shop.Shop;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

/**
 * Created on 30/08/16.
 */
public interface ShopFormat<S extends Shop> extends Plugin {

    ArrayList<S> load(@NotNull File selectedFile);


}
