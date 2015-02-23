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


    @Override
    public void handle(Player player, Packet pkt) {
        final int id = pkt.getByte();

        switch(id) {
            case START:
                final String[] playerStrings = new String[pkt.getByte()];
                for(int i = 0; i < playerStrings.length; i++) {
                    playerStrings[i] = pkt.getRS2String();
                }
                final List<Player> players = new ArrayList<Player>();

                for(final String s : playerStrings) {
                    final Player p = World.getWorld().getPlayer(s);
                    if(p == null || !ItemSpawning.canSpawn(player)) {
                        player.sendMessage("%s cannot join party, removed from group", s);
                    } else {
                        if(p.getSkills().getLevel(Skills.DUNGEONINEERING) < player.getDungoneering().getChosen().min_level)
                            player.sendMessage("%s does not meet difficulty level requirements, removed from group", s);
                        players.add(p);
                    }


                }

                for(final Player p : players) {
                    p.getActionSender().sendDialogue("Join "+player.getName()+ "?", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT,
                            "Yes, I want to start a "+player.getDungoneering().getChosen().toString() + " dungeon", "No");
                }

                World.getWorld().submit(new Event(1000) {
                    int count = 0;
                    @Override
                    public void execute() throws IOException {
                        final List<Player> newList = players.stream().filter(p -> p.getExtraData().getBoolean("checkdungeon")).collect(Collectors.toList());
                        if(newList.size() == players.size()) {
                            players.add(player);
                            player.getDungoneering().start(players);
                            this.stop();
                        }
                        if(count++ < 8) {
                            newList.stream().forEach(p -> p.sendMessage("Starting in: " + (8 - count) + " seconds"));
                        } else {
                            players.add(player);
                            player.getDungoneering().start(players);
                            this.stop();
                        }
                    }
                });
                break;
        }

    }


}
