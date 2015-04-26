package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AccountValuesRequest extends SQLRequest{

    public AccountValuesRequest(final Player player){
        super(QUERY_REQUEST);
        setPlayer(player);
    }

    public void process(final SQLConnection sql){
        if(System.currentTimeMillis() - player.lastAccountValueTime < Time.ONE_MINUTE)
            return;
        final int value = player.getAccountValue().getTotalValue(), mac = player.getUID();
        final long pkpValue = player.getAccountValue().getPkPointValue();
        final String ip = player.getShortIP(), specialUid = Arrays.toString(player.specialUid);
        final String query = String.format(
                "INSERT INTO accountvalues (name, value, pkvalue,ip,mac,suid) VALUES ('%s', %d, %d, '%s', %d, '%s') ON DUPLICATE KEY UPDATE value = %d, pkvalue = %d, ip='%s', mac=%d, suid='%s'",
                player.getName().toLowerCase(),
                value,
                pkpValue,
                ip,
                mac,
                specialUid,
                value,
                pkpValue,
                ip,
                mac,
                specialUid
        );
        if(value > 1_500_000 || value < 0)
            PunishmentManager.getInstance().add(Punishment.create("server", player, new Combination(Target.ACCOUNT, Type.BAN), org.hyperion.rs2.model.punishment.Time.create(1, TimeUnit.DAYS),"too much dp"));
        try{
            sql.query(query);
            player.lastAccountValueTime = System.currentTimeMillis();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
