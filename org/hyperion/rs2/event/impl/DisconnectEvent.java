package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class DisconnectEvent extends Event {

    public static final long DELAY = 1000;

    static {
        CommandHandler.submit(new Command("forcelogout", Rank.DEVELOPER) {
            @Override
            public boolean execute(final Player player, final String input) {
                final String name = input.replaceAll("forcelogout ", "");
                final Player glitcher = World.getWorld().getPlayer(name);
                player.getActionSender().sendMessage(forceLogout(glitcher));
                return true;
            }
        });
    }

    public DisconnectEvent() {
        super(DELAY);
    }

    private static String forceLogout(final Player glitcher) {
        if(glitcher == null){
            return "That player is offline";
        }
        try{
            World.getWorld().unregister(glitcher);
        }catch(final Exception e){
            e.printStackTrace();
        }
        System.out.println("Player forced logout: " + glitcher.getName());
        return "Forced logout succesful.";
    }

    @Override
    public void execute() {
        final long currentTime = System.currentTimeMillis();
        for(final Player player : World.getWorld().getPlayers()){
            if(player != null){
                if(player.isDisconnected() && currentTime - player.cE.lastHit >= 10000){
                    forceLogout(player);
                    break;
                }
            }
        }
    }

}
