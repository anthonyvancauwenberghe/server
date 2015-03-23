package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.util.TextUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/14/15
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class LegendaryStoreHandler implements ContentTemplate {

    private static final int
        RIGOUR_SCROLL = 18839, AUGURY_SCROLL = 18344, WRATH_SCROLL = 18950;

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.NPC_OPTION1)
            return new int[]{2790};
        else if(type == ClickType.EAT)
            return new int[]{AUGURY_SCROLL, RIGOUR_SCROLL, WRATH_SCROLL};
        return new int[0];
    }

    @Override
    public boolean npcOptionOne(Player player, int npcId, int npcLocationX, int npcLocationY, int npcSlot) {
        ShopManager.open(player, 79);
        return true;
    }

    @Override
    public boolean itemOptionOne(Player player, int id, int slot, int interfaceId) {
        if(interfaceId == Inventory.INTERFACE) {
            if(player.getInventory().remove(slot, Item.create(id, 1)) == 1) {
                if(id == AUGURY_SCROLL) {
                    player.getPermExtraData().put("augury", true);
                } else if(id == RIGOUR_SCROLL) {
                    player.getPermExtraData().put("rigour", true);
                } else if(id == WRATH_SCROLL) {
                    player.getPermExtraData().put("wrath", true);
                }
                PlayerSaving.getSaving().save(player);
                player.sendf("You unlock the @blu@%s@bla@ prayer!", TextUtils.titleCase(ItemDefinition.forId(id).getName().replace("Scroll of ", "")));
            }
        }
        return true;
    }
}
