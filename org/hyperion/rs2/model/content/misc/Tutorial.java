package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.net.ActionSender;

public class Tutorial implements ContentTemplate {

    private static final String[] STEP_DESCRIPTION = {
            "Welcome to ArteroPK! use the command ::tutorial to start your tutorial!",
            "To request for help, use the [::reqhelp reason] command anytime.",
            "To look up the rules, use the ::rules command.",
            "Use the ::tutorial command to continue",
            "Teleport home in order to complete the tutorial",
            "You finished the tutorial! Welcome to ArteroPK!"
    };

    public static void getProgress(Player player) {
        switch(player.getTutorialProgress()) {
            case 1:
                DialogueManager.openDialogue(player, 2000);
                return;
            case 2:
                DialogueManager.openDialogue(player, 2003);
                return;
            case 3:
                DialogueManager.openDialogue(player, 2006);
                return;
            case 4:
                DialogueManager.openDialogue(player, 2009);
                return;
            default:
                player.getActionSender().sendMessage(STEP_DESCRIPTION[player.getTutorialProgress()]);
                return;
        }
    }

    public static void giveReward(Player player) {
        player.getInventory().add(new Item(15273, 100));
        player.getInventory().add(new Item(13889, 1));
        player.getInventory().add(new Item(13895, 1));
        player.getActionSender().sendMessage("The vesta gear will be destroyed after dieing five times with them on you!");
    }

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.DIALOGUE_MANAGER)
            return new int[]{2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009};
        else
            return new int[0];
    }

    @Override
    public boolean dialogueAction(Player player, int dialogueId) {
        switch (dialogueId) {
            case 2000:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This tutorial will go through the basics of ArteroPK", "and teach you what you need to know.", "Completing this tutorial will result in a nice reward!");
                player.getInterfaceState().setNextDialogueId(0, 201);
                return true;
            case 2001:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "First, if you ever need any help, use the @blu@::reqhelp reason", "command to alert a moderator.");
                player.getInterfaceState().setNextDialogueId(0, 202);
                return true;
            case 2002:
                player.getActionSender().sendMessage(STEP_DESCRIPTION[player.getTutorialProgress()]);
                player.setTutorialProgress(2);
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Use the @blu@::rules@bla@ command to learn the what", "you prohibited of doing on this server!");
                player.getInterfaceState().setNextDialogueId(0, 203);
                return true;
            case 2003:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "A good way to make money is pking in Edgeville.", "You can acquire artifacts for pk points and emblems as well!");
                player.getInterfaceState().setNextDialogueId(0, 204);
                return true;
            case 2004:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "There are also bosses you can fight with decent drops.", "If your lucky, you can loot items like claws!");
                player.getInterfaceState().setNextDialogueId(0, 205);
                return true;
            case 2005:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "You can also find guides on our forums. Just go to", "@blu@www.arteropk.com");
                player.setTutorialProgress(3);
                player.getInterfaceState().setNextDialogueId(0, 206);
                return true;
            case 2006:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Team Dungeoneering is a very fun activity that you can", "play if you aren't feeling up to pking!");
                player.getInterfaceState().setNextDialogueId(0, 207);
                return true;
            case 2007:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Click on your ring to teleport to the lobby now.", "Then use the ::tutorial command to continue.");
                player.getInterfaceState().setNextDialogueId(0, 208);
                return true;
            case 2008:
                player.getActionSender().removeChatboxInterface();
                return true;
            case 2009:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "There are also various monster teleports and other", "minigames that are available. Teleport back home and start", "your journey here at ArteroPK!");
                player.getInterfaceState().setNextDialogueId(0, 208);
                return true;
        }
        return false;
    }

}
