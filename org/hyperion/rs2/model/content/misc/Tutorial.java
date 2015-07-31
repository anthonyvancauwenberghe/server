package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.net.ActionSender;

public class Tutorial implements ContentTemplate {

    private static final String[] STEP_DESCRIPTION = {
            "Welcome to ArteroPK! Completing this tutorial gives you '@dre@Vesta (deg)@bla@', a very powerful armor on this server",
            "To request for help, use the @blu@::reqhelp@bla@ command anytime.",
            "To look up the rules, use the @blu@::rules@bla@ command.",
            "Use the @blu@::tutorial@bla@ command to continue if the dialogue does not continue automatically",
            "Teleport home in order to complete the tutorial",
            "Use the @blu@::tutorial@bla@ command to continue if the dialogue does not continue automatically",
            "Use the @blu@::tutorial@bla@ command to continue if the dialogue does not continue automatically",
            "You have completed the tutorial!",
            "You chose to skip the tutorial..."
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
        player.getActionSender().sendMessage("The Vesta gear has a 20% chance of being destroyed on death!");
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
                        "This tutorial will go through the basics", "of ArteroPK and teach you what you need to know.", "After the tutorial you will receive your reward!");
                player.getInterfaceState().setNextDialogueId(0, 2101);
                return true;
            case 2101:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "First, if you ever need any help, use the @blu@::reqhelp", "command to alert a moderator. Be sure to add a reason!");
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
                        "A good way to make money is PKing in Edgeville.", "You can acquire emblems and artifacts for PK points!");
                player.getInterfaceState().setNextDialogueId(0, 2104);
                return true;
            case 2104:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "There are also bosses you can fight with decent drops,", "if you're lucky, you can loot strong items like claws!");
                player.getInterfaceState().setNextDialogueId(0, 2105);
                return true;
            case 2105:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "You can find guides on our forums. Just go to", "@blu@www.arteropk.com");
                player.setTutorialProgress(3);
                player.getInterfaceState().setNextDialogueId(0, 2106);
                return true;
            case 2106:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Team dungeoneering is a very fun activity that you can", "play if you aren't feeling up to PKing!");
                player.getInterfaceState().setNextDialogueId(0, 2107);
                return true;
            case 2107:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Click on your ring to @blu@teleport to the lobby@bla@ now.", "You will be teleported to the team dungeoneering lobby.");
                player.getInterfaceState().setNextDialogueId(0, 2108);
                return true;
            case 2108:
                player.getActionSender().sendMessage("Click your '@dre@Ring of Kinship@bla@' and then use @blu@::tutorial@bla@ to continue.");
                player.getActionSender().removeChatboxInterface();
                return true;
            case 2109:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "There are also various monster teleports and other", "minigames that are available.");
                player.getInterfaceState().setNextDialogueId(0, 2110);
                return true;
            case 2110:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "For this next part, we will now show you some", "important locations throughout ArteroPK!");
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
                        "This is the home of ArteroPK, all the shops are here.", "You can use the @blu@::home@bla@ command to teleport here in the future.");
                player.getInterfaceState().setNextDialogueId(0, 2113);
                return true;
            case 2113:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(2539, 4717, 0), true);
                DialogueManager.openDialogue(player, 2114);
                return true;
            case 2114:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the mage bank, you can get here by doing the", "@blu@::mb @bla@command. It is a popular PKing spot,", "just click on the lever and you are good to go.");
                player.getInterfaceState().setNextDialogueId(0, 2115);
                return true;
            case 2115:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(2480, 5174, 0), true);
                DialogueManager.openDialogue(player, 2116);
                return true;
            case 2116:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Inside this cave is a high risk PK zone. ", "None of your items are protected beyond that point!");
                player.getInterfaceState().setNextDialogueId(0, 2117);
                return true;
            case 2117:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(2373, 4972, 0), true);
                DialogueManager.openDialogue(player, 2118);
                return true;
            case 2118:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the donator zone. You must buy ", "2000 donator points to become a @red@Donator@bla@.", " and 10,000 donator points to become a @gre@Super Donator@bla@.");
                player.getInterfaceState().setNextDialogueId(0, 2119);
                return true;
            case 2119:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "You can donate by typing @blu@::donate@bla@ in the chatbox.", "This is where you can PvM for really good gear. ", "Now we'll take you to the other donator zone for skilling!");
                player.getInterfaceState().setNextDialogueId(0, 2120);
                return true;
            case 2120:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(3793, 2851, 0), true);
                DialogueManager.openDialogue(player, 2121);
                return true;
            case 2121:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the donator zone where you can skill,", "and now we'll show you where most players PK!");
                player.getInterfaceState().setNextDialogueId(0, 2122);
                return true;
            case 2122:
                player.getActionSender().removeChatboxInterface();
                Magic.teleport(player, Location.create(3088, 3517, 0), true);
                DialogueManager.openDialogue(player, 2123);
                return true;
            case 2123:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "This is the main place where people PK. If you want to", "gear up fast, go to the achievements tab and click on a set.", "This will gear you up so you are ready to PK!");
                player.getInterfaceState().setNextDialogueId(0, 2124);
                return true;
            case 2124:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "If you have any other questions, use the @blu@::reqhelp@bla@ command", "and remember to have fun!");
                player.getInterfaceState().setNextDialogueId(0, 2125);
                return true;
            case 2125:
                player.getActionSender().sendDialogue("Tutorial", ActionSender.DialogueType.NPC, 1, Animation.FacialAnimation.DEFAULT,
                        "Congratulations, you have completed the tutorial.", "Be sure to try out the armor we placed in your bag...", "It's very powerful.");
                player.getInterfaceState().setNextDialogueId(0, 10003);
                if(player.getTutorialProgress() == 6) {
                    player.setTutorialProgress(7);
                    giveReward(player);
                }
                return true;
        }
        return false;
    }

}
