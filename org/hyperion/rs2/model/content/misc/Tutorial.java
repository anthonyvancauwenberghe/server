package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.net.ActionSender;

public class Tutorial implements ContentTemplate {

    private static final String[] STEP_DESCRIPTION = {
            "Welcome to ArteroPK! Completing this Tutorial gives you Vesta (deg) very powerful armor on this server",
            "To request for help, use the [::reqhelp reason] command anytime.",
            "To look up the rules, use the ::rules command.",
            "Use the ::tutorial command to continue if the dialogue does not continue automatically",
            "Teleport home in order to complete the tutorial",
            "Use the ::tutorial command to continue",
            "Use the ::tutorial command to continue",
            "You have completed the tutorial!",
            "You choose to skip the tutorial, sorry..."
    };

    public static void getProgress(Player player) {
        switch(player.getTutorialProgress()) {
            case 1:
                DialogueManager.openDialogue(player, 2100);
                return;
            case 2:
                DialogueManager.openDialogue(player, 2103);
                return;
            case 3:
                DialogueManager.openDialogue(player, 2106);
                return;
            case 4:
                DialogueManager.openDialogue(player, 2109);
                return;
            case 6:
                DialogueManager.openDialogue(player, 2111);
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
        player.getActionSender().sendMessage("The vesta gear has a 20% chance of being destroyed on death!");
    }

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.DIALOGUE_MANAGER) {
            int[] values = new int[26];
            for (int i = 2100; i < 2126; i++) {
                values[i - 2100] = i;
            }
            return values;
        } else
            return new int[0];
    }

    @Override
    public boolean dialogueAction(Player player, int dialogueId) {
        switch (dialogueId) {
            case 2100:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This tutorial will go through the basics of ArteroPK", "and teach you what you need to know.", "Completing this tutorial will result in a nice reward!");
                player.getInterfaceState().setNextDialogueId(0, 2101);
                return true;
            case 2101:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "First, if you ever need any help, use the @blu@::reqhelp reason", "command to alert a moderator.");
                player.getInterfaceState().setNextDialogueId(0, 2102);
                return true;
            case 2102:
                player.getActionSender().sendMessage(STEP_DESCRIPTION[player.getTutorialProgress()]);
                player.setTutorialProgress(2);
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Use the @blu@::rules@bla@ command to learn the what", "you prohibited of doing on this server!");
                player.getInterfaceState().setNextDialogueId(0, 2103);
                return true;
            case 2103:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "A good way to make money is pking in Edgeville.", "You can acquire artifacts for pk points and emblems as well!");
                player.getInterfaceState().setNextDialogueId(0, 2104);
                return true;
            case 2104:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "There are also bosses you can fight with decent drops.", "If your lucky, you can loot items like claws!");
                player.getInterfaceState().setNextDialogueId(0, 2105);
                return true;
            case 2105:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "You can also find guides on our forums. Just go to", "@blu@www.arteropk.com");
                player.setTutorialProgress(3);
                player.getInterfaceState().setNextDialogueId(0, 2106);
                return true;
            case 2106:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Team Dungeoneering is a very fun activity that you can", "play if you aren't feeling up to pking!");
                player.getInterfaceState().setNextDialogueId(0, 2107);
                return true;
            case 2107:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Click on your ring to teleport to the lobby now.", "You will be teleported to the Team Dungeoneering Lobby.");
                player.getInterfaceState().setNextDialogueId(0, 2108);
                return true;
            case 2108:
                player.getActionSender().removeChatboxInterface();
                return true;
            case 2109:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "There are also various monster teleports and other", "minigames that are available. ");
                player.getInterfaceState().setNextDialogueId(0, 2110);
                return true;
            case 2110:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "For this next part, we will now show you some ", "cool locations throughout the server!");
                player.getInterfaceState().setNextDialogueId(0, 2111);
                return true;
            case 2111:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Edgeville.LOCATION, true);
                player.setTutorialProgress(6);
                DialogueManager.openDialogue(player, 2112);
                return true;
            case 2112:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the home of the server. All the shops are here.", "You can use the ::home command te teleport here in the future.");
                player.getInterfaceState().setNextDialogueId(0, 2113);
                return true;
            case 2113:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(2539, 4717, 0), true);
                DialogueManager.openDialogue(player, 2114);
                return true;
            case 2114:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the mage bank (use the ::mb command to teleport here in the future)", " It's a popular pking Place! Just click on the lever and you are good to go.");
                player.getInterfaceState().setNextDialogueId(0, 2115);
                return true;
            case 2115:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(2480, 5174, 0), true);
                DialogueManager.openDialogue(player, 2116);
                return true;
            case 2116:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Inside the cave is a high risk PK zone. ", "You will not protect any items beyond that point!");
                player.getInterfaceState().setNextDialogueId(0, 2117);
                return true;
            case 2117:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(2373, 4972, 0), true);
                DialogueManager.openDialogue(player, 2118);
                return true;
            case 2118:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the donator zone (::dp command). In order to become a regular ", "donator you must donate for 2000 donator points.", " Super donator requires 10000 donator points.");
                player.getInterfaceState().setNextDialogueId(0, 2119);
                return true;
            case 2119:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This donator zone is where you can PvM for good gear. ", "Now we'll take you to the other donator zone for PvP!");
                player.getInterfaceState().setNextDialogueId(0, 2120);
                return true;
            case 2120:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(3793, 2851, 0), true);
                DialogueManager.openDialogue(player, 2121);
                return true;
            case 2121:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the other donator zone where you can skill!", "Now we'll show you where most players PK!");
                player.getInterfaceState().setNextDialogueId(0, 2122);
                return true;
            case 2122:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(3088, 3517, 0), true);
                DialogueManager.openDialogue(player, 2123);
                return true;
            case 2123:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the main place people PK. If you want gear", " fast, go to the achievements tab (green tab) and click on an instant set.", "This will automatically gear you up so you can PK!");
                player.getInterfaceState().setNextDialogueId(0, 2124);
                return true;
            case 2124:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "If you have any other questions, use the ::reqhelp command", "and remember to have fun on ArteroPK!");
                player.getInterfaceState().setNextDialogueId(0, 2125);
                return true;
            case 2125:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Congratulations, you have completed the tutorial.", " Enjoy your stay here at ArteroPK!", "Try out your new armor that we placed in your bag it's very powerful");
                player.getInterfaceState().setNextDialogueId(0, 10001);
                if(player.getTutorialProgress() == 6) {
                    player.setTutorialProgress(7);
                    giveReward(player);
                }
                return true;
        }
        return false;
    }

}
