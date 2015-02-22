package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/21/15
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DungeoneeringManager implements ContentTemplate {
    @Override
    public int[] getValues(int type) {
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean npcOptionOne(Player player, int npcId, int npcLocationX, int npcLocationY, int npcSlot) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean objectClickOne(Player player, int id, int x, int y) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean dialogueAction(Player player, int dialogueId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean handleDeath(Player player) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
