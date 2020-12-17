package com.playtime.commands;

import com.playtime.Playtime;
import com.playtime.database.Database;
import com.playtime.database.PlanConnection;
import com.playtime.database.PlaytimeConnection;
import com.playtime.maps.Maps;
import com.playtime.util.Utilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlaytimeCMD implements CommandExecutor {

    private Playtime playtime;

    public PlaytimeCMD(Playtime playtime) {
        this.playtime = playtime;
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        try {
            if (cmd.getName().equalsIgnoreCase("playtime") || cmd.getName().equalsIgnoreCase("pt")) {

                if (args.length == 0) {
                    if (sender instanceof Player) {
                        final Player player = (Player) sender;
                        if (sender.hasPermission("playtime.view")) {
                            Database.getPlaytimeMessage(sender, player.getUniqueId(), false);
                        } else {
                            sender.sendMessage(
                                    Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid Usage. /playtime <player>");
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("top")) {
                        sender.sendMessage(ChatColor.YELLOW + "Playtime > " + ChatColor.RED
                                + "We are currently still working on this plugin and this part is still under development. It will be added soon.");
                        return true;
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        if (sender.hasPermission("playtime.reload")) {
                            playtime.reloadConfig();
                            sender.sendMessage(ChatColor.GREEN + "Playtime plugin reload complete.");
                            return true;
                        } else {
                            sender.sendMessage(
                                    Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                            return true;
                        }

                    } else if (args[0].equalsIgnoreCase("set")) {
                        if (sender.hasPermission("playtime.set")) {
                            sender.sendMessage(
                                    Utilities.format(playtime.getConfig().getString("messages.invalid-set-command")));
                            return true;
                        } else {
                            sender.sendMessage(
                                    Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("extra")){
                        if (sender.hasPermission("playtime.extra")) {
                            Player player = (Player) sender;
                            Database.getDetailedPlaytimeMessage(sender, player, false);
                        } else {
                            sender.sendMessage(Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                        }
                        return true;
                    } else if (sender.hasPermission("playtime.view.others")) {

                        OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);

                        if (PlaytimeConnection.hasPlayedBefore(target.getUniqueId())) {
                            Database.getPlaytimeMessage(sender, target.getUniqueId(), true);
                        } else {
                            sender.sendMessage(Utilities.format(playtime.getConfig().getString("messages.player-not-found")
                                    .replace("%player%", target.getName())));
                        }

                    } else {
                        sender.sendMessage(Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                        return true;
                    }

                }
                if (args.length == 2){
                    if (args[0].equalsIgnoreCase("extra")){
                        if (sender.hasPermission("playtime.extra") && sender.hasPermission("playtime.extra.others")) {
                            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[1]);

                            if (PlaytimeConnection.hasPlayedBefore(target.getUniqueId())) {
                                Database.getDetailedPlaytimeMessage(sender, target, true);
                            } else {
                                sender.sendMessage(Utilities.format(playtime.getConfig().getString("messages.player-not-found")
                                        .replace("%player%", args[1])));
                            }
                        } else {
                            sender.sendMessage(Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                        }
                    }
                }
                if (args.length == 2 || args.length == 3) {
                    if (args[0].equalsIgnoreCase("set")) {
                        sender.sendMessage(
                                Utilities.format(playtime.getConfig().getString("messages.invalid-set-command")));
                        return true;
                    }
                }
                if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("set")) {
                        if (sender.hasPermission("playtime.set")) {
                            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[1]);


                            if (PlaytimeConnection.hasPlayedBefore(target.getUniqueId())) {

                                int time;
                                try {
                                    time = Integer.parseInt(args[3]);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(Utilities
                                            .format(playtime.getConfig().getString("messages.invalid-set-command")));
                                    return true;
                                }

                                try {
                                    List<String> tableNames = Maps.serverNames;
                                    if (tableNames.contains(args[2].toLowerCase())) {
                                        ResultSet playerTime = PlaytimeConnection
                                                .getPlaytimeServer(target.getUniqueId(), args[2]);
                                        if (playerTime.next()) {
                                            int oldTime = playerTime.getInt("TIME");
                                            PlaytimeConnection.setPlaytime(target.getUniqueId(), time, args[2]);
                                            sender.sendMessage(Database.changeTimeFormat(target, time, oldTime, args[2]));
                                        } else {
                                            sender.sendMessage(Utilities
                                                    .format(playtime.getConfig().getString("messages.no-playtime-server")
                                                            .replace("%player%", args[1]).replace("%server%", args[2])));
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage(Utilities.format(playtime.getConfig()
                                                .getString("messages.invalid-server").replace("%server%", args[2])));
                                        return true;
                                    }
                                } catch (SQLException e) {
                                    sender.sendMessage("Failed to input into database... Check console for error log.");
                                    e.printStackTrace();
                                    return true;
                                }
                                return true;

                            } else {
                                sender.sendMessage(Utilities.format(playtime.getConfig()
                                        .getString("messages.player-not-found").replace("%player%", args[1])));
                                return true;
                            }
                        } else {
                            sender.sendMessage(
                                    Utilities.format(playtime.getConfig().getString("messages.no-permission")));
                            return true;
                        }
                    }
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
