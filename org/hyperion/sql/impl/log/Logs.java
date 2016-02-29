package org.hyperion.sql.impl.log;

import org.hyperion.sql.dao.SqlDaoManager;
import org.hyperion.sql.db.Db;

/**
 * Created by Gilles on 29/02/2016.
 */
public class Logs extends SqlDaoManager<LogDao> {
    public Logs(Db db) {
        super(db, LogDao.class);
    }
}
