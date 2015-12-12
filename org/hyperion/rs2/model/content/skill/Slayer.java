package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.skill.slayer.SlayerTask;
import org.hyperion.rs2.model.shops.SlayerShop;

import java.io.FileNotFoundException;


/**
 * Slayer Class Mad Turnip
 */

public class Slayer implements ContentTemplate {

    private static final int SLAYER_MASTER = 1599, SLAYER_GEM = 4155;

    /**
     * Class constructor.
     */
    public Slayer() {

    }

    @Override
    public boolean clickObject(final Player player, final int type, final int npcId, final int slot, final int objId, final int a) {
        if(type == ClickType.EAT){ //slayer gem
            player.sendMessage("You have " + player.getSlayer().getTaskAmount() + " " + player.getSlayer().getTask() + " npcs left to kill", "You have " + player.getSlayer().getSlayerPoints() + " slayer points", "You have completed " + player.getSlayer().getTotalTasks() + " tasks");
            return true;
        }
        if(type == ClickType.NPC_OPTION1){ // talk to slayer masker
            DialogueManager.openDialogue(player, 174);
            return true;
        }
        if(type == ClickType.NPC_DEATH){
            final int slayerXP = player.getSlayer().killedTask(npcId);
            if(slayerXP > 0){
                ContentEntity.addSkillXP(player, slayerXP, Skills.SLAYER);
                if(player.getSlayer().getTaskAmount() == 0){
                    player.getAchievementTracker().slayerTaskCompleted(npcId);
                    player.sendf("You have completed %d tasks in a row and have %d slayer points", player.getSlayer().getTotalTasks(), player.getSlayer().getSlayerPoints());
                }
            }
            return false;
        }
        if(type == ClickType.ITEM_ON_ITEM){
            final int usedItem = npcId;
            final int onItem = objId;
            if(usedItem == SlayerShop.SLAYER_HELM || onItem == SlayerShop.SLAYER_HELM){
                if(player.getInventory().contains(SlayerShop.FOCUS_SIGHT) && player.getInventory().contains(SlayerShop.HEX_CREST) && player.getInventory().contains(SlayerShop.SLAYER_HELM)){
                    player.getInventory().remove(Item.create(SlayerShop.FOCUS_SIGHT));
                    player.getInventory().remove(Item.create(SlayerShop.HEX_CREST));
                    player.getInventory().remove(Item.create(SlayerShop.SLAYER_HELM));
                    player.getInventory().add(Item.create(SlayerShop.FULL_HELM));
                }
            }
        }
        if(type == ClickType.OBJECT_CLICK1){
            final int objectId = npcId;
            if(objectId == 4493)
                player.setTeleportTarget(Location.create(3433, 3538, 1));
            if(objectId == 4495)
                player.setTeleportTarget(Location.create(3417, 3541, 2));
            if(objectId == 10529)
                player.setTeleportTarget(Location.create(3427, 3555, 1));
            if(objectId == 5126)
                player.setTeleportTarget(Location.create(3445, 3555, 2));
            if(objectId == 9319)
                player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() + 1));
        }
        return false;
    }


    @Override
    public void init() throws FileNotFoundException {
    }


    @Override
    public int[] getValues(final int type) {
        if(type == ClickType.NPC_OPTION1){
            final int[] j = {SLAYER_MASTER,};
            return j;
        }
        if(type == ClickType.EAT){ //slayer gem
            final int[] j = {SLAYER_GEM,};
            return j;
        }
        if(type == ClickType.NPC_DEATH){

            return SlayerTask.getTasks();
        }
        if(type == ClickType.ITEM_ON_ITEM){
            return new int[]{SlayerShop.FOCUS_SIGHT, SlayerShop.SLAYER_HELM, SlayerShop.HEX_CREST,};
        }
        if(type == ClickType.OBJECT_CLICK1){
            return new int[]{9319, 4495, 4493, 10529, 5126};
        }
        return null;
    }


}