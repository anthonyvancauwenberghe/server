package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.util.Time;

public class AccountValuesRequest extends SQLRequest{

    public AccountValuesRequest(final Player player){
        super(QUERY_REQUEST);
        setPlayer(player);
    }

    public void process(final SQLConnection sql){
        if(System.currentTimeMillis() - player.lastAccountValueTime < Time.ONE_MINUTE)
            return;
        final String query = String.format(
                "INSERT INTO accountValues (name, accountValue) VALUES ('%s', %d)",
                player.getName().toLowerCase(), player.getAccountValue().getTotalValue()
        );
        try{
            sql.query(query);
            player.lastAccountValueTime = System.currentTimeMillis();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
