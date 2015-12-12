package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class ZombieMinigame implements ContentTemplate {

    public static final int START_NPC = 0, START_DIALOG = 164;
    private static final List<Integer> creatures = Arrays.asList(3, 5);

    public static boolean canJoin(final Player player) {
        return canJoin(ContentEntity.getTotalAmountOfEquipmentItems(player), ContentEntity.getTotalAmountOfItems(player));
    }

    public static boolean canJoin(final int equipSlots, final int invSlots) {
        if(equipSlots == 0 && invSlots == 0)
            return true;
        return false;
    }

    /**
     * Handles incoming packets related to the minigame
     */
    @Override
    public boolean clickObject(final Player player, final int clickType, final int clickId, final int c, final int d, final int i1) {
        if(clickType == ClickType.NPC_OPTION1){
            switch(clickId){
                case START_NPC:
                    DialogueManager.openDialogue(player, START_DIALOG);
                    break;
            }
        }else if(clickType == ClickType.NPC_DEATH){
            for(final int creatureId : creatures){
                if(creatureId == clickId){

                }
            }
        }
        return false;
    }

    public void startGame(final Player player) {

    }

    public void startWaves(final Player player) {

    }

    public void updateInterface(final Player player) {

    }

    @Override
    public int[] getValues(final int type) {
        if(type == ClickType.NPC_OPTION1)
            return new int[]{START_NPC};
        else if(type == ClickType.NPC_DEATH){
            final Integer[] values = creatures.toArray(new Integer[creatures.size()]);
            final int[] vals = new int[values.length];
            for(int i = 0; i < vals.length; i++){
                vals[i] = values[i];
            }
            return vals;
        }
        return null;
    }

    @Deprecated
    public void init() throws FileNotFoundException {

    }

}
