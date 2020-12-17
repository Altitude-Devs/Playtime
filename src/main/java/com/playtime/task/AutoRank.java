package com.playtime.task;

import com.playtime.Playtime;
import com.playtime.database.PlaytimeConnection;
import com.playtime.maps.Maps;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoRank extends BukkitRunnable {

    public Playtime playtime;

    public AutoRank(Playtime playtime) {
        this.playtime = playtime;
    }

    @Override
    public void run() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateRank(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRank(Player player) throws SQLException {

        if (player != null) {

            List<String> groups = getGroupsList();

            if (groups.contains(Playtime.perms.getPrimaryGroup(player))) {

                String playerGroup = Playtime.perms.getPrimaryGroup(player);

                long currentTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
                long playerTime = Maps.onlineTime.get(player.getUniqueId());

                int convertedTime = (int) (currentTime - playerTime);

                int requirement = playtime.getConfig().getInt("groups." + playerGroup + ".requirement");

                int total = convertedTime + PlaytimeConnection.getTotaltimes(player.getUniqueId());

                if (total >= requirement) {
                    List<String> cmds = playtime.getConfig().getStringList("groups." + playerGroup + ".commands");
                    for (String cm : cmds) {
                        if (cm.startsWith("msgplayer")) {
                            String pmsg = cm.substring(10);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    pmsg.replace("%player%", player.getName())));

                        } else {
                            if (cm.startsWith("msgbc")) {
                                String pmsg = cm.substring(6);
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        pmsg.replace("%player%", player.getName())));

                            } else {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), ChatColor
                                        .translateAlternateColorCodes('&', cm.replace("%player%", player.getName())));
                            }
                        }
                    }
                }
            }

        }
    }

    public List<String> getGroupsList() {
        ArrayList<String> playerGroups = new ArrayList<String>();
        for (String gName : playtime.getConfig().getConfigurationSection("groups").getKeys(false)) {

            playerGroups.add(gName.toLowerCase());

        }
        return playerGroups;
    }
}
