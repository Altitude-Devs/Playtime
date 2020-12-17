package com.playtime.database;

import com.playtime.Playtime;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;

public class DatabaseManager {


    public Playtime playtime;

    public Connection connection;

    public String driver, ip, port, database, username, password, tableName;

    public DatabaseManager(String driver, String ip, String port, String database, String username, String password,
                    String tableName, Playtime playtime) throws SQLException  {

        this.driver = driver;
        this.ip = ip;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
        this.playtime = playtime;

        openConnection();
        createPlaytimeTable();

    }


    public void openConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            connection = DriverManager.getConnection(
                    "jdbc:" + driver + "://" + ip + ":" + port + "/" + database, username,
                    password);
        }
    }

    public Connection getConnection() {
        try {
            openConnection();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return connection;
    }

    public void createPlaytimeTable() {

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName.toLowerCase() + "(\n" + " uuid VARCHAR(36) NOT NULL,\n"
                + " time INTEGER,\n" + " seen INTEGER(12),\n" + " online BIT,\n" + " PRIMARY KEY(UUID)\n);";

        try {

            Statement statement = getConnection().createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setPlaytime(final UUID u, final int time, final String tableName) {

        Bukkit.getScheduler().runTaskAsynchronously(playtime, new Runnable() {

            @Override
            public void run() {

                try {
                    // Player doesn't have a playtime on server
                    if (!getStringResult("SELECT * FROM " + tableName.toLowerCase() + " WHERE uuid = ?", u.toString()).next()) {

                        PreparedStatement insert = getConnection().prepareStatement(
                                "INSERT INTO " + tableName.toLowerCase() + " (uuid, time, seen) VALUES (?, ?, UNIX_TIMESTAMP(0))");
                        insert.setString(1, u.toString());
                        insert.setInt(2, time);
                        insert.execute();
                        insert.close();
                    } else {
                        PreparedStatement update = getConnection()
                                .prepareStatement("UPDATE " + tableName.toLowerCase() + " SET time = ? WHERE uuid = ?");
                        update.setInt(1, time);
                        update.setString(2, u.toString());
                        update.execute();
                        update.close();
                    }
                }catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateOnlinePlayer(final UUID u, final String tableName) {

        Bukkit.getScheduler().runTaskAsynchronously(playtime, new Runnable() {

            @Override
            public void run() {

                try {
                    // Player doesn't have a playtime on server
                    if (!getStringResult("SELECT * FROM " + tableName.toLowerCase() + " WHERE uuid = ?", u.toString()).next()) {
                        PreparedStatement insert = getConnection().prepareStatement(
                                "INSERT INTO " + tableName.toLowerCase() + " (uuid, time, seen, online) VALUES (?, 0, ?, ?)");
                        insert.setString(1, u.toString());
                        insert.setInt(2, (int) (System.currentTimeMillis() / 1000));
                        insert.setBoolean(3, true);
                        insert.execute();
                        insert.close();
                    } else {
                        PreparedStatement update = getConnection()
                                .prepareStatement("UPDATE " + tableName.toLowerCase() + " SET seen = ?, online = ? WHERE uuid = ?");
                        update.setInt(1, (int) (System.currentTimeMillis() / 1000));
                        update.setBoolean(2, true);
                        update.setString(3, u.toString());
                        update.execute();
                        update.close();
                    }
                }catch(SQLException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    public void updateOnlinePlayerTTF(final UUID u, final String tableName) {

        Bukkit.getScheduler().runTaskAsynchronously(playtime, new Runnable() {

            @Override
            public void run() {

                try {
                    // Player doesn't have a playtime on server
                    if (!getStringResult("SELECT * FROM " + tableName.toLowerCase() + " WHERE uuid = ?", u.toString()).next()) {
                        PreparedStatement insert = getConnection().prepareStatement(
                                "INSERT INTO " + tableName.toLowerCase() + " (uuid, time, seen, online) VALUES (?, NULL, ?, ?)");
                        insert.setString(1, u.toString());
                        insert.setInt(2, (int) (System.currentTimeMillis() / 1000));
                        insert.setBoolean(3, true);
                        insert.execute();
                        insert.close();
                    } else {
                        PreparedStatement update = getConnection()
                                .prepareStatement("UPDATE " + tableName.toLowerCase() + " SET seen = ?, online = ? WHERE uuid = ?");
                        update.setInt(1, (int) (System.currentTimeMillis() / 1000));
                        update.setBoolean(2, true);
                        update.setString(3, u.toString());
                        update.execute();
                        update.close();
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void updateOfflinePlayerTTF(final UUID u, final String tableName) {

        Bukkit.getScheduler().runTaskAsynchronously(playtime, new Runnable() {

            @Override
            public void run() {

                try {
                    // Player doesn't have a playtime on server
                    if (!getStringResult("SELECT * FROM " + tableName.toLowerCase() + " WHERE uuid = ?", u.toString()).next()) {
                        PreparedStatement insert = getConnection().prepareStatement(
                                "INSERT INTO " + tableName.toLowerCase() + " (uuid, time, seen, online) VALUES (?, NULL, ?, ?)");
                        insert.setString(1, u.toString());
                        insert.setInt(2, (int) (System.currentTimeMillis() / 1000));
                        insert.setBoolean(3, false);
                        insert.execute();
                        insert.close();
                    } else {
                        PreparedStatement update = getConnection()
                                .prepareStatement("UPDATE " + tableName.toLowerCase() + " SET seen = ?, online = ? WHERE uuid = ?");
                        update.setInt(1, (int) (System.currentTimeMillis() / 1000));
                        update.setBoolean(2, false);
                        update.setString(3, u.toString());
                        update.execute();
                        update.close();
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void updateOfflinePlayer(final UUID u, final String tableName) {

        Bukkit.getScheduler().runTaskAsynchronously(playtime, new Runnable() {

            @Override
            public void run() {

                try {
                    // Player doesn't have a playtime on server
                    if (!getStringResult("SELECT * FROM " + tableName.toLowerCase() + " WHERE uuid = ?", u.toString()).next()) {
                        PreparedStatement insert = getConnection().prepareStatement(
                                "INSERT INTO " + tableName.toLowerCase() + " (uuid, time, seen, online) VALUES (?, 0, ?, ?)");
                        insert.setString(1, u.toString());
                        insert.setInt(2, (int) (System.currentTimeMillis() / 1000));
                        insert.setBoolean(3, false);
                        insert.execute();
                        insert.close();
                    } else {
                        PreparedStatement update = getConnection()
                                .prepareStatement("UPDATE " + tableName.toLowerCase() + " SET seen = ?, online = ? WHERE uuid = ?");
                        update.setInt(1, (int) (System.currentTimeMillis() / 1000));
                        update.setBoolean(2, false);
                        update.setString(3, u.toString());
                        update.execute();
                        update.close();
                    }

                }catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public ResultSet getPlaytimeServer(UUID u, String server) throws SQLException {
        return getStringResult("SELECT time FROM " + server.toLowerCase() + " WHERE uuid = ?", u.toString());
    }

    public boolean hasPlayedBefore(UUID u) throws SQLException {
        List<String> tableNames = getServers();
        ResultSet users = null;
        for (String tables : tableNames) {
            users = getStringResult("SELECT * FROM " + tables + " WHERE uuid = ?", u.toString());
            if (users.next()) {
                return true;
            }
        }
        users.close();
        return false;

    }

    public Map<String, Integer> getServerTimes(UUID u) throws SQLException {
        Map<String, Integer> playtimes = new HashMap<>();
        List<String> tableNames = getServers();

        ResultSet users = null;
        for (String table : tableNames) {
            users = getStringResult("SELECT " + table + ".time FROM " + table + " WHERE " + table + ".uuid = ?",
                    u.toString());
            while (users.next()) {
                Integer idParent = (Integer) users.getObject("time");
                if (idParent != null) {
                    playtimes.put(table, users.getInt("time"));
                }

            }

        }

        users.close();
        return playtimes;
    }

    public ResultSet getSeenTime(UUID u) throws SQLException {
        return getStringResult("SELECT seen,server FROM last_seen WHERE uuid = ?", u.toString());
    }

    public Map<String, Integer> getSeenTimes(UUID u) throws SQLException {
        Map<String, Integer> seentimes = new HashMap<>();
        List<String> tableNames = getServers();
        ResultSet users = null;
        for (String table : tableNames) {
            users = getStringResult("SELECT " + table + ".seen FROM " + table + " WHERE " + table + ".uuid = ?",
                    u.toString());
            while (users.next()) {
                seentimes.put(table, users.getInt("seen"));
            }

        }

        users.close();
        return seentimes;
    }

    public Integer getTotaltimes(UUID u) throws SQLException {
        int time = 0;
        List<String> tableNames = getServers();

        ResultSet rs = null;
        for (String table : tableNames) {

            rs = getStringResult("SELECT " + table + ".time FROM " + table + " WHERE " + table + ".uuid = ?",
                    u.toString());

            while (rs.next()) {
                time += rs.getInt("TIME");
            }

        }

        rs.close();
        return time;

    }

    public List<String> getServers() throws SQLException {
        List<String> servers = new ArrayList<String>();

        ResultSet rs = getStringResult("SELECT table_name FROM information_schema.tables where table_schema = ?",
                playtime.getConfig().getString("database"));

        while (rs.next()) {
            String tableName = rs.getString("table_name");
            if (!tableName.equalsIgnoreCase("old_playtime")) {
                servers.add(tableName);
            }
        }

        rs.close();
        return servers;

    }

    public ResultSet getStringResult(String query, String... parameters) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(query);
        for (int i = 1; i < parameters.length + 1; i++) {
            statement.setString(i, parameters[i - 1]);
        }

        return statement.executeQuery();
    }

    // VALLEY AND SUMMIT MATH

    public ResultSet getOldPlaytimeServer(UUID uuid, String server) throws SQLException {
        return getStringResult("SELECT time FROM old_playtime WHERE uuid = ? AND server = ?", uuid.toString(), server);
    }

    public void deleteOldPlaytime(UUID u, String server) throws SQLException {
        if (inOldDataBase(u, server)) {
            PreparedStatement insert = getConnection().prepareStatement("DELETE FROM old_playtime WHERE uuid = ? AND server = ?");
            insert.setString(1, u.toString());
            insert.setString(2, server);
            insert.executeUpdate();
            insert.close();
        }

    }

    public boolean inOldDataBase(UUID u, String server) {
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
}
