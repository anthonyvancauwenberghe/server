package org.hyperion.rs2.saving.impl;

import org.hyperion.Server;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.saving.SaveInteger;
import org.hyperion.rs2.sql.requests.QueryRequest;

public class SaveVotePoints extends SaveInteger {

    public SaveVotePoints(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getDefaultValue() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setValue(final Player player, int value) {
        if(value > 6000){
            final String query = "INSERT INTO glitchers(name,message) VALUES ('" + player.getName().toLowerCase() + "','votepoints glitch:" +
                    value + "')";
            if(Server.getConfig().getBoolean("logssql"))
                World.getWorld().getLogsConnection().offer(new QueryRequest(query));
            value = 0;
        }
        player.getPoints().setVotingPoints(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getPoints().getVotingPoints();
    }

}
