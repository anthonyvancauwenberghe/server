package org.hyperion.rs2.model.joshyachievementsv2.sql;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

public class SaveAchievementsRequest extends SQLRequest{

    public SaveAchievementsRequest(final Player player){
        super(QUERY_REQUEST);
        setPlayer(player);
    }

    public void process(final SQLConnection sql) throws SQLException{
        if(!sql.isConnected()){
            player.sendf("Your achievement data cannot be saved at this time");
            //perhaps fallback to file? meh idk
            return;
        }
        player.getAchievementTracker().streamAvailableTaskProgress()
                .forEach(p -> {
                    final String query = String.format(
                            "INSERT INTO achievement_data (playerName, achievementId, taskId, progress, startTime, finishTime) " +
                                    "VALUES ('%s', %d, %d, %d, %d, %d)",
                            player.getName(),
                            p.achievementId, p.taskId,
                            p.progress,
                            Optional.ofNullable(p.startDate).map(Date::getTime).orElse(-1L),
                            Optional.ofNullable(p.finishDate).map(Date::getTime).orElse(-1L)
                    );
                    try{
                        sql.query(query);
                    }catch(Exception ex){
                        ex.printStackTrace();
                        player.sendf("Unable to achievement data: achievement %d task %d", p.achievementId, p.taskId);
                    }
                });
    }
}
