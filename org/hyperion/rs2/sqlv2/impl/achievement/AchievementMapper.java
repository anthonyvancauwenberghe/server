package org.hyperion.rs2.sqlv2.impl.achievement;

import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTaskProgress;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Gilles on 3/02/2016.
 */
public class AchievementMapper implements ResultSetMapper<AchievementTaskProgress> {
    @Override
    public AchievementTaskProgress map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new AchievementTaskProgress(
                resultSet.getShort("achievementId"),
                resultSet.getByte("taskId"),
                resultSet.getInt("progress"),
                Optional.ofNullable(resultSet.getTimestamp("startTime")).orElse(null),
                Optional.ofNullable(resultSet.getTimestamp("finishTime")).orElse(null)
        );
    }
}
