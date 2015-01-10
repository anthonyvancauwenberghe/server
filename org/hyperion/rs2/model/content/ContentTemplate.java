package org.hyperion.rs2.model.content;

import org.hyperion.rs2.model.Player;

import java.io.FileNotFoundException;

public interface ContentTemplate {

	public default boolean clickObject(Player player, int type, int a, int b, int c, int d){
        if(type == ClickType.ITEM_ON_ITEM)
            return itemOnItem(player, a, b, c, d);
        if(type == ClickType.NPC_OPTION1)
            return npcOptionOne(player, a, b, c, d);
        if(type == ClickType.EAT)
            return itemOptionOne(player, a, b, c);
        if(type == ClickType.ACTION_BUTTON)
            return actionButton(player, a);
        if(type == ClickType.DIALOGUE_MANAGER)
            return dialogueAction(player, a);
        if(type == ClickType.OBJECT_CLICK1) {
            if(a == ClickId.FIGHT_PITS_DEATH)
                return handleDeath(player);
            return objectClickOne(player, a, b, c);
        }
        return false;
    }//this will work for all items, objects , npcs etc, specify value -1 if the value is unused

	public abstract void init() throws FileNotFoundException;

	public abstract int[] getValues(int type);

    public default boolean itemOnItem(Player player, int used, int usedSlot, int usedWith, int usedWithSlot) { return false; }
    public default boolean npcOptionOne(Player player, int npcId, int npcLocationX, int npcLocationY, int npcSlot) { return false; }
    public default boolean itemOptionOne(Player player, int id, int slot, int interfaceId) { return false; }
    public default boolean objectClickOne(Player player, int id, int x, int y) { return false; }
    public default boolean actionButton(Player player, int buttonId) { return false; }
    public default boolean dialogueAction(Player player, int dialogueId) { return false; }
    public default boolean handleDeath(Player player) { return false; }
}