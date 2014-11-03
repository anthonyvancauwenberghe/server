package org.hyperion.rs2.model.content.skill;

import java.io.FileNotFoundException;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.skill.slayer.SlayerTask;


/**
 * Slayer Class Mad Turnip
 */

public class Slayer implements ContentTemplate {

	/**
	 * Class constructor.
	 */
	public Slayer() {

	}

    private static final int SLAYER_MASTER = 1599, SLAYER_GEM = 4155;

    @Override
    public boolean clickObject(final Player player, final int type, final int npcId, final int slot, final int objId, final int a) {
        if(type == ClickType.EAT) { //slayer gem
            return true;
        }
        if(type == ClickType.NPC_OPTION1) { // talk to slayer masker
            DialogueManager.openDialogue(player, 174);
            return true;
        }
        if(type == ClickType.NPC_DEATH) {
            int slayerXP = player.getSlayerTask().killedTask(npcId);
            if(slayerXP > 0) {
                ContentEntity.addSkillXP(player, slayerXP, Skills.SLAYER);
            }
            if(player.getSlayerTask().getTaskAmount() == 0) {
                player.sendf("You have completed %d tasks in a row and have %d slayer points", player.getSlayerTask().getTotalTasks(),player.getSlayerTask().getSlayerPoints());
            }
            return false;
        }
        return false;
    }



	@Override public void init() throws FileNotFoundException {}


	@Override
	public int[] getValues(int type) {
		if(type == ClickType.NPC_OPTION1) {
			int[] j = {SLAYER_MASTER,};
			return j;
		}
		if(type == ClickType.EAT) { //slayer gem
			int[] j = {SLAYER_GEM,};
			return j;
		}
        if(type == ClickType.NPC_DEATH)
            return SlayerTask.getTasks();
        return null;
	}




}