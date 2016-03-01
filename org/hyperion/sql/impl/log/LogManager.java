package org.hyperion.sql.impl.log;

import org.hyperion.sql.DbHub;
import org.hyperion.sql.impl.log.type.IPLog;

/**
 * Created by Gilles on 29/02/2016.
 */
public final class LogManager {

    private LogManager() {}

    public static boolean insertLog(Log log) {
        if(!DbHub.initialized() || !DbHub.getPlayerDb().isInitialized())
            return false;
        switch(log.getType()) {
            case IP:
                DbHub.getPlayerDb().getLogs().insertIpLog((IPLog)log);
                return true;
            default:
                return false;
        }
    }
}
