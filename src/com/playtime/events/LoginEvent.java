package com.playtime.events;

import com.playtime.Playtime;
import com.playtime.database.PlaytimeConnection;
import com.playtime.database.SeenConnection;
import com.playtime.maps.Maps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class LoginEvent implements Listener {

    private Playtime playtime;

    public LoginEvent(Playtime playtime) {
        this.playtime = playtime;

    }

    @EventHandler
    public void playerLogin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (Maps.otherTableNames.contains("old_playtime")) {
            checkPlayerTimes(player);
        }

        if (playtime.getConfig().getBoolean("tracktime") == true) {

            PlaytimeConnection.updatePlayerPlaytime(player.getUniqueId(), Playtime.getServerName(), true);


            long i = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
            Maps.onlineTime.put(player.getUniqueId(), i);
        } else {

            PlaytimeConnection.updatePlayerPlaytime(player.getUniqueId(), Playtime.getServerName(), false);

        }

        SeenConnection.updateSeenTime(player.getUniqueId(), Playtime.getServerName(), true);


    }

    public void checkPlayerTimes(Player player) {

        try {
            if (PlaytimeConnection.inOldDataBase(player.getUniqueId(), "Valley")
                    && PlaytimeConnection.inOldDataBase(player.getUniqueId(), "Summit")) {

                ResultSet valley = PlaytimeConnection.getOldPlaytimeServer(player.getUniqueId(), "Valley");
                ResultSet summit = PlaytimeConnection.getOldPlaytimeServer(player.getUniqueId(), "Summit");
                if (valley.next() && summit.next()) {
                    int valleyTime = valley.getInt("TIME");
                    int summitTime = summit.getInt("TIME");

                    if (valleyTime >= summitTime) {
                        int newSummitTime = valleyTime - summitTime;

                        if (newSummitTime >= 0 && newSummitTime < summitTime) {
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), newSummitTime, "Summit");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Valley");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Summit");
                        } else {
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), 0, "Summit");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Valley");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Summit");
                        }
                    } else {
                        int newSummitTime = summitTime - valleyTime;

                        if (newSummitTime >= 0 && newSummitTime < summitTime) {
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), newSummitTime, "Summit");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Valley");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Summit");
                        } else {
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            PlaytimeConnection.setPlaytime(player.getUniqueId(), 0, "Summit");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Valley");
                            PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Summit");
                        }
                    }

                }

            }
            if (PlaytimeConnection.inOldDataBase(player.getUniqueId(), "Valley")
                    && !PlaytimeConnection.inOldDataBase(player.getUniqueId(), "Summit")) {
                ResultSet valley = PlaytimeConnection.getOldPlaytimeServer(player.getUniqueId(), "Valley");
                if (valley.next()) {
                    int valleyTime = valley.getInt("TIME");

                    PlaytimeConnection.setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                    PlaytimeConnection.setPlaytime(player.getUniqueId(), 0, "Summit");
                    PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Valley");
                }
            }
            if (!PlaytimeConnection.inOldDataBase(player.getUniqueId(), "Valley")
                    && PlaytimeConnection.inOldDataBase(player.getUniqueId(), "Summit")) {
                ResultSet summit = PlaytimeConnection.getOldPlaytimeServer(player.getUniqueId(), "Summit");
                if (summit.next()) {
                    int summitTime = summit.getInt("TIME");

                    PlaytimeConnection.setPlaytime(player.getUniqueId(), 0, "Valley");
                    PlaytimeConnection.setPlaytime(player.getUniqueId(), summitTime, "Summit");
                    PlaytimeConnection.deleteOldPlaytime(player.getUniqueId(), "Summit");
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
