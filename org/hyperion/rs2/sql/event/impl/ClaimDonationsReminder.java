package org.hyperion.rs2.sql.event.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

public class ClaimDonationsReminder extends SQLEvent {

    public static final long DELAY = Time.ONE_MINUTE;

    public ClaimDonationsReminder() {
        super(DELAY);
    }

    @Override
    public void execute(SQLConnection sql) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT name FROM donations WHERE name IN(");
        for(Player player: World.getWorld().getPlayers()) {
            if(player != null) {
                sb.append("'" + player.getSafeDisplayName() + "',");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        ResultSet rs = sql.query(sb.toString());
        if(rs != null) {
            while(rs.next()) {
                String name = rs.getString("name");
                Player player = World.getWorld().getPlayer(name);
                if(player != null) {
                    player.getActionSender().sendMessage("@blu@Your donation has been processed.");
                    player.getActionSender().sendMessage("@blu@Please claim your donation with the ::claimpoints command.");
                }
            }
            rs.close();
        }
        super.updateStartTime();
    }

}