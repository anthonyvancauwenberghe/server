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

public class StaffActivityRequest extends SQLRequest{

    public StaffActivityRequest(final Player player){
        super(QUERY_REQUEST);
        setPlayer(player);
    }

    public void process(final SQLConnection sql){
        final String name = player.getName().toLowerCase();
        final long login = player.loginTime/1000;
        final long logout = System.currentTimeMillis()/1000;
        final long duration = logout-login;
        final String query = String.format(
                "INSERT INTO staffactivity (name, login, logout,duration) VALUES ('%s', %d, %d, %d)",
                name,
                login,
                logout,
                duration
        );
        try{
            sql.query(query);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}