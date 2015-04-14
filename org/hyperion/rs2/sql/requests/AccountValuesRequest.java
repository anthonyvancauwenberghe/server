package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.util.Time;

import java.util.Arrays;

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
        try{
            sql.query(query);
            player.lastAccountValueTime = System.currentTimeMillis();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
