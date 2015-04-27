package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 4/23/15
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class BarrowsFFA extends SpecialArea{

    private static final int HEIGHT_LEVEL = 0;
    public static final Location PORTAL = Location.create(0, 0, 0);
    private static final Location GAME = Location.create(0, 0, HEIGHT_LEVEL);
    private static final Location LOBBY = Location.create(0, 0 , 0);

    private final List<Player> lobby = new ArrayList<>(), game = new ArrayList<>();

    private int gameTime, nextGameTime;

    {
        World.getWorld().submit(new Event(100) {
            @Override
            public void execute() throws IOException {
                process();
            }
        });
    }

    public enum BarrowSet {
        DHAROK(),
        KARIL(),
        AHRIM(),
        GUTHAN(),
        TORAGS(),
        VERACS();


        private final Item[] items;

        private BarrowSet(final Integer... ids) {
            if(ids.length != 4)
                throw new IllegalArgumentException("Length of ids is invalid");
            this.items = Stream.of(ids).map(Item::create).toArray(Item[]::new);
        }


        public void equip(final Player player) {
            int i = 0;
            for(; i < 4; i++) {
                player.getEquipment().set(Equipment.getType(items[i]).getSlot(), items[i]);
            }
            for(; i < items.length; i++)
                player.getInventory().add(items[i]);
        }


    }
    //handles timers, interfaces & shit
    public void process() {
        if(gameTime > 0) {
            //update # of players, and game time left, tell people in lobby that a game is currently in progress!
        } else if(--nextGameTime == 0) {
            //update the interface, inform people of the jank
        }
    }

    public void startGame() {
        game.addAll(lobby);
        lobby.clear();
        for(final Player player : game)
            enter(player);
    }

    public void endGame() {
        //there is a winner
        if(game.size() == 1) {
            final Player winner = game.get(0);
            exit(winner);
            // reward(winner); - give the reward
        } else {
            for(final Player player : game)
                exit(player);
        }

    }



    @Override
    public boolean canSpawn() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPkArea() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getPkLevel() {
        return 120;
    }

    @Override
    public Location getDefaultLocation() {
        return GAME;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean inArea(Player player) {
        final int x = player.getLocation().getX();
        final int y = player.getLocation().getY();
        final int z = player.getLocation().getZ();
        return z == HEIGHT_LEVEL && (x > y);  //borders, not implemented
    }

    @Override
    public String canEnter(Player player) {
        if(!game.contains(player))
            return "You are not allowed here";
        return "";
    }

    @Override
    public void enter(final Player player) {
        final String enter = canEnter(player);
        if(enter.length() > 2)
            player.sendMessage(enter);
        else Magic.teleport(player, getDefaultLocation(), false);

    }

    @Override
    public void exit(final Player player) {
        if ((lobby.contains(player) && lobby.remove(player)) || (game.contains(player) && game.remove(player))) {
            player.setTeleportTarget(PORTAL);
            player.getEquipment().clear();
            player.getInventory().clear();
        }
    }


   // @Override
    public int[] getValues(int type) {
        return new int[0];
    }

    //used to enter lobby
    //@Override
    public boolean objectClickOne(Player player, int id, int x, int y) {
        return false;
    }

    //used to pick your barrows set
    //@Override
    public boolean actionButton(Player player, int buttonId) {
        final BarrowSet set = BarrowSet.values()[buttonId - /*some number*/0];
        player.getExtraData().put("barrowset", set);
        return false;
    }

}
