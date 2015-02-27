package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.misc2.Edgeville;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/20/15
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class Dungeon {
    private final Map<Player, Integer> deaths = new HashMap<>();

    private List<Room> rooms = new ArrayList<>();

    private final List<Player> players;
    public final int heightLevel;
    public final DungeonDifficulty difficulty;
    private final long start_time;

    public Dungeon(final List<Player> players, final DungeonDifficulty difficulty) {
        this.players = players;
        this.heightLevel = players.get(0).getIndex();
        this.difficulty = difficulty;
        this.start_time = System.currentTimeMillis();
    }


    public void start() {
        addRooms();
        assignChildren();
        final Room start = rooms.get(0);
        for(final Player player : players) {
            player.setTeleportTarget(start.getSpawnLocation());
            player.getDungoneering().setCurrentRoom(start);
            for(final Item bound : player.getDungoneering().getBinds())
                player.getInventory().add(bound);

        }
        start.initialized = true;
        final Point loc = start.definition.randomLoc();
        final NPC trader = World.getWorld().getNPCManager().addNPC(Location.create(loc.x, loc.y, start.heightLevel), DungeoneeringManager.TRADER_ID, -1);
        start.events.add(trader);

    }


    public final void remove(final Player player, boolean complete) {
        player.setTeleportTarget(DungeoneeringManager.LOBBY);
        player.getDungoneering().setCurrentDungeon(null);
        player.getDungoneering().loadXP(player.getSkills(), false);
        players.remove(player);
        if(players.size() == 0)
            destroy();
        if(complete) {
            long elapsed_time = System.currentTimeMillis() - start_time;
            long delta_time = difficulty.time - elapsed_time;
            long time = TimeUnit.MINUTES.convert(delta_time, TimeUnit.MILLISECONDS);
            double multiplier = (time/10D) + 1.0;
            int death = deaths.getOrDefault(player, 0);
            double death_penalty = Math.pow(0.85, death);
            final int xp = (int)((difficulty.xp * multiplier) * death_penalty);

            player.getSkills().addExperience(Skills.DUNGEONINEERING, xp);
            player.sendMessage("@red@DUNGEON COMPLETE", "@blu@Exp: @bla@ "+xp, "@blu@Time: @bla@" + elapsed_time/1000 +" seconds", "@blu@Deaths: @bla@"+death);
        }

        for(final Item item : player.getInventory().toArray()) {
            if(item.getId() != 15707)
                player.getInventory().remove(item);
        }
    }

    public void addRooms() {
        int loopAround = 0;
        int size = difficulty.rooms;
        while(size > 0) {
            final List<RoomDefinition> list = new ArrayList<>();
            list.addAll(RoomDefinition.ROOM_DEFINITIONS_LIST);
            Collections.shuffle(list);
            for(final RoomDefinition def : list) {
                rooms.add(def.getRoom(this, loopAround));
                if(--size == 0)
                    break;
            }
            loopAround++;
        }
    }

    public void complete() {
        for(final Player player : players) {
            remove(player, true);
        }
    }

    public void assignChildren() {
        for(int i = 0; i < rooms.size() - 1; i++) {
            rooms.get(i).setChild(rooms.get(i+1));
        }
        final Room boss = rooms.get(rooms.size() - 1);
        boss.boss = true;
        boss.heightLevel = (int)(heightLevel * Math.pow(4, 3));

        for(int i = 1; i < rooms.size(); i++) {
            rooms.get(i).setParent(rooms.get(i-1));
        }

    }


    public void destroy() {
        for(final Room room : rooms)
            room.destroy();
        rooms.clear();
        players.clear();

    }

    public Room getStartRoom() {
        return rooms.get(0);
    }

    public void kill(final Player player) {
        int old = deaths.getOrDefault(player, 0);
        deaths.put(player, old + 1);
    }

}
