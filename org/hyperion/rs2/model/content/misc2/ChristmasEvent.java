package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.Magic;
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


    public static final int[] SANTA_SUIT_IDS = {14595, 14602, 14603, 11949, 14605, 15426};
    private static final Item GIFT = Item.create(15420, 1);
    private static final String CHRISTMAS_EVENT_KEY = "christmasevent";

    @Override
    public void init() throws FileNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getValues(final int type) {
        if(type == 6)
            return new int[]{358};
        if(type == ClickType.EAT)
            return new int[]{GIFT.getId()};
        if(type == ClickType.DIALOGUE_MANAGER)
            return new int[]{5000, 5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008};
        if(type == ClickType.NPC_OPTION1)
            return new int[]{9400};
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean npcOptionOne(final Player player, final int npcId, final int npcLocationX, final int npcLocationY, final int npcSlot) {
        final int christmasevent = player.getPermExtraData().getInt(CHRISTMAS_EVENT_KEY);
        switch(christmasevent){
            case 0:
            case 1:
                DialogueManager.openDialogue(player, 5000);
                break;
            case 2:
                DialogueManager.openDialogue(player, 5003);
                break;
        }
        return true;
    }

    @Override
    public boolean itemOptionOne(final Player player, final int id, final int slot, final int interfaceId) {
        player.getActionSender().sendDialogue("Are you sure?", ActionSender.DialogueType.OPTION, 9400, Animation.FacialAnimation.DEFAULT, "Yes, Santa is fat anyways", "No... can't ruin Christmas!");
        player.getInterfaceState().setNextDialogueId(0, 5005);
        player.getInterfaceState().setNextDialogueId(1, -1);
        return true;
    }

    @Override
    public boolean objectClickOne(final Player player, final int id, final int x, final int y) {
        if(x == 3091 && y == 3507 && player.getPermExtraData().getInt(CHRISTMAS_EVENT_KEY) == 1){
            player.getInventory().add(GIFT);
            player.getPermExtraData().put(CHRISTMAS_EVENT_KEY, 2);
            player.getActionSender().sendDialogue(player.getName(), ActionSender.DialogueType.PLAYER, GIFT.getId(), Animation.FacialAnimation.PLAIN_EVIL, "I found his present...", "I should probably give it back to him");
            return true;
        }
        return false;
    }

    @Override
    public boolean dialogueAction(final Player player, final int dialogueId) {
        switch(dialogueId){
            case 5000:
                player.getActionSender().sendDialogue("Santa", ActionSender.DialogueType.NPC, 9400, Animation.FacialAnimation.DEFAULT, "Christmas and new years was saved young warrior!", "Maybe next year I will need your assistance");
                player.getInterfaceState().setNextDialogueId(0, -1);
                break;
            case 5001:
                player.getActionSender().sendDialogue("Help santa?", ActionSender.DialogueType.OPTION, 9400, Animation.FacialAnimation.DEFAULT, "Yes", "No");
                player.getInterfaceState().setNextDialogueId(0, 5002);
                player.getInterfaceState().setNextDialogueId(1, -1);
                break;
            case 5002:
                Magic.teleport(player, Location.create(3096, 3509, 1), true);
                player.getActionSender().removeChatboxInterface();
                player.getActionSender().sendDialogue("Find the present", ActionSender.DialogueType.PLAYER, 9400, Animation.FacialAnimation.DISTRESSED, "Must be inside one of these crates...");
                player.sendMessage("You need to search for Santa's gift in the crates");
                player.getInterfaceState().setNextDialogueId(0, -1);
                player.getPermExtraData().put(CHRISTMAS_EVENT_KEY, 1);
                break;
            case 5003:
                if(player.getInventory().contains(GIFT.getId())){
                    player.getActionSender().sendDialogue("Santa", ActionSender.DialogueType.NPC, 9400, Animation.FacialAnimation.DEFAULT, "Thank you kind warrior");
                    player.getInterfaceState().setNextDialogueId(0, 5004);

                }else{
                    player.getActionSender().sendDialogue("Santa", ActionSender.DialogueType.NPC, 9400, Animation.FacialAnimation.DEFAULT, "Where is my present?!");
                    player.getInterfaceState().setNextDialogueId(0, -1);
                }
                break;
            case 5004:
                handleReward(player, true);
                break;
            case 5005:
                player.getActionSender().sendDialogue("Santa", ActionSender.DialogueType.NPC, 9400, Animation.FacialAnimation.DEFAULT, "What are you doing?!");
                player.getInterfaceState().setNextDialogueId(0, 5006);
                break;
            case 5006:
                player.getActionSender().sendDialogue(player.getName(), ActionSender.DialogueType.PLAYER, 9400, Animation.FacialAnimation.DELIGHTED_EVIL, "What are you going to do?", "Sit on me?");
                player.getInterfaceState().setNextDialogueId(0, 5007);
                break;
            case 5007:
                player.getActionSender().sendDialogue("Santa", ActionSender.DialogueType.NPC, 9400, Animation.FacialAnimation.DEFAULT, "Be cursed.");
                player.getInterfaceState().setNextDialogueId(0, 5008);
                break;
            case 5008:
                handleReward(player, false);
                break;
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleReward(final Player player, final boolean fair) {
        player.getActionSender().removeChatboxInterface();
        if(player.getInventory().freeSlots() <= 8){
            player.sendMessage("You need at least 8 free slots to do this");
            return;
        }
        if(player.getInventory().remove(GIFT) > 0){
            player.getInventory().add(Item.create(fair ? 19469 : 19468));
            player.sendMessage("@red@Congratulations you have completed the event!");
            player.getPermExtraData().put(CHRISTMAS_EVENT_KEY, 3);
            for(final int i : SANTA_SUIT_IDS)
                player.getInventory().add(Item.create(i));
        }
    }
}
