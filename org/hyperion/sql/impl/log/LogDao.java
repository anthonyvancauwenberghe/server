package org.hyperion.sql.impl.log;

import org.hyperion.sql.dao.SqlDao;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by Gilles on 29/02/2016.
 */
public interface LogDao extends SqlDao {

    @SqlUpdate("INSERT INTO player_ips (last_visit, playername, ip) VALUES (:last_visit, :playername, :ip) ON DUPLICATE KEY UPDATE last_visit = :last_visit")
    void addIp(@Bind("last_visit") final String timestamp, @Bind("playername") final String playerName, @Bind("ip") final String ip);
}
