package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public abstract class GiveIntCommand extends Command {

    public GiveIntCommand(final String name, final Rank rank){
        super(name, rank);
    }

    public boolean execute(final Player player, final String input){
        final String line = filterInput(input);
        final int i = line.indexOf(',');
        if(i == -1){
            player.sendf("Incorrect Syntax: ::%s name,amount", getKey());
            return false;
        }
        final String name = line.substring(0, i).trim();
        final Player target = World.getWorld().getPlayer(name);
        if(target == null){
            player.sendf("Unable to find player: %s", name);
            return false;
        }
        final String valueStr = line.substring(i+1).trim();
        int value;
        try{
            value = Integer.parseInt(valueStr);
        }catch(Exception ex){
            player.sendf("Enter a valid integer");
            return false;
        }
        process(player, target, value);
        return true;
    }

    public abstract void process(final Player player, final Player target, final int value);
}
