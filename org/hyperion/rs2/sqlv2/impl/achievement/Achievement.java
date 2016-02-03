package org.hyperion.rs2.sqlv2.impl.achievement;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTaskProgress;
import org.hyperion.rs2.sqlv2.dao.SqlDaoManager;
import org.hyperion.rs2.sqlv2.db.Db;
import org.hyperion.rs2.sqlv2.db.DbConfig;

import java.util.List;

/**
 * Created by Gilles on 3/02/2016.
 */
public class Achievement extends SqlDaoManager<AchievementDao> {

    public Achievement(final Db db) {
        super(db, AchievementDao.class);
    }

    public List<AchievementTaskProgress> loadTaskProgress(final Player player) {
        try(final AchievementDao dao = open()) {
            return dao.getProgress(player.getName().toLowerCase().replace("_", " "));
        } catch(Exception ex) {
            if (DbConfig.consoleDebug)
                ex.printStackTrace();
            return null;
        }
    }

    public boolean updateTaskProgress(final Player player, final AchievementTaskProgress atp) {
        try(final AchievementDao dao = open()){
            return dao.updateProgress(player.getName().toLowerCase().replace("_", " "), atp.achievementId, atp.taskId, atp.progress, atp.startDate, atp.finishDate) == 1;
        } catch(Exception ex) {
            if (DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean insertTaskProgress(final Player player, final AchievementTaskProgress atp){
        try(final AchievementDao dao = open()){
            return dao.insertProgress(player.getName().toLowerCase().replace("_", " "), atp.achievementId, atp.taskId, atp.progress, atp.startDate, atp.finishDate) == 1;
        } catch(Exception ex) {
            if (DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }
}
