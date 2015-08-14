package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.Packet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
                int sizeIndex = pkt.getByte();
                if(sizeIndex < 0)
                    return;
                final DungeonDifficulty.DungeonSize size = DungeonDifficulty.DungeonSize.values()[sizeIndex];
                final String[] playerStrings = new String[pkt.getByte()];
                for(int i = 0; i < playerStrings.length; i++) {
                    playerStrings[i] = pkt.getRS2String();
                }
                final List<Player> players = new CopyOnWriteArrayList<>();

                for(final String s : playerStrings) {
                    final Player p = World.getWorld().getPlayer(s);
                    if(p == null || !p.getLocation().inDungeonLobby()) {
                        player.sendMessage("%s cannot join party, removed from group", s);
                    } else {
                        if(p.getSkills().getLevel(Skills.DUNGEONEERING) < difficulty.min_level)
                            player.sendMessage("%s does not meet difficulty level requirements, removed from group", s);
                        if(!players.contains(p))
                            players.add(p);
                    }


                }

                World.getWorld().submit(new Event(1000) {
                    @Override
                    public void execute() throws IOException {
                        players.add(player);
                        player.getDungoneering().start(players, difficulty, size);
                        this.stop();
                    }
                });
                hide(player);
                break;
            case INVITE:
                ClanManager.joinClanChat(player, "Party "+player.getName(), false);
                final String name = pkt.getRS2String();
                final Player p = World.getWorld().getPlayer(name);
                if(name.equalsIgnoreCase(player.getName())) {
                    player.sendMessage("You cannot invite yourself!");
                    return;
                }
                if(p == null || p.getSkills().getLevel(Skills.DUNGEONEERING) < difficulty.min_level || !p.getLocation().inDungeonLobby()) {
                    player.write(createDataBuilder().put((byte) 1).putRS2String(name).toPacket());
                    break;
                }


                p.getActionSender().sendDialogue("Join "+player.getName()+"?", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT,
                        "Yes, I want to join this dungeon", "No");
                p.getExtraData().put("dungoffer", player);
                p.getInterfaceState().setNextDialogueId(0, 7000);
                p.getInterfaceState().setNextDialogueId(1, 7001);
                break;
        }

    }

    public void respond(final Player player, int response) {
        final Player holder = (Player)player.getExtraData().get("dungoffer");
        holder.write(createDataBuilder().put((byte) response).putRS2String(player.getName()).toPacket());
        if(response == 0)
            ClanManager.joinClanChat(player, "Party "+holder.getName(), false);
        player.getExtraData().put("dungoffer", null);
    }



}
