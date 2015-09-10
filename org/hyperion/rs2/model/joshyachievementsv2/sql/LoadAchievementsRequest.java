package org.hyperion.rs2.model.joshyachievementsv2.sql;

import java.io.File;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementProgress;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTaskProgress;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

public class LoadAchievementsRequest extends SQLRequest{

    public LoadAchievementsRequest(final Player player){
        super(QUERY_REQUEST);
        setPlayer(player);
    }

    public void process(final SQLConnection sql) throws SQLException{
        if(!sql.isConnected()){
            player.sendf("Your achievement data cannot be loaded at this time");
            return;
        }
        final String query = String.format("SELECT * FROM achievement_data WHERE playerName = '%s'", player.getName());
        try(final ResultSet rs = sql.query(query)){
            while(rs.next()){
                final int achievementId = rs.getInt("achievementId");
                final int taskId = rs.getInt("taskId");
                final int progress = rs.getInt("progress");
                final Date startDate = Optional.of(rs.getLong("startTime"))
                        .filter(t -> t != -1)
                        .map(Date::new)
                        .orElse(null);
                final Date finishDate = Optional.of(rs.getLong("finishTime"))
                        .filter(t -> t != -1)
                        .map(Date::new)
                        .orElse(null);
                final AchievementProgress p = player.getAchievementTracker().progress(achievementId);
                p.add(new AchievementTaskProgress(achievementId, taskId, progress, startDate, finishDate));
            }
            player.sendf("Loaded achievement data");
        }catch(Exception ex){
            ex.printStackTrace();
            player.sendf("Error loading your achievement data");
        }
    }
}
