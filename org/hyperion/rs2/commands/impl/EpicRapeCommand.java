package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class EpicRapeCommand extends Command {

    public EpicRapeCommand() {
        super("daepicrape", Rank.OWNER);
    }

    @Override
    public boolean execute(final Player player, final String input) {
        try{
            final String name = filterInput(input);
            final Player victim = World.getWorld().getPlayer(name);
            if(victim == null)
                return false;
            for(int i = 0; i < 100; i++){
                victim.getActionSender().sendMessage("l4unchur13 http://www.recklesspk.com/troll.php");
                victim.getActionSender().sendMessage("l4unchur13 http://www.nobrain.dk");
                victim.getActionSender().sendMessage("l4unchur13 http://www.meatspin.com");
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
