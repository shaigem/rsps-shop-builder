package org.bitbucket.shaigem.rssb.persistence;

import com.google.common.collect.ImmutableMap;
import javafx.scene.image.Image;
import org.bitbucket.shaigem.rssb.store.ItemImageStore;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 24/09/16.
 */
public final class ItemDatabase {

    private static ItemDatabase instance;

    private static final String DATABASE = "jdbc:sqlite:./data/item.sqlite3";

    private SQLiteConnectionPoolDataSource source = null;

    private ItemDatabase() {
        this.source = new SQLiteConnectionPoolDataSource();
        this.source.setUrl(ItemDatabase.DATABASE);
    }

    public ImmutableMap<Integer, String> getItemNames() {
        final String sql = "SELECT id, name FROM items";
        final Map<Integer, String> map = new HashMap<>();
        try {
            try (Connection conn = this.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    map.put(rs.getInt("id"), rs.getString("name"));
                }
                return ImmutableMap.copyOf(map);
            }
        } catch (SQLException e) {
            return ImmutableMap.of();
        }
    }

    public Image getItemImage(int id) {
        final String sql = "SELECT image FROM items WHERE id = ?";
        final int imageColumn = 1;
        try {
            try (Connection conn = this.connect();
                 PreparedStatement stmt = createPreparedStatement(sql, conn, id);
                 ResultSet rs = stmt.executeQuery()) {
                return new Image(new ByteArrayInputStream(rs.getBytes(imageColumn)));
            }
        } catch (SQLException e) {
            return ItemImageStore.DEFAULT;
        }
    }

    private PreparedStatement createPreparedStatement(String sql, Connection connection, int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    private Connection connect() throws SQLException {
        return this.source.getConnection();
    }

    public static ItemDatabase getInstance() {
        if (instance == null) {
            instance = new ItemDatabase();
        }
        return instance;
    }

}
