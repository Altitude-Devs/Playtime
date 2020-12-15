package com.playtime.database;

import com.playtime.Playtime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static DatabaseConnection instance;

    public Connection connection;

    public String drivers, ip, port, database, username, password, tableName;

    public DatabaseConnection() throws SQLException  {

        this.drivers = Playtime.getInstance().getConfig().getString("drivers");
        this.ip = Playtime.getInstance().getConfig().getString("ip");
        this.port = Playtime.getInstance().getConfig().getString("port");
        this.database = Playtime.getInstance().getConfig().getString("database");
        this.username = Playtime.getInstance().getConfig().getString("username");
        this.password = Playtime.getInstance().getConfig().getString("password");
        this.tableName = Playtime.getInstance().getConfig().getString("server");

        instance = this;

        instance.openConnection();

        PlaytimeConnection.createPlaytimeTable();
        SeenConnection.createSeenTable();

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
                    "jdbc:" + drivers + "://" + ip + ":" + port + "/" + database, username,
                    password);
        }
    }

    public static Connection getConnection() {
        try {
            instance.openConnection();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return instance.connection;
    }

    public static void initialize() throws SQLException {
        instance = new DatabaseConnection();
    }
}
