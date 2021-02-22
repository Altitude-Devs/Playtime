package com.playtime.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PlanConnection {

    public static ResultSet getDetailedPlaytime(String uuid) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        String sql = "SELECT curmonth.curmonth_min, prevmonth.prevmonth_min, curweek.curweek_min, prevweek.prevweek_min " +
                "FROM plan.`plan_users` pu " +
                "LEFT JOIN (" +
                    "SELECT uuid, SUM(session_end - session_start) curmonth_min " +
                    "FROM plan.`plan_sessions` " +
                    "WHERE uuid = '" + uuid + "' ";
        cal.set(Calendar.DAY_OF_MONTH, 1);
        sql +=
                    "AND session_start >= '" + cal.getTimeInMillis() + "' " + // first day of current month
                    "GROUP BY uuid) curmonth ON curmonth.uuid = pu.uuid " +
                "LEFT JOIN (" +
                    "SELECT uuid, SUM(session_end - session_start) prevmonth_min " +
                    "FROM plan.`plan_sessions` " +
                    "WHERE uuid = '" + uuid + "' ";
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long lastDayOfPrevMonths = cal.getTimeInMillis() + TimeUnit.DAYS.toMillis(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        sql +=
                    "AND session_start >= '" + cal.getTimeInMillis() + "' " + // first day of prev month
                    "AND session_end <= '" + lastDayOfPrevMonths + "' " + // last day of prev month
                    "GROUP BY uuid)" +
                "prevmonth ON prevmonth.uuid = pu.uuid " +
                "LEFT JOIN (" +
                    "SELECT uuid, SUM(session_end - session_start) curweek_min " +
                    "FROM plan.`plan_sessions` " +
                    "WHERE uuid = '" + uuid + "' ";
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        sql +=
                    "AND session_start >= '" + cal.getTimeInMillis() + "' " + // first day of current week (monday)
                    "GROUP BY uuid) " +
                "curweek ON curweek.uuid = pu.uuid " +
                "LEFT JOIN (" +
                    "SELECT uuid, SUM(session_end - session_start) prevweek_min " +
                    "FROM plan.`plan_sessions` " +
                    "WHERE uuid = '" + uuid + "' " +
                    "AND session_end <= '" + cal.getTimeInMillis() + "' ";
                cal.add(Calendar.HOUR, -(24*7));
        sql +=
                    "AND session_start >= '" + cal.getTimeInMillis() + "' "; // first day of prev week (monday)
        sql +=// last day of prev week (sunday)
                    "GROUP BY uuid) " +
                "prevweek ON prevweek.uuid = pu.uuid " +
                "WHERE pu.uuid = '" + uuid + "'";
        try {
            PreparedStatement statement = PlanDatabaseConnection.getConnection().prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
