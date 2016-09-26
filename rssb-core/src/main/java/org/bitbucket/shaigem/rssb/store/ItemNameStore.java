package org.bitbucket.shaigem.rssb.store;

import com.google.common.collect.ImmutableMap;
import org.bitbucket.shaigem.rssb.persistence.ItemDatabase;

/**
 * Created on 2015-08-11.
 */
public final class ItemNameStore {

    //We create an empty map first
    private static ImmutableMap<Integer, String> namesMap = ImmutableMap.of();

    public static void parseItemNames() throws Exception {
       namesMap = ImmutableMap.copyOf(ItemDatabase.getInstance().getItemNames());
    }

    public static String getItemName(int id) {
        return namesMap.getOrDefault(id, "Unknown");
    }

    public static ImmutableMap<Integer, String> getNamesMap() {
        return namesMap;
    }

}
