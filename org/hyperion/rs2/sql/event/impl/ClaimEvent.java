package org.hyperion.rs2.sql.event.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.sql.requests.DonationRequest;
import org.hyperion.rs2.sql.requests.VoteRequest;
import org.hyperion.util.Time;

public class ClaimEvent extends SQLEvent {

    public static final long DELAY = Time.ONE_MINUTE;

    public ClaimEvent() {
        super(DELAY);
    }

    @Override
    public void execute(SQLConnection sql) throws SQLException {
        ResultSet rs = sql.query("SELECT * FROM donator WHERE `currentTime` >= DATE_SUB(NOW(), INTERVAL 14 DAY) and finished=0 and amount>0");
        if(rs != null) {
            while(rs.next()) {
                String name = rs.getString("name");
                Player player = World.getWorld().getPlayer(name);
                if(player != null) {
                    World.getWorld().getDonationsConnection().offer(new DonationRequest(player));
                }
            }
            rs.close();
        }

        rs = sql.query("SELECT * FROM waitingVotes WHERE voted=0");
        if(rs != null) {
            while(rs.next()) {
                String name = rs.getString("realUsername");
                Player player = World.getWorld().getPlayer(name);
                if(player != null) {
                    World.getWorld().getDonationsConnection().offer(new VoteRequest(player));
                }
            }
            rs.close();
        }

        super.updateStartTime();
        System.out.println("Requests have been done.");
    }

}