package com.playtime.database;

import com.playtime.Playtime;
import com.playtime.maps.Maps;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlaytimeConnection {

    public Playtime playtime;

    public PlaytimeConnection(Playtime playtime) {
        this.playtime = playtime;
    }

    public static void createPlaytimeTable() {

        String sql = "CREATE TABLE IF NOT EXISTS " + Playtime.getServerName().toLowerCase() + "(\n" + " uuid VARCHAR(36) NOT NULL,\n"
                + " time INTEGER,\n" + " seen INTEGER(12),\n" + " online BIT,\n" + " PRIMARY KEY(UUID)\n);";

        try {

            Statement statement = DatabaseConnection.getConnection().createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void setPlaytime(final UUID uuid, final int time, final String serverName) {

        String sql = "INSERT INTO " + serverName.toLowerCase() + " (uuid, time, seen) VALUES (?, ?, UNIX_TIMESTAMP(0)) " + time;

        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            statement.setInt(2, time);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static void getServerNames() {

        Bukkit.getScheduler().runTaskAsynchronously(Playtime.getInstance(), new Runnable() {


            @Override
            public void run() {

                String sql = "SELECT table_name FROM information_schema.tables where table_schema = ?";

                try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {

                    statement.setString(1, Playtime.getInstance().getConfig().getString("database"));

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        String tableName = rs.getString("table_name");
                        if (!tableName.equalsIgnoreCase("old_playtime") && !tableName.equalsIgnoreCase("last_seen")
                                && !tableName.equalsIgnoreCase("staffpt")) {
                            Maps.serverNames.add(tableName);
                        } else {
                            Maps.otherTableNames.add(tableName);
                        }

                    }

                    rs.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public static void updatePlayerPlaytime(final UUID uuid, final String server, final boolean trackTime) {


        String sql = "INSERT INTO " + server.toLowerCase() + " (uuid, time, seen) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE seen = " + (int) (System.currentTimeMillis() / 1000);

        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            if (trackTime) {
                statement.setInt(2, 0);
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }

            statement.setInt(3, (int) (System.currentTimeMillis() / 1000));

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void getPlaytime(final Callback<HashMap<String, Integer>> callback, final UUID uuid) {

        final HashMap<String, Integer> playtimes = new HashMap<>();

        final List<String> servers = Maps.serverNames;

        Bukkit.getScheduler().runTaskAsynchronously(Playtime.getInstance(), new Runnable() {

            int totalTime = 0;

            @Override
            public void run() {


                for (final String server : servers) {

                    final String sql = "SELECT " + server + ".time FROM " + server + " WHERE " + server + ".uuid = ?";

                    try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {

                        statement.setString(1, uuid.toString());

                        ResultSet resultSet = statement.executeQuery();

                        while (resultSet.next()) {

                            totalTime += resultSet.getInt("time");

                            Integer idParent = (Integer) resultSet.getObject("time");
                            if (idParent != null) {
                                playtimes.put(server, resultSet.getInt("time"));
                            }
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                playtimes.put("TOTALTIME", totalTime);


                Bukkit.getScheduler().runTaskAsynchronously(Playtime.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onSuccess(playtimes);
                        } catch (Exception ex) {
                            callback.onFailure(ex.getCause());
                        }
                    }
                });
            }
        });
    }

    public static ResultSet getPlaytimeServer(UUID u, String server) throws SQLException {
        return getStringResult("SELECT time FROM " + server.toLowerCase() + " WHERE uuid = ?", u.toString());
    }

    public static boolean hasPlayedBefore(UUID uuid) throws SQLException {

        final String sql = "SELECT * FROM last_seen WHERE uuid = ?";

        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                return true;
            }
        }

        return false;

    }

    public static Integer getTotaltimes(UUID u) throws SQLException {
        int time = 0;
        List<String> tableNames = Maps.serverNames;

        ResultSet rs;
        for (String table : tableNames) {

            rs = getStringResult("SELECT " + table + ".time FROM " + table + " WHERE " + table + ".uuid = ?",
                    u.toString());

            while (rs.next()) {
                time += rs.getInt("TIME");
            }

        }

        return time;

    }

    private static ResultSet getStringResult(String query, String... parameters) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(query);
        for (int i = 1; i < parameters.length + 1; i++) {
            statement.setString(i, parameters[i - 1]);
        }

        return statement.executeQuery();
    }

    public static ResultSet getOldPlaytimeServer(UUID uuid, String server) throws SQLException {
        return getStringResult("SELECT time FROM old_playtime WHERE uuid = ? AND server = ?", uuid.toString(), server);
    }

    public static void deleteOldPlaytime(UUID u, String server) throws SQLException {
        if (inOldDataBase(u, server)) {
            PreparedStatement insert = DatabaseConnection.getConnection().prepareStatement("DELETE FROM old_playtime WHERE uuid = ? AND server = ?");
            insert.setString(1, u.toString());
            insert.setString(2, server);
            insert.executeUpdate();
            insert.close();
        }

    }

    public static boolean inOldDataBase(UUID u, String server) {
        try {
            if (getStringResult("SELECT * FROM old_playtime WHERE uuid = ? AND server = ?", u.toString(), server)
                    .next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }


    public interface Callback<T> {
        void onSuccess(T done);

        void onFailure(Throwable cause);
    }


}
