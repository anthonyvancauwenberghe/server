package org.hyperion.sql.impl.log;

import org.hyperion.rs2.model.Player;
import org.hyperion.sql.impl.log.type.IPLog;

import java.sql.Timestamp;

/**
 * Created by Gilles on 29/02/2016.
 */
public class Log {
    protected enum LogType {
        TRADE,
        STAKE,
        STAKE_RESULT,
        COMMAND,
        MESSAGE,
        PRIVATE_MESSAGE,
        CLAN,
        SHOP,
        DEATH_BY_PLAYER,
        DEATH_BY_NPC,
        PLAYER_KILL,
        PICKUP_ITEM,
        GAMBLE,
        IP;

        public int getFlag() {
            return 1 << (ordinal() + 1);
        }
    }

    private final Timestamp timestamp;
    private final LogType type;

    protected Log(final Timestamp timestamp, final LogType type) {
        this.timestamp = timestamp;
        this.type = type;
    }

    protected Timestamp getTimestamp() {
        return timestamp;
    }

    protected LogType getType() {
        return type;
    }

    protected static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static IPLog ipLog(Player player) {
        return new IPLog(player.getName(), player.getShortIP());
    }
}
