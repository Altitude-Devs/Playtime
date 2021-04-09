package com.playtime;

import com.playtime.commands.PlaytimeCMD;
import com.playtime.commands.SeenCMD;
import com.playtime.database.PlanDatabaseConnection;
import com.playtime.database.PlaytimeDatabaseConnection;
import com.playtime.database.DatabaseManager;
import com.playtime.database.PlaytimeConnection;
import com.playtime.events.LoginEvent;
import com.playtime.events.LogoutEvent;
import com.playtime.events.OldTimes;
import com.playtime.maps.Maps;
import com.playtime.proxy.ProxyManager;
import com.playtime.task.AutoRank;
import com.playtime.task.AutoSave;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Playtime extends JavaPlugin implements PluginMessageListener {

    public PlaytimeConnection playtimeConnection;
    public DatabaseManager manager;
    public PlaytimeDatabaseConnection database;
    public AutoSave save;
    public OldTimes oldTimes;

    public static Playtime instance;

    public ProxyManager proxyManager;

    public static Permission perms = null;

    @Override
    public void onEnable() {

        instance = this;

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        setupPermissions();

        saveDefaultConfig();

        getCommand("playtime").setExecutor(new PlaytimeCMD(this));
        getCommand("pt").setExecutor(new PlaytimeCMD(this));
        getCommand("seen").setExecutor(new SeenCMD(this));

        getServer().getPluginManager().registerEvents(new LogoutEvent(this), this);
        getServer().getPluginManager().registerEvents(new LoginEvent(this), this);

        try {

            database.initialize();
            PlanDatabaseConnection.initialize();

            if (getConfig().getBoolean("tracktime") == true) {
                AutoSave autoSave = new AutoSave(this);
                autoSave.runTaskTimerAsynchronously(this, getConfig().getInt("auto-save") * 20 * 60,
                        getConfig().getInt("auto-save") * 20 * 60);

                AutoRank autoRank = new AutoRank(this);
                autoRank.runTaskTimer(this, getConfig().getInt("auto-rank") * 20 * 60,
                        getConfig().getInt("auto-rank") * 20 * 60);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Maps.onlineTime.put(player.getUniqueId(),
                            TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()));
                }

            }

            if (Maps.serverNames.isEmpty()) {
                PlaytimeConnection.getServerNames();
                //Playtime.getInstance().getLogger().info("Successfully loaded and cache data from the database.");
            }

        } catch (SQLException e) {
            getLogger().severe("*** Could not connect to the database. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
        }

    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("tracktime")) {

            getLogger().info("Saving players...");
            for (Player players : Bukkit.getOnlinePlayers()) {
                PlaytimeConnection.updatePlayerPlaytime(players.getUniqueId(), getServerName(), true);
                getAutoSave().playerLogout(players.getUniqueId());
                Maps.onlineTime.remove(players.getUniqueId());

            }

            getLogger().info("All player times were saved successfully");

            try {
                PlaytimeDatabaseConnection.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    public static Playtime getInstance() {
        return instance;
    }

    public static String getServerName() {
        return getInstance().getConfig().getString("server");
    }

    public PlaytimeDatabaseConnection getDatabase() {
        return database;
    }

    //public DatabaseManager getDatabaseManager() {
    // return manager;
    //}

    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public AutoSave getAutoSave() {
        return save;
    }

    public OldTimes getOldTimes() {
        return oldTimes;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String subChannel = in.readUTF();
            if (subChannel.equals("PlayerList")) {
                String server = in.readUTF(); // The name of the server you got the player list of, as given in args.
                String[] playerList = in.readUTF().split(", ");

                ProxyManager.isOnline = false;
                if (ProxyManager.player != null && ProxyManager.player.getName() != null && Arrays.asList(playerList).contains(ProxyManager.player.getName())) {
                    ProxyManager.isOnline = true;
                }
            }
        } catch (EOFException localEOFException) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
