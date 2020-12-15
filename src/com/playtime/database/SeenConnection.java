package com.playtime.database;

import com.playtime.Playtime;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class SeenConnection {

    public Playtime playtime;

    public SeenConnection(Playtime playtime) {
        this.playtime = playtime;
    }

    public static  void createSeenTable() {

        String sql = "CREATE TABLE IF NOT EXISTS last_seen (\n" + " uuid VARCHAR(36) NOT NULL,\n"
                + " seen INTEGER(12),\n" + " server VARCHAR(25),\n" + " PRIMARY KEY(UUID)\n);";

        try {

            Statement statement = DatabaseConnection.getConnection().createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void updateSeenTime(final UUID uuid, final String serverName, final boolean login) {

        final int seen = (int) (System.currentTimeMillis() / 1000);

        Bukkit.getScheduler().runTaskAsynchronously(Playtime.getInstance(), new Runnable() {

            @Override
            public void run() {

                if(login) {

                    String sql = "INSERT INTO last_seen (uuid, seen, server) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE seen = ?, server = ?";

                    try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {

                        statement.setString(1, uuid.toString());

                        statement.setInt(2, seen);

                        statement.setString(3, serverName.toLowerCase());

                        statement.setInt(4, seen);

                        statement.setString(5, serverName.toLowerCase());

                        statement.execute();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {

                    String sql = "INSERT INTO last_seen (uuid, seen, server) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE seen = ?";

                    try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {

                        statement.setString(1, uuid.toString());

                        statement.setInt(2, seen);

                        statement.setString(3, serverName.toLowerCase());

                        statement.setInt(4, seen);

                        statement.execute();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }


            }

        });

    }

    public static ResultSet getSeenTime(UUID uuid) throws SQLException {
        return getStringResult("SELECT seen,server FROM last_seen WHERE uuid = ?", uuid.toString());
    }

    private static ResultSet getStringResult(String query, String... parameters) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(query);
        for (int i = 1; i < parameters.length + 1; i++) {
            statement.setString(i, parameters[i - 1]);
        }

        return statement.executeQuery();
    }




}
