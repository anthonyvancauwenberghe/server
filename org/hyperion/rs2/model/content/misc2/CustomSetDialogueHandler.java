package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.net.ActionSender;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/8/14
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSetDialogueHandler implements ContentTemplate {


    @Override
    public void init() throws FileNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.DIALOGUE_MANAGER)
            return new int[]{1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007};  //To change body of implemented methods use File | Settings | File Templates.
        else if(type == ClickType.ACTION_BUTTON)
            return new int[]{29162, 29163};
        else
            return new int[0];
    }

    public boolean actionButton(Player player, int actionButton) {
        if(player.wildernessLevel > 0) {
            player.getActionSender().sendMessage("You cannot spawn Items in the wilderness.");
            return false;
        }
        switch(actionButton) {
            case 29162:
                DialogueManager.openDialogue(player, 1000);
                return true;
            case 29163:
                DialogueManager.openDialogue(player, 1004);
                return true;
        }
        return false;
    }

    @Override
    public boolean dialogueAction(Player player, int dialogueId) {
        switch(dialogueId) {
            case 1000:
                player.getActionSender().sendDialogue("Are you sure?", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT,
                        "Save slot 1",
                        "Save slot 2",
                        "Save slot 3");
                player.getInterfaceState().setNextDialogueId(0, 194);
                player.getInterfaceState().setNextDialogueId(1, 195);
                player.getInterfaceState().setNextDialogueId(2, 196);
                return true;
            case 1001:
            case 1002:
            case 1003:
                final boolean saved = player.getCustomSetHolder().save(dialogueId - 1001);
                if(!saved)
                    player.getActionSender().sendMessage("You cannot save into that slot!");
                player.getActionSender().removeChatboxInterface();
                return saved;
            case 1004:
                player.getActionSender().sendDialogue("Are you sure?", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT,
                        "Load slot 1",
                        "Load slot 2",
                        "Load slot 3");
                player.getInterfaceState().setNextDialogueId(0, 198);
                player.getInterfaceState().setNextDialogueId(1, 199);
                player.getInterfaceState().setNextDialogueId(2, 200);
                return true;
            case 1005:
            case 1006:
            case 1007:
                final boolean apply = player.getCustomSetHolder().apply(dialogueId - 1005);
                if(!apply)
                    player.sendMessage("Trouble loading instant set!");
                player.getActionSender().removeChatboxInterface();
                return apply;
        }
        return false;
    }
}
