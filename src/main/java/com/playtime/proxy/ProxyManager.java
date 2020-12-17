package com.playtime.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.playtime.Playtime;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class ProxyManager {

    public static OfflinePlayer player;

    public static boolean isOnline = false;

    public static boolean isPlayerOnline(OfflinePlayer p) {
        return isOnline;
    }

    public static void requestOnlineStatus(OfflinePlayer p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            player = null;
            out.writeUTF("PlayerList");
            out.writeUTF("ALL");
            Bukkit.getServer().sendPluginMessage(Playtime.getInstance(), "BungeeCord", out.toByteArray());
            player = p;

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }



}
