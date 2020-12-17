package com.playtime.task;

import com.playtime.Playtime;
import com.playtime.database.PlaytimeConnection;
import com.playtime.maps.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AutoSave extends BukkitRunnable {

    private Playtime playtime;

    public AutoSave(Playtime playtime) {
        this.playtime = playtime;
    }

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Maps.onlineTime.containsKey(player.getUniqueId())) {
                saveTime(player);
            }
        }
    }

    public static void playerLogout(UUID u) {

        OfflinePlayer player = Bukkit.getOfflinePlayer(u);

        long currentTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
        long playerTime = Maps.onlineTime.get(player.getUniqueId());

        int convertedTime = (int) (currentTime - playerTime);

        if (currentTime > playerTime) {
            try {
                ResultSet currentPlaytime = PlaytimeConnection.getPlaytimeServer(player.getUniqueId(), Playtime.getInstance().getConfig().getString("server"));
                if (currentPlaytime.next()) {
                    convertedTime += currentPlaytime.getInt("TIME");
                } else {
                    throw new SQLException(
                            "Unable to find previous time... Not logging to prevent time being overwritten...");
                }

                PlaytimeConnection.setPlaytime(player.getUniqueId(), convertedTime, Playtime.getInstance().getConfig().getString("server"));
                Maps.onlineTime.put(player.getUniqueId(), currentTime);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void saveTime(Player player) {

        long currentTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
        long playerTime = Maps.onlineTime.get(player.getUniqueId());

        int convertedTime = (int) (currentTime - playerTime);

        if (currentTime > playerTime) {

            try {
                ResultSet currentPlaytime = PlaytimeConnection.getPlaytimeServer(player.getUniqueId(), playtime.getConfig().getString("server"));
                if (currentPlaytime.next()) {
                    convertedTime += currentPlaytime.getInt("TIME");
                } else {
                    throw new SQLException(
                            "Unable to find previous time... Not logging to prevent time being overwritten...");
                }

                PlaytimeConnection.setPlaytime(player.getUniqueId(), convertedTime, playtime.getConfig().getString("server"));
                Maps.onlineTime.put(player.getUniqueId(), currentTime);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


}
