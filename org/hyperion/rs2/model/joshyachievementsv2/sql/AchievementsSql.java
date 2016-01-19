package org.hyperion.rs2.model.joshyachievementsv2.sql;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTaskProgress;
import org.hyperion.rs2.sql.MySQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Administrator on 13.10.2015.
 */
public final class AchievementsSql {

    public static MySQLConnection sql;

    private AchievementsSql(){}

    public static boolean insertTaskProgress(final Player player, final AchievementTaskProgress atp){
        try(final PreparedStatement stmt = sql.prepare("INSERT INTO achievement_data (playerName, achievementId, taskId, progress, startTime, finishTime) VALUES (?, ?, ?, ?, ?, ?)")){
            stmt.setString(1, player.getName().toLowerCase());
            stmt.setShort(2, (short)atp.achievementId);
            stmt.setByte(3, (byte)atp.taskId);
            stmt.setInt(4, atp.progress);
            stmt.setString(5, atp.startDate != null ? atp.startDate.toString() : null);
            stmt.setString(6, atp.finishDate != null ? atp.finishDate.toString() : null);
            return stmt.executeUpdate() == 1;
        } catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean updateTaskProgress(final Player player, final AchievementTaskProgress atp){
        try(final PreparedStatement stmt = sql.prepare("UPDATE achievement_data SET progress = ?, startTime = ?, finishTime = ? WHERE playerName = ? AND achievementId = ? AND taskId = ?")){
            stmt.setInt(1, atp.progress);
            stmt.setString(2, atp.startDate != null ? atp.startDate.toString() : null);
            stmt.setString(3, atp.finishDate != null ? atp.finishDate.toString() : null);
            stmt.setString(4, player.getName().toLowerCase());
            stmt.setShort(5, (short) atp.achievementId);
            stmt.setByte(6, (byte) atp.taskId);
            return stmt.executeUpdate() == 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public static List<AchievementTaskProgress> loadTaskProgress(final Player player){
        final List<AchievementTaskProgress> list = new ArrayList<>();
        try(final PreparedStatement stmt = sql.prepare("SELECT * FROM achievement_data WHERE playerName = ?")){
            stmt.setString(1, player.getName().toLowerCase());
            try(final ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    final int achievementId = rs.getShort("achievementId");
                    final int taskId = rs.getByte("taskId");
                    final int progress = rs.getInt("progress");
                    final Timestamp startDate = Optional.ofNullable(rs.getTimestamp("startTime"))
                            .orElse(null);
                    final Timestamp finishDate = Optional.ofNullable(rs.getTimestamp("finishTime"))
                            .orElse(null);
                    list.add(new AchievementTaskProgress(achievementId, taskId, progress, startDate, finishDate));
                }
            }
            return list;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
