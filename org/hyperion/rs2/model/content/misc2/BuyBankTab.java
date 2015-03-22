package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerPoints;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.net.ActionSender;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 3/22/15
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuyBankTab implements ContentTemplate {

    private enum TabPrice {
        TAB_3(100),
        TAB_4(1000),
        TAB_5(5000),
        TAB_6(10000, 100),
        TAB_7(50000, 500),
        TAB_8(100000, 1000),
        TAB_9(500000, 5000);

        public final int dp, pkp;

        private TabPrice(int pkt) {
            this(pkt, 0);
        }

        private TabPrice(int pkp, int dp) {
            this.dp = dp;
            this.pkp = pkp;
        }
    }

    @Override
    public int[] getValues(int type) {
        if (type == ClickType.DIALOGUE_MANAGER)
            return new int[]{6500, 6501, 6502};
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean dialogueAction(Player player, int dialogueId) {
        final TabPrice price = TabPrice.values()[player.getBankField().getTabAmount() - 2];
        switch(dialogueId) {
            case 6500:
                player.getActionSender().sendDialogue("Banker", ActionSender.DialogueType.NPC, 449, Animation.FacialAnimation.HAPPY,
                        "This bank tab is currently unavailable for you", "You can purchase it for "+price.dp+" dp and "+price.pkp+ " pk points!");
                player.getInterfaceState().setNextDialogueId(0, 6501);
                return true;
            case 6501:
                player.getActionSender().sendDialogue("Purchase", ActionSender.DialogueType.OPTION, 449, Animation.FacialAnimation.DEFAULT,
                        "Yes! I would like to purchase it", "Nevermind...");
                player.getInterfaceState().setNextDialogueId(0, 6502);
                player.getInterfaceState().setNextDialogueId(1, -1);
                return true;
            case 6502:
                final PlayerPoints points = player.getPoints();
                if(points.getPkPoints() < price.pkp || points.getDonatorPoints() < price.dp) {
                    player.sendMessage("You don't have enough points to purchase this");
                    break;
                }
                points.setPkPoints(points.getPkPoints() - price.pkp);
                points.setDonatorPoints(points.getDonatorPoints() - price.dp);
                player.getBankField().setTabAmount(player.getBankField().getTabAmount() + 1);
                break;
        }
        return false;
    }
}
