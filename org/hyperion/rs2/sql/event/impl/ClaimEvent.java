package org.hyperion.rs2.sql.event.impl;

import org.hyperion.Server;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.sql.requests.DonationRequest;
import org.hyperion.rs2.sql.requests.VoteRequest;
import org.hyperion.util.Time;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClaimEvent extends SQLEvent {

    public static final long DELAY = Time.ONE_MINUTE;
    //public static final long DELAY = Time.ONE_SECOND * 10;

    public ClaimEvent() {
        super(DELAY);
    }

    @Override
    public void execute(final SQLConnection sql) throws SQLException {
        final List<Player> donated = new ArrayList<Player>();
        final List<Player> voted = new ArrayList<Player>();

        ResultSet rs = sql.query("SELECT * FROM donator WHERE `currentTime` >= DATE_SUB(NOW(), INTERVAL 14 DAY) AND finished=0 AND amount>0");
        if(rs != null){
            while(rs.next()){
                final String name = rs.getString("name");
                final Player player = World.getWorld().getPlayer(name);
                if(player != null){
                    if(!donated.contains(player)){
                        if(Server.getConfig().getBoolean("donationssql"))
                            World.getWorld().getDonationsConnection().offer(new DonationRequest(player));
                        donated.add(player);
                    }
                }
            }
            rs.close();
        }

        rs = sql.query("SELECT * FROM waitingVotes WHERE `timestamp` >= DATE_SUB(NOW(), INTERVAL 14 DAY) AND processed=0");
        if(rs != null){
            while(rs.next()){
                final String name = rs.getString("realUsername");
                final Player player = World.getWorld().getPlayer(name);
                if(player != null){
                    if(!voted.contains(player)){
                        if(Server.getConfig().getBoolean("donationssql"))
                            World.getWorld().getDonationsConnection().offer(new VoteRequest(player));
                        voted.add(player);
                    }
                }
            }
            rs.close();
        }
        super.updateStartTime();
    }

}