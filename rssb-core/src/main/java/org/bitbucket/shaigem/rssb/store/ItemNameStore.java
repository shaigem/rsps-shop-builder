package org.bitbucket.shaigem.rssb.store;

import com.google.common.collect.ImmutableMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2015-08-11.
 */
public final class ItemNameStore {

    //We create an empty map first
    private static ImmutableMap<Integer, String> namesMap = ImmutableMap.of();

    //TODO expose the item list so other people can replace it with their own

    public static void parseItemNames() throws Exception {
        Map<Integer, String> names = new HashMap<>();

        try (InputStream inputStream = ItemNameStore.class.getClassLoader()
                .getResourceAsStream("item/itemlist.txt")) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    inputStream))) {
                do {
                    String line = in.readLine();
                    if (line == null)
                        break;
                    if (!line.startsWith("//")) {
                        String splitedLine[] = line.split(" - ", 3);
                        int id = Integer.valueOf(splitedLine[0]);
                        String name = splitedLine[1];
                        names.put(id, name);
                    }
                } while (true);

            }
            namesMap = ImmutableMap.copyOf(names);

        }
    }


    public static String getItemName(int id) {
        return namesMap.getOrDefault(id, "Unknown");
    }

    public static ImmutableMap<Integer, String> getNamesMap() {
        return namesMap;
    }

}
