package com.playtime.database;

import com.playtime.Playtime;
import com.playtime.maps.Maps;
import com.playtime.proxy.ProxyManager;
import com.playtime.util.Utilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Database {

    public enum seenType {
        LAST,
        SERVER
    }

    public static void getPlaytimeMessage(final CommandSender sender, final UUID u, final boolean otherPlayer) {

        PlaytimeConnection.getPlaytime(new PlaytimeConnection.Callback<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(HashMap<String, Integer> done) {


                OfflinePlayer player = Bukkit.getOfflinePlayer(u);

                List<String> header = Playtime.getInstance().getConfig().getStringList("playtime-format-header");
                StringBuilder header_message = new StringBuilder();
                for (String s : header) {
                    if (otherPlayer) {
                        s = s.replaceFirst("%player%", player.getName() + "'s");
                    } else {
                        s = s.replaceFirst("%player%", "Your");
                    }
                    header_message.append(Utilities.format(s));
                    header_message.append("\n");
                }

                sender.sendMessage(header_message.toString());

                for (String server : Maps.serverNames) {
                    if (done.get(server) != null) {
                        String serverName = server;
                        sender.sendMessage(Utilities.format(Playtime.getInstance().getConfig().getString("playtime-format")
                                .replace("%server%", serverName.substring(0, 1).toUpperCase() + serverName.substring(1))
                                .replace("%time%", Utilities.convertTime(done.get(server)))));
                    }
                }

                List<String> footer = Playtime.getInstance().getConfig().getStringList("playtime-format-footer");
                StringBuilder footer_message = new StringBuilder();
                for (String s : footer) {
                    s = s.replaceFirst("%total%", Utilities.convertTime(done.get("TOTALTIME")));
                    footer_message.append(Utilities.format(s));
                    footer_message.append("\n");
                }

                sender.sendMessage(footer_message.toString());

            }

            @Override
            public void onFailure(Throwable cause) {

            }
        }, u);

    }

    public static void seenMessage(final CommandSender sender, UUID seenTimeUUID, String string) {
        try {

            final OfflinePlayer player = Bukkit.getOfflinePlayer(seenTimeUUID);

            ProxyManager.requestOnlineStatus(player);

            ResultSet seen = SeenConnection.getSeenTime(seenTimeUUID);

            if (seen.next()) {
                int seenTime = seen.getInt("seen");
                String getServerName = seen.getString("server");

                final long timeFormat = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000)
                        - TimeUnit.SECONDS.toMinutes(seenTime);
                //final String capServerName = getServerName.substring(0, 1).toUpperCase() + getServerName.substring(1);
                final String finalServerName = Playtime.getInstance().getConfig().getString("seen-servers-format." + getServerName.toLowerCase());

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Playtime.getInstance(), new Runnable() {

                    @Override
                    public void run() {

                        if(finalServerName != null) {
                            if (!Playtime.getInstance().getProxyManager().isPlayerOnline(player)) {

                                sender.sendMessage(Utilities.format(Playtime.getInstance().getConfig().getString("seen-format")
                                        .replace("%server%", finalServerName)
                                        .replace("%time%", Utilities.convertTime((int) timeFormat))
                                        .replace("%player%", player.getName())
                                        .replace("%online/offline%", Playtime.getInstance().getConfig().getString("seen-offline-format"))));


                            } else {

                                sender.sendMessage(Utilities.format(Playtime.getInstance().getConfig().getString("seen-format")
                                        .replace("%server%", finalServerName)
                                        .replace("%time%", Utilities.convertTime((int) timeFormat))
                                        .replace("%player%", player.getName())
                                        .replace("%online/offline%", Playtime.getInstance().getConfig().getString("seen-online-format"))));
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Something went wrong! Can't find server name in the config. Please report this to a admin.");
                        }
                    }

                }, 5L);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static String changeTimeFormat(OfflinePlayer targetPlayer, Integer newTime, Integer oldTime, String serverName) {
        List<String> format = Playtime.getInstance().getConfig().getStringList("messages.player-time-change");
        StringBuilder message = new StringBuilder();
        for (String s : format) {
            s = s.replaceFirst("%server%", serverName);
            s = s.replaceFirst("%newtime%", Utilities.convertTime(newTime));
            s = s.replaceFirst("%oldtime%", Utilities.convertTime(oldTime));
            s = s.replaceFirst("%player%", targetPlayer.getName());
            message.append(Utilities.format(s));
            message.append("\n");
        }

        return message.toString();

    }


}
