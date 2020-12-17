package com.playtime.events;

import com.playtime.Playtime;

public class OldTimes {

    private Playtime playtime;

    public OldTimes(Playtime playtime) {
        this.playtime = playtime;
    }

    /*public void checkPlayerTimes(Player player) {

        try {
            if (playtime.getDatabaseManager().inOldDataBase(player.getUniqueId(), "Valley")
                    && playtime.getDatabaseManager().inOldDataBase(player.getUniqueId(), "Summit")) {

                ResultSet valley = playtime.getDatabaseManager().getOldPlaytimeServer(player.getUniqueId(), "Valley");
                ResultSet summit = playtime.getDatabaseManager().getOldPlaytimeServer(player.getUniqueId(), "Summit");
                if (valley.next() && summit.next()) {
                    int valleyTime = valley.getInt("TIME");
                    int summitTime = summit.getInt("TIME");

                    if (valleyTime >= summitTime) {
                        int newSummitTime = valleyTime - summitTime;

                        if (newSummitTime >= 0 && newSummitTime < summitTime) {
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), newSummitTime, "Summit");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Valley");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Summit");
                        } else {
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), 0, "Summit");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Valley");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Summit");
                        }
                    } else {
                        int newSummitTime = summitTime - valleyTime;

                        if (newSummitTime >= 0 && newSummitTime < summitTime) {
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), newSummitTime, "Summit");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Valley");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Summit");
                        } else {
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                            playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), 0, "Summit");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Valley");
                            playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Summit");
                        }
                    }

                }

            }
            if (playtime.getDatabaseManager().inOldDataBase(player.getUniqueId(), "Valley")
                    && !playtime.getDatabaseManager().inOldDataBase(player.getUniqueId(), "Summit")) {
                ResultSet valley = playtime.getDatabaseManager().getOldPlaytimeServer(player.getUniqueId(), "Valley");
                if (valley.next()) {
                    int valleyTime = valley.getInt("TIME");

                    playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), valleyTime, "Valley");
                    playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), 0, "Summit");
                    playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Valley");
                }
            }
            if (!playtime.getDatabaseManager().inOldDataBase(player.getUniqueId(), "Valley")
                    && playtime.getDatabaseManager().inOldDataBase(player.getUniqueId(), "Summit")) {
                ResultSet summit = playtime.getDatabaseManager().getOldPlaytimeServer(player.getUniqueId(), "Summit");
                if (summit.next()) {
                    int summitTime = summit.getInt("TIME");

                    playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), 0, "Valley");
                    playtime.getDatabaseManager().setPlaytime(player.getUniqueId(), summitTime, "Summit");
                    playtime.getDatabaseManager().deleteOldPlaytime(player.getUniqueId(), "Summit");
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }*/
}
