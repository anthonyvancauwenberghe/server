package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ClickId;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc2.Food;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.model.itf.impl.DungoneeringParty;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.util.Misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/21/15
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DungeoneeringManager implements ContentTemplate {

    private static final int DIALOGUE_ID = 7000;

    private static List<Integer> items;

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.EAT)
            return new int[]{15707};
        else if(type == ClickType.OBJECT_CLICK1)
            return new int[]{2447, 2804};
        else if(type == ClickType.DIALOGUE_MANAGER)
            return new int[]{DIALOGUE_ID, DIALOGUE_ID + 1};
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean itemOptionOne(Player player, int id, int slot, int interfaceId) {
        if(cantJoin(player))  {
            player.sendMessage("You can only bring the ring of kinship - no summoning allowed!");
            return false;
        }
        Magic.teleport(player, Location.create(2987, 9637, 0), false);
        player.SummoningCounter = 0;
        return true;
    }

    @Override
    public boolean npcOptionOne(Player player, int npcId, int npcLocationX, int npcLocationY, int npcSlot) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean objectClickOne(Player player, int id, int x, int y) {
        switch(id) {
            case 2447:
                final Location loc = player.getDungoneering().clickPortal();
                if(loc == null) {
                    player.sendMessage("You need to clear the room before progressing");
                    return true;
                }
                player.setTeleportTarget(loc);
                player.getDungoneering().getRoom().initialize();
                return true;
        }
        return false;
    }

    @Override
    public boolean dialogueAction(Player player, int dialogueId) {
        switch(dialogueId) {
            case 7000:
            case 7001:
                final DungoneeringParty itf = InterfaceManager.<DungoneeringParty>get(DungoneeringParty.ID);
                itf.sendResponse(player, dialogueId - 7000);
                break;
        }
        return true;
    }

    @Override
    public boolean handleDeath(Player player) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static final List<Integer> parse() {
        final List<ItemDefinition> items = new ArrayList<>();
        for(int i = 0; i < 13_000; i++) {
            if(!ItemSpawning.canSpawn(i))
                continue;
            final ItemDefinition def = ItemDefinition.forId(i);
            if(def == null) continue;
            if(nonviable(def) && Food.get(i) == null)
                continue;
            if(def.isNoted())
                continue;
            items.add(def);

        }

        final Iterator<ItemDefinition> defs = items.iterator();
        while(defs.hasNext()) {
            final ItemDefinition current = defs.next();
            if(defs.hasNext()) {
                if(current.getName().equalsIgnoreCase(defs.next().getName()))
                    defs.remove();
            }
        }

        System.out.println(items.size());
        return items.stream().map(d -> d.getId()).collect(Collectors.toList());
    }

    public static final boolean cantJoin(final Player player) {
        return ContentEntity.getTotalAmountOfEquipmentItems(player) > 0 || !(ContentEntity.getTotalAmountOfItems(player) == 1 && player.getInventory().contains(15707)) ||  player.cE.summonedNpc != null || (player.getBoB() != null && player.getBoB().freeSlots() != player.getBoB().capacity());
    }


    private static final boolean nonviable(final ItemDefinition def) {
        if(!full(def.getBonus()) || def.getName().contains("(") || def.getName().contains("/") || def.getName().endsWith("0") || def.getName().endsWith("5") || def.getName().toLowerCase().startsWith("anger"))
            return true;
        return false;
    }
    private static final boolean full(final int[] bonus) {
        for(int i : bonus)
            if(i > 15)
                return true;
        return false;
    }

    public static List<Integer> getItems() {
        if(items == null)
            items = parse();
        return items;
    }

    public static int randomItem() {
        return getItems().get(Misc.random(getItems().size() - 1));
    }

}
