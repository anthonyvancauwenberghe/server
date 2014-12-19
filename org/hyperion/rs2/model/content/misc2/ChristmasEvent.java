package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.net.ActionSender;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/18/14
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChristmasEvent implements ContentTemplate {


    private static final Item GIFT = Item.create(6542, 1);
    private static final String CHRISTMAS_EVENT_KEY = "christmasevent";


    @Override
    public void init() throws FileNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getValues(int type) {
        if(type == 6)
            return new int[]{358};
        if(type == ClickType.DIALOGUE_MANAGER)
            return new int[]{5000, 5001, 5002} ;
        if(type == ClickType.NPC_OPTION1)
            return new int[]{9400};
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean npcOptionOne(Player player, int npcId, int npcLocationX, int npcLocationY, int npcSlot) {
        int christmasevent = player.getPermExtraData().getInt(CHRISTMAS_EVENT_KEY);
        switch(christmasevent) {
            case 0:
                DialogueManager.openDialogue(player, 5000);
                break;
            case 1:
                break;
        }
        return true;
    }

    @Override
    public boolean itemOptionOne(Player player, int id, int slot, int interfaceId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean objectClickOne(Player player, int id, int x, int y) {
        if(x == 3091 && y == 3507 && player.getPermExtraData().getInt(CHRISTMAS_EVENT_KEY) == 4) {
            player.getInventory().add(GIFT);
            player.sendMessage("You find the present!", "You should return it back to santa...");
            player.getExtraData().put(CHRISTMAS_EVENT_KEY, 5);
            return true;
        }
        return false;
    }

    @Override
    public boolean dialogueAction(Player player, int dialogueId) {
        switch(dialogueId) {
            case 5000:
                player.getActionSender().sendDialogue("Santa", ActionSender.DialogueType.NPC, 9400, Animation.FacialAnimation.DEFAULT,
                        "I accidentaly lost my present in an area not far from here...", "Young warrior! Care to help me?");
                player.getInterfaceState().setNextDialogueId(0, 5001);
                break;
            case 5001:
                player.getActionSender().sendDialogue("Help santa?", ActionSender.DialogueType.OPTION, 9400, Animation.FacialAnimation.DEFAULT,
                        "Yes","No");
                player.getInterfaceState().setNextDialogueId(0, 5002);
                player.getInterfaceState().setNextDialogueId(1, -1);
                break;
            case 5002:
                player.getActionSender().sendDialogue(player.getName(), ActionSender.DialogueType.PLAYER, 9400, Animation.FacialAnimation.DEFAULT,
                        "I want to help you but I don't think","it is that urgent right now...", "I'll talk to you again soon!");
                player.getInterfaceState().setNextDialogueId(0, -1);
                break;
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
