package org.hyperion.rs2.model.content;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

public class ContentEntity {

    public static double getXp(final Player player, final int skill) {
        return player.getSkills().getExperience(skill);
    }


    public static int getTotalAmountOfItems(final Player player) {
        int count = 0;
        for(final Item i : player.getInventory().toArray()){
            if(i == null)
                continue;
            count += i.getCount();
        }
        return count;
    }

    public static int getTotalAmountOfEquipmentItems(final Player player) {
        int count = 0;
        for(final Item i : player.getEquipment().toArray()){
            if(i == null)
                continue;
            count += i.getCount();
        }
        return count;
    }


    public static int getItemAmount(final Player player, final int id) {
        if(player.getInventory().getSlotById(id) < 0)
            return 0;
        final Item i = player.getInventory().get(player.getInventory().getSlotById(id));
        if(i == null)
            return 0;
        else if(i.getDefinition().isStackable())
            return i.getCount();
        else{
            int count = 0;
            for(final Item i2 : player.getInventory().toArray()){
                if(i2 != null){
                    if(i2.getId() == id){
                        count++;
                    }
                }
            }
            return count;
        }
    }

    public static void sendInterfaceModel(final Player player, final int id, final int zoom, final int model) {
        player.getActionSender().sendInterfaceModel(id, zoom, model);
    }

    public static void sendString(final Player player, final String s, final int id) {
        player.getActionSender().sendString(id, s);
    }

    public static void removeAllWindows(final Player player) {
        player.getActionSender().removeAllInterfaces();
    }

    public static void refreshSmithingScreen(final Player player, final int writeFrame, final int[][] SMITHING_ITEMS) {
        player.getActionSender().sendUpdateSmith(writeFrame, SMITHING_ITEMS);
    }

    public static void showInterface(final Player player, final int id) {
        player.getActionSender().showInterface(id);
    }

    public static int getItemSlot(final Player player, final int item) {
        return player.getInventory().getSlotById(item);
    }

    public static void deleteItemAll(final Player player, final int id, final int amount) {
        for(final Item i : player.getInventory().toArray()){
            if(i == null)
                continue;
            if(i.getId() == id){
                player.getInventory().remove(i);
            }
        }
    }

    public static void turnTo(final Player p, final int x, final int y) {
        p.face(Location.create(x, y, 0));
    }

    public static void playerGfx(final Player player, final int gfx) {
        player.playGraphics(Graphic.create(gfx, 6553600));
    }

    public static void playerGfx(final Player player, final int gfx, final int delay) {
        player.playGraphics(Graphic.create(gfx, delay));
    }

    public static int freeSlots(final Player player) {
        return player.getInventory().freeSlots();
    }

    public static boolean hasRoomFor(final Player player, final int id, final int count) {
        return player.getInventory().hasRoomFor(new Item(id, count));
    }

    public static String getItemName(final int item) {
        return ItemDefinition.forId(item).getName();
    }

    public static String getObjectName(final int id) {
        return "";
    }

    public static void sendMessage(final Player player, final String message) {
        player.getActionSender().sendMessage(message);
    }

    public static boolean isItemInBag(final Player player, final int itemId) {
        if(player.getInventory().getSlotById(itemId) == -1)
            return false;
        final Item i = player.getInventory().get(player.getInventory().getSlotById(itemId));
        if(i == null)
            return false;
        return true;
    }

    public static boolean addItem(final Player player, final int itemId) {
        return addItem(player, itemId, 1);
    }

    public static boolean addItem(final Player player, final int itemId, final int amm) {
        return player.getInventory().add(new Item(itemId, amm));
    }

    public static boolean addItem(final Player player, final int itemId, final int amm, final int slot) {
        final Item item = player.getInventory().get(slot);
        if(item != null){
            return false;
        }
        player.getInventory().add(new Item(itemId, amm), slot);
        return true;
    }

    public static boolean deleteItem(final Player player, final int itemId) {
        return player.getInventory().remove(new Item(itemId, 1)) != 0;
    }

    public static boolean deleteItem(final Player player, final int itemId, final int slot) {
        return deleteItem(player, itemId, slot, 1);
    }

    public static boolean deleteItemA(final Player player, final int itemId, final int am) {
        return player.getInventory().remove(new Item(itemId, am)) != 0;
    }

    public static int returnSkillLevel(final Player player, final int skill) {
        return player.getSkills().getLevel(skill);
    }

    public static void addSkillXP(final Player player, final double addXp, final int skill) {
        player.getSkills().addExperience(skill, addXp);
    }

    public static boolean deleteItem(final Player player, final int itemId, final int slot, final int amount) {
        final Item item = player.getInventory().get(slot);
        if(item == null){
            return false; // invalid packet, or client out of sync
        }
        if(item.getId() != itemId){
            return false; // invalid packet, or client out of sync
        }
        if(item.getCount() < amount){
            return false;
        }
        return player.getInventory().remove(slot, new Item(item.getId(), amount)) > 0;
    }

    public static int getLevelForXP(final Player player, final int skill) {
        return player.getSkills().getLevelForExp(skill);
    }

	/*public static int getLevelForXP(Player player,double xp){
        return getLevelForXP(player,(int) xp);
	}*/

    public static void openDialogue(final Player player, final String[] message) {

    }

    public static void openInterface(final Player player, final int id) {

    }

    public static void freezeWalking(final Player player, final int length) {

    }

    public static int random(final int range) {
        return (int) (java.lang.Math.random() * (range + 1));
    }

    public static void showInterfaceWalkable(final Player player, final int i) {
        player.getActionSender().showInterfaceWalkable(i);
    }

    public static boolean isInArea(final Player player, final int x1, final int y1, final int x2, final int y2) {
        if(player.getLocation().getX() >= x1 && player.getLocation().getX() <= x2 && player.getLocation().getY() >= y1 && player.getLocation().getY() <= y2)
            return true;
        return false;
    }

    public static void teleport(final Player player, final int x, final int y, final int z) {
        player.setTeleportTarget(Location.create(x, y, z));
    }

    public static void startAnimation(final Player player, final int id) {
        player.playAnimation(Animation.create(id, 0));
    }

    public static void startAnimation(final Player player, final int id, final int delay) {
        player.playAnimation(Animation.create(id, delay));
    }

    public static void replaceItem(final Player player, final int slot, final int newId, final int amount) {
        final Item i = player.getInventory().get(slot);
        if(i != null)
            player.getInventory().remove(slot, i);
        //player.getInventory().remove(i);
        addItem(player, newId, amount, slot);
    }

    public static void heal(final Player player, final int heal) {
        player.heal(heal);
    }

    public static void setOvlSkill(final Player player, final int skill, final int am) {
        player.getSkills().setLevel(skill, player.getSkills().getLevelForExp(skill) + am);
    }

    public static void increaseSkill(final Player player, final int skill, final int am, final boolean ignoreHp) {
        //note do not use for HP!!!!!!
        if((!ignoreHp && skill == 3) || skill == 5){
            player.heal(am, skill);
            return;
        }else if(player.getSkills().getLevel(skill) > player.getSkills().getLevelForExp(skill)){
            player.getSkills().setLevel(skill, player.getSkills().getLevelForExp(skill) + am);
        }else{
            player.getSkills().setLevel(skill, player.getSkills().getLevel(skill) + am);
        }
    }

    public static void increaseSkill(final Player player, final int skill, final int am) {
        increaseSkill(player, skill, am, false);
    }

    public static void decreaseSkill(final Player player, final int skill, final int am) {
        if(player.getSkills().getLevel(skill) - am > 1)
            player.getSkills().setLevel(skill, player.getSkills().getLevel(skill) - am);
        else
            player.getSkills().setLevel(skill, 1);
    }

    public static boolean isItemInBag(final Player player, final int item, final int slot) {
        final Item item2 = player.getInventory().get(slot);
        if(item2 == null){
            return false; // invalid packet, or client out of sync
        }
        if(item2.getId() != item){
            return false; // invalid packet, or client out of sync
        }
        return true;
    }

    public static int count99Levels(final Player player) {
        int counter = 0;
        for(int i = 0; i < 21; i++){
            if(player.getSkills().getXPForLevel(i) >= 99)
                counter++;
        }
        return counter;
    }
}