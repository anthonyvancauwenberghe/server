package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.sql.requests.RichWhitelistRequest;
import org.hyperion.util.Time;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles on 25/10/2015.
 */
public class RichWhitelistEvent extends SQLEvent {

    public static boolean enabled = true;
    public static List<String> whitelist = new ArrayList();

    public RichWhitelistEvent(){
        super(Time.ONE_MINUTE);
    }

    @Override
    public void execute(SQLConnection sql) throws SQLException {
        if(!enabled)
            this.stop();
        sql.offer(new RichWhitelistRequest());
    }

    static {
        CommandHandler.submit(new Command("disablerichevent") {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                enabled = false;
                player.sendMessage("Rich check event is now disabled.");
                return true;
            }
        });
        CommandHandler.submit(new Command("enablerichevent") {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                enabled = true;
                player.sendMessage("Rich check event is now enabled.");
                return true;
            }
        });
    }
}
