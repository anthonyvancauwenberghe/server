package org.hyperion.rs2.model.sets.newsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.EquipmentReq;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc.ItemSpawning;

import java.util.Optional;

/**
 * Created by Gilles on 4/11/2015.
 */
public class CustomSet {

    private Item[] equipment;
    private Item[] inventory;

    public CustomSet(final Item[] equipment, final Item[] inventory) {
        this.equipment = equipment;
        this.inventory = inventory;
    }

    public boolean apply(Player player) {
        if(Location.inAttackableArea(player))
            return false;
        if(FightPits.inGame(player) || FightPits.inPits(player))
            return false;
        if(player.getDungeoneering().inDungeon())
            return false;
        for(final Container toClear : new Container[]{player.getInventory(), player.getEquipment()})
            if(!Container.transfer(toClear, player.getBank()))
                return false;
        for(final Item item : equipment) {
            if(item == null || item.getId() == -1)
                continue;
            if(!EquipmentReq.canEquipItem(player, item.getId()))
                continue;
            if(!ItemSpawning.canSpawn(item.getId()) || player.hardMode())
                if(player.getBank().remove(item) < 1)
                    continue;
            player.getEquipment().set(Equipment.getType(item).getSlot(), item);
        }
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i] == null || inventory[i].getId() == -1)
                continue;
            if(!ItemSpawning.canSpawn(inventory[i].getId()) || player.hardMode()) {
                player.getBank().remove(inventory[i]);
                player.getInventory().set(i, inventory[i]);
            } else {
                player.getInventory().set(i, inventory[i]);
            }
        }
        return true;
    }

    public static CustomSet fromCurrent(final Player player) {
        return new CustomSet(player.getEquipment().getCopiedItems(), player.getInventory().getCopiedItems());
    }

    public static CustomSet fromJson(JsonObject jsonObject) {
        Item[] equipment = new Item[14];
        Item[] inventory = new Item[28];
        JsonArray equipmentArray = jsonObject.get("equipment").getAsJsonArray();
        JsonArray inventoryArray = jsonObject.get("inventory").getAsJsonArray();
        int i = 0;
        for(JsonElement element : equipmentArray) {
            JsonObject object = element.getAsJsonObject();
            int id = object.get("id").getAsInt();
            int amount = object.get("amount").getAsInt();
            equipment[i] = new Item(id, amount);
            i++;
        }
        i = 0;
        for(JsonElement element : inventoryArray) {
            JsonObject object = element.getAsJsonObject();
            int id = object.get("id").getAsInt();
            int amount = object.get("amount").getAsInt();
            inventory[i] = new Item(id, amount);
            i++;
        }
        return new CustomSet(equipment, inventory);
    }

    public JsonObject toJson() {
        JsonObject completeObject = new JsonObject();
        JsonArray equipmentArray = new JsonArray();
        JsonArray inventoryArray = new JsonArray();
        for(int i = 0; i < equipment.length; i++) {
            Item item = Optional.ofNullable(equipment[i]).orElse(new Item(-1, 0));
            JsonObject object = new JsonObject();
            object.addProperty("id", item.getId());
            object.addProperty("amount", item.getCount());
            equipmentArray.add(object);
        }
        for(int i = 0; i < inventory.length; i++) {
            Item item = Optional.ofNullable(inventory[i]).orElse(new Item(-1, 0));
            JsonObject object = new JsonObject();
            object.addProperty("id", item.getId());
            object.addProperty("amount", item.getCount());
            inventoryArray.add(object);
        }
        completeObject.add("equipment", equipmentArray);
        completeObject.add("inventory", inventoryArray);
        return completeObject;
    }
}
