package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/22/15
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DungoneeringParty extends Interface {


    public static final int ID = 13;

    public DungoneeringParty() {
        super(ID);
    }


    private static final int START = 0;
    private static final int INVITE = 1;


    @Override
    public void handle(Player player, Packet pkt) {
        final int id = pkt.getByte();
        final DungeonDifficulty difficulty = DungeonDifficulty.values()[pkt.getByte()];

        switch(id) {
            case START:
                final String[] playerStrings = new String[pkt.getByte()];
                for(int i = 0; i < playerStrings.length; i++) {
                    playerStrings[i] = pkt.getRS2String();
                }
                final List<Player> players = new ArrayList<>();

                for(final String s : playerStrings) {
                    final Player p = World.getWorld().getPlayer(s);
                    if(p == null || !p.getLocation().inDungeonLobby()) {
                        player.sendMessage("%s cannot join party, removed from group", s);
                    } else {
                        if(p.getSkills().getLevel(Skills.DUNGEONINEERING) < difficulty.min_level)
                            player.sendMessage("%s does not meet difficulty level requirements, removed from group", s);
                        players.add(p);
                    }


                }

                World.getWorld().submit(new Event(1000) {
                    @Override
                    public void execute() throws IOException {
                        players.add(player);
                        player.getDungoneering().start(players, difficulty);
                        this.stop();
                    }
                });
                break;
            case INVITE:
                final String name = pkt.getRS2String();
                final Player p = World.getWorld().getPlayer(name);
                System.out.println("HERE");
                if(p == null || p.getSkills().getLevel(Skills.DUNGEONINEERING) < difficulty.min_level || !p.getLocation().inDungeonLobby()) {
                    player.write(createDataBuilder().put((byte) 1).putRS2String(name).toPacket());
                    break;
                }



                p.getActionSender().sendDialogue("Join "+player.getName()+"?", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT,
                        "Yes, I want to join this dungeon", "No");
                player.getExtraData().put("dungoffer", player);
                player.getInterfaceState().setNextDialogueId(0, 7000);
                player.getInterfaceState().setNextDialogueId(1, 7001);
                break;
        }

    }

    public void respond(final Player player, int response) {
        final Player holder = (Player)player.getExtraData().get("dungoffer");
        holder.write(createDataBuilder().put((byte) response).putRS2String(player.getName()).toPacket());
    }



}
