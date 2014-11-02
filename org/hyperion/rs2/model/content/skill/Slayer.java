package org.hyperion.rs2.model.content.skill;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.skill.slayer.SlayerTasks;


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
            if(!player.getSlayerTask().assignTask())
                player.sendf("You still have %d %s to kill!", player.getSlayerTask().getTaskAmount(), player.getSlayerTask().getTask());
            return true;
        }
        if(type == ClickType.NPC_DEATH) {
            if(player.getSlayerTask().killedTask(npcId));
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
            return SlayerTasks.getTasks();
        return null;
	}




}