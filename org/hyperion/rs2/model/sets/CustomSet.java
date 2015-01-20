package org.hyperion.rs2.model.sets;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.EquipmentReq;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.util.Misc;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/7/14
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSet {

    public static CustomSet rand() {

        final int[] inventory = new int[28];
        final int[] inventoryStackSizes = new int[28];

        for(int i = 0; i < inventory.length; i++) {
            inventory[i] = Misc.random(1000);
            inventoryStackSizes[i] = Misc.random(10000);
        }

        final int[] equipment = new int[] {10828, 4722, 4720, 4151};

        CustomSet set = new CustomSet(equipment, inventory, inventoryStackSizes);

        return set;

    }

    private static final String EQUIP_KEY = "equipment:",
            INV_KEY = "inventory:", INV_STACK_KEY = "inventoryStackSizes:";

    private final int[] equipmentIds,
            inventoryIds, inventoryStackSizes;

    public CustomSet(final int[] equipmentIds, final int[] inventoryIds, final int[] inventoryStackSizes) throws IllegalArgumentException{
        if(inventoryIds.length != 28 || inventoryStackSizes.length != inventoryIds.length)
            throw new IllegalArgumentException("Inventory needs to be 28 slots");

        this.equipmentIds = equipmentIds;
        this.inventoryIds = inventoryIds;
        this.inventoryStackSizes = inventoryStackSizes;
    }

    public boolean apply(final Player player) {
        if(Location.inAttackableArea(player))
            return false;
        for(final Container toClear : new Container[]{player.getInventory(), player.getEquipment()})
            if(!Container.transfer(toClear, player.getBank()))
                return false;
        for(final int id : equipmentIds) {
            final Item item = Item.create(id);
            if(!EquipmentReq.canEquipItem(player, id))
                continue;
            if(!ItemSpawning.canSpawn(id) || player.hardMode())
                if(player.getBank().remove(item) < 1)
                    continue;
            player.getEquipment().set(Equipment.getType(Item.create(id)).getSlot(), Item.create(id));
        }
        for(int index = 0; index < inventoryIds.length; index++) {
            final Item item = Item.create(inventoryIds[index], inventoryStackSizes[index]);
            if(!ItemSpawning.canSpawn(inventoryIds[index]) || player.hardMode())
                player.getInventory().add(Item.create(inventoryIds[index], player.getBank().remove(item)));
            else
                player.getInventory().add(item);
        }
        return true;
    }

    public String toSaveableString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("NEW_SET{");

        builder.append(EQUIP_KEY).append(Arrays.toString(equipmentIds));
        builder.append(";").append(INV_KEY).append(Arrays.toString(inventoryIds));
        builder.append(";").append(INV_STACK_KEY).append(Arrays.toString(inventoryStackSizes));

        builder.append("}");

        return builder.toString();
    }

    public static CustomSet fromGear(final Container inventory, final Container equipment) {
        final int[] equipmentIds = Stream.of(equipment.toArray()).filter(Objects::nonNull).mapToInt(item -> item.getId()).toArray();
        final Item[] items = inventory.toArray();
        final int[] inventoryIds = Stream.of(items).mapToInt(item -> item == null ? 391 : item.getId()).toArray();
        final int[] inventoryStackSizes = Stream.of(items).mapToInt(item -> item == null ? 1 : item.getCount()).toArray();
        return new CustomSet(equipmentIds, inventoryIds, inventoryStackSizes);
    }

    public static CustomSet fromString(String read) {
        read = read.replace("}", "");
        read = read.replace('[', '\0').replace("]", "");
        final List<Integer> equipmentIds = new ArrayList<>();
        String equipment = read.substring(read.indexOf(EQUIP_KEY)+EQUIP_KEY.length(), read.indexOf(";") + 1).trim();
        read = read.replace(equipment, "");
        equipment = equipment.replace(";", "");
        for(final String part : equipment.split(",")) {
            equipmentIds.add(Integer.valueOf(part.trim()));
        }

        final int[] inventoryIds = new int[28],
                inventoryStackSizes = new int[28];

        String inventory = read.substring(read.indexOf(INV_KEY) + INV_KEY.length(), read.indexOf(";") + 1).trim();
        read = read.replace(inventory, "");
        inventory = inventory.replace(";", "");

        int index = 0;
        for(final String part : inventory.split(","))
            inventoryIds[index++] = Integer.valueOf(part.trim());
        index = 0;

        String inventoryStack = read.substring(read.indexOf(INV_STACK_KEY) + INV_STACK_KEY.length()).trim();
        read = read.replace(inventoryStack, "");
        inventoryStack = inventoryStack.replace(";", "");
        for(final String part : inventoryStack.split(","))
            inventoryStackSizes[index++] = Integer.valueOf(part.trim());

        int equipmentId[] = new int[equipmentIds.size()];

        for(int i = 0; i < equipmentIds.size(); i++)
            equipmentId[i] = equipmentIds.get(i);

        return new CustomSet(equipmentId, inventoryIds, inventoryStackSizes);


    }

}
