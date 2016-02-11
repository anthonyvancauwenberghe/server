package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPacketActivityCommand extends Command {

    private static final int DEFAULT_SIZE = 5;

    public enum Style implements Comparator<Player> {

        PACKET_COUNT("Packet Count", "packetCount"),
        PACKETS_READ("Packets Read", "packetsRead"),
        PACKETS_WRITE("Packets Wrote", "packetsWrite");

        private final String name;
        private final String key;

        Style(final String name, final String key){
            this.name = name;
            this.key = key;
        }

        public int compare(final Player p1, final Player p2){
            return p2.getExtraData().getInt(key) - p1.getExtraData().getInt(key);
        }

        public void execute(final Player player, final List<Player> players, final int size){
            long total = 0;
            for(final Player p : players)
                total += p.getExtraData().getInt(key);
            player.sendf("@blu@%s Activity @bla@- @red@%,d @blu@Total", name, total);
            Collections.sort(players, this);
            for(int i = size-1; i >= 0; i--){
                final Player p = players.get(i);
                player.sendf("@blu@%s@bla@: @red@%,d", p.getName(), p.getExtraData().get(key));
            }
        }

    }

    public ViewPacketActivityCommand(){
        super("viewpacketactivity", Rank.ADMINISTRATOR);
    }

    public boolean execute(final Player player, final String input){
        final List<Player> players = new ArrayList<>(World.getPlayers());
        int size = DEFAULT_SIZE;
        final String line = filterInput(input).trim();
        if(!line.isEmpty()){
            try{
                size = Math.min(players.size(), Integer.parseInt(line));
            }catch(Exception ex){}
        }
        for(final Style style : Style.values())
            style.execute(player, players, size);
        return true;
    }
}
