package org.hyperion.rs2.model.content;

import org.hyperion.rs2.model.Player;

import java.io.FileNotFoundException;

public interface ContentTemplate {

    default boolean clickObject(final Player player, final int type, final int a, final int b, final int c, final int d) {
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
        if(type == ClickType.OBJECT_CLICK1){
            if(a == ClickId.FIGHT_PITS_DEATH)
                return handleDeath(player);
            return objectClickOne(player, a, b, c);
        }
        return clickObject2(player, type, a, b, c, d);
    }//this will work for all items, objects , npcs etc, specify value -1 if the value is unused

    default boolean clickObject2(final Player player, final int type, final int a, final int b, final int c, final int d) {
        return false;
    }

    default void init() throws FileNotFoundException {
        ;
    }

    int[] getValues(int type);

    default boolean itemOnItem(final Player player, final int used, final int usedSlot, final int usedWith, final int usedWithSlot) {
        return false;
    }

    default boolean npcOptionOne(final Player player, final int npcId, final int npcLocationX, final int npcLocationY, final int npcSlot) {
        return false;
    }

    default boolean itemOptionOne(final Player player, final int id, final int slot, final int interfaceId) {
        return false;
    }

    default boolean objectClickOne(final Player player, final int id, final int x, final int y) {
        return false;
    }

    default boolean actionButton(final Player player, final int buttonId) {
        return false;
    }

    default boolean dialogueAction(final Player player, final int dialogueId) {
        return false;
    }

    default boolean handleDeath(final Player player) {
        return false;
    }

    default boolean npcDeath(final Player player, final int npcId, final int npcLocationX, final int npcLocationY, final int npcSlot) {
        return false;
    }
}