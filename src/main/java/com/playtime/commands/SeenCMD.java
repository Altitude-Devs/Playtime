package com.playtime.commands;

import com.playtime.Playtime;
import com.playtime.database.Database;
import com.playtime.database.PlaytimeConnection;
import com.playtime.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class SeenCMD implements CommandExecutor {

    private Playtime playtime;

    public SeenCMD(Playtime playtime) {
        this.playtime = playtime;
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {

            if (cmd.getName().equalsIgnoreCase("seen")) {
                if (sender.hasPermission("playtime.seen")) {
                    if (args.length == 0) {
                        sender.sendMessage(
                                Utilities.format(playtime.getConfig().getString("messages.invalid-seen-command")));
                    }

                    if (args.length == 1) {
                        final OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);

                            try {
                                if(PlaytimeConnection.hasPlayedBefore(target.getUniqueId())) {

                                    Database.seenMessage(sender, target.getUniqueId(), "last");

                                } else {
                                    sender.sendMessage(Utilities.format(Playtime.getInstance().getConfig().getString("seen-time-null")
                                            .replace("%player%", target.getName())));
                                }
                            }catch(SQLException e) {
                                e.printStackTrace();
                            }

                    }

                } else {
                    sender.sendMessage(Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                }
            }

        return false;
    }


    /*public String seenMessage(UUID uuid) {
        try {
            Map<String, Integer> seenTimes = playtime.getDatabaseManager().getSeenTimes(uuid);
            List<String> format = playtime.getConfig().getStringList("seen-format");

            StringBuilder message = new StringBuilder();

            if (!playtime.getProxyManager().isPlayerOnline(Bukkit.getPlayer(uuid))) {
                String serverName = null;
                int seenTime = 0;
                for (String s : format) {
                    for (Map.Entry<String, Integer> entry : seenTimes.entrySet()) {
                        if (entry.getValue() > seenTime) {
                            seenTime = entry.getValue();
                            serverName = entry.getKey();
                        }
                    }
                    if (seenTime == 0) {

                        return Utilities.format(playtime.getConfig().getString("seen-time-null").replace("%server%", serverName));

                    } else {
                        long offlineTime = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000)
                                - TimeUnit.SECONDS.toMinutes(seenTime);
                        s = s.replaceFirst("%server%",
                                serverName.substring(0, 1).toUpperCase() + serverName.substring(1));
                        s = s.replaceFirst("%time%", playtime.getConfig().getString("seen-offline-format") + " "
                                + Utilities.convertTime((int) offlineTime));
                    }
                    message.append(Utilities.format(s));
                    message.append("\n");

                }

                return message.toString();

            } else {
                String serverName = ProxyManager.getPlayerServerName(Bukkit.getPlayer(uuid));

                ResultSet time = playtime.getDatabaseManager().getSeenTime(uuid, serverName);
                if (time.next()) {
                    int seenTime = time.getInt("seen");


                    long onlineTime = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000)
                            - TimeUnit.SECONDS.toMinutes(seenTime);
                    for (String s : format) {
                        s = s.replaceFirst("%server%",
                                serverName.substring(0, 1).toUpperCase() + serverName.substring(1));
                        s = s.replaceFirst("%time%", playtime.getConfig().getString("seen-online-format") + " "
                                + Utilities.convertTime((int) onlineTime));

                        message.append(Utilities.format(s));
                        message.append("\n");

                    }

                    return message.toString();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }*/
}
