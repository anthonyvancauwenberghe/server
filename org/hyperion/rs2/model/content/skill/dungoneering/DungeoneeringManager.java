package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.packet.CommandPacketHandler;

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
        if(type == ClickType.EAT)
            return new int[]{15707};
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean itemOptionOne(Player player, int id, int slot, int interfaceId) {
        if(ContentEntity.getTotalAmountOfEquipmentItems(player) > 0 || ContentEntity.getTotalAmountOfItems(player) > 0)  {
            player.sendMessage("Please remove all items before teleporting here");
            return false;
        }
        Magic.teleport(player, Location.create(2987, 9637, 0), false);
        player.SummoningCounter = 0;

        return true;
    }

    @Override
    public boolean npcOptionOne(Player player, int npcId, int npcLocationX, int npcLocationY, int npcSlot) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean objectClickOne(Player player, int id, int x, int y) {
        switch(id) {
            case 7000:
                player.getActionSender().sendDialogue("Join "+player.getName()+ "?", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT,
                        "Easy", "Medium", "Hard");
                final InterfaceState state = player.getInterfaceState();
                state.setNextDialogueId(0, 7001);
                state.setNextDialogueId(1, 7002);
                state.setNextDialogueId(2, 7003);
                break;
            case 7001:
            case 7002:
            case 7003:
                final DungeonDifficulty difficulty = DungeonDifficulty.values()[id - 7001];
                player.getDungoneering().setChosen(difficulty);
                break;

        }
        return true;
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
