package org.hyperion.sql.impl.log;

import org.hyperion.Server;
import org.hyperion.engine.EngineTask;
import org.hyperion.sql.DbHub;
import org.hyperion.sql.dao.SqlDaoManager;
import org.hyperion.sql.db.Db;
import org.hyperion.sql.impl.log.type.IPLog;

import java.util.concurrent.TimeUnit;

/**
 * Created by Gilles on 29/02/2016.
 */
public class Logs extends SqlDaoManager<LogDao> {
    public Logs(Db db) {
        super(db, LogDao.class);
    }

    public void insertIpLog(IPLog ipLog) {
        Server.getLoader().getEngine().submitSql(new EngineTask<Boolean>("Insert IP", 5, TimeUnit.SECONDS) {
            @Override
            public Boolean call() throws Exception {
                try(final LogDao dao = open()) {
                    dao.addIp(ipLog.getTimestamp().toString(), ipLog.getPlayerName(), ipLog.getIp());
                } catch(Exception ex) {
                    if (DbHub.isConsoleDebug())
                        ex.printStackTrace();
                }
                return true;
            }
        });
    }
}
