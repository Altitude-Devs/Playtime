package com.playtime.events;

import com.playtime.Playtime;
import com.playtime.database.PlaytimeConnection;
import com.playtime.database.SeenConnection;
import com.playtime.maps.Maps;
import com.playtime.task.AutoSave;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogoutEvent implements Listener {

    private Playtime playtime;

    public LogoutEvent(Playtime playtime) {
        this.playtime = playtime;

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId());

        if (playtime.getConfig().getBoolean("tracktime") == true) {

            PlaytimeConnection.updatePlayerPlaytime(player.getUniqueId(), Playtime.getServerName(), true);

            if (Maps.onlineTime.containsKey(player.getUniqueId())) {
                AutoSave.playerLogout(player.getUniqueId());
                Maps.onlineTime.remove(player.getUniqueId());
            }
        } else {

            PlaytimeConnection.updatePlayerPlaytime(e.getPlayer().getUniqueId(), Playtime.getServerName(), true);

        }

        SeenConnection.updateSeenTime(player.getUniqueId(), Playtime.getServerName(), false);

    }
}
