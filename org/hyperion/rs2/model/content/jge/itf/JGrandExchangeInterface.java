package org.hyperion.rs2.model.content.jge.itf;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.jge.entry.EntryBuilder;

/**
 * Created by Administrator on 9/24/2015.
 */
public final class JGrandExchangeInterface {

    public static final int ID = 23670;

    public static final int BACK = 22723;
    public static final int DECREASE_QUANTITY = 22713;
    public static final int INCREASE_QUANTITY = 22714;
    public static final int SET_QUANTITY_1 = 22686;
    public static final int SET_QUANTITY_10 = 22689;
    public static final int SET_QUANTITY_100 = 22692;
    public static final int SET_QUANTITY_500 = 22695;
    public static final int ENTER_QUANTITY = 22698;
    public static final int DECREASE_PRICE = 22715;
    public static final int INCREASE_PRICE = 22716;
    public static final int DECREASE_PRICE_PERCENT = 22701;
    public static final int EQUATE_PRICE = 22704;
    public static final int ENTER_PRICE = 22710;
    public static final int INCREASE_PRICE_PERCENT = 22707;
    public static final int CONFIRM = 22720;
    public static final int CANCEL = 22188;
    public static final int CLAIM_PROGRESS_SLOT = 22192;
    public static final int CLAIM_RETURN_SLOT = 22193;
    public static final int VIEW_BACK = 22187;

    public static final int ENTRY_TYPE_LABEL = 22672;
    public static final int ENTRY_UNIT_PRICE_LABEL = 22675;
    public static final int ITEM_NAME_LABEL = 22673;
    public static final int ITEM_DESC_LABEL = 22674;
    public static final int UNIT_PRICE_LABEL = 22677;
    public static final int TOTAL_PRICE_LABEL = 22683;
    public static final int ITEM_QUANTITY_LABEL = 22676;

    public static final int ENTRY_ITEM_BOX = 22717;

    public static final int SLOT_1_BUY = 23673;
    public static final int SLOT_1_SELL = SLOT_1_BUY + 3;
    public static final int SLOT_1_VIEW = 23715;

    public static final int SLOT_2_BUY = SLOT_1_BUY + 7;
    public static final int SLOT_2_SELL = SLOT_2_BUY + 3;
    public static final int SLOT_2_VIEW = SLOT_1_VIEW + 9;

    public static final int SLOT_3_BUY = SLOT_2_BUY + 7;
    public static final int SLOT_3_SELL = SLOT_3_BUY + 3;
    public static final int SLOT_3_VIEW = SLOT_2_VIEW + 9;

    public static final int SLOT_4_BUY = SLOT_3_BUY + 7;
    public static final int SLOT_4_SELL = SLOT_4_BUY + 3;
    public static final int SLOT_4_VIEW = SLOT_3_VIEW + 9;

    public static final int SLOT_5_BUY = SLOT_4_BUY + 7;
    public static final int SLOT_5_SELL = SLOT_5_BUY + 3;
    public static final int SLOT_5_VIEW = SLOT_4_VIEW + 9;

    public static final int SLOT_6_BUY = SLOT_5_BUY + 7;
    public static final int SLOT_6_SELL = SLOT_6_BUY + 3;
    public static final int SLOT_6_VIEW = SLOT_5_VIEW + 9;

    private JGrandExchangeInterface(){}

    public static void open(final Player player){
        player.getActionSender().showInterface(ID);
    }

    public static void setEntryType(final Player player, final Entry.Type type){
        player.getActionSender().sendString(ENTRY_TYPE_LABEL, type != null ? type.name : "");
    }

    public static void setEntryItem(final Player player, final Item item){
        player.getActionSender().sendUpdateItems(ENTRY_ITEM_BOX, new Item[]{item});
        player.getActionSender().sendString(ITEM_NAME_LABEL, item != null ? item.getDefinition().getName() : "");
        player.getActionSender().sendString(ITEM_DESC_LABEL, item != null ? item.getDefinition().getDescription() : "");
    }

    public static void showSellInventory(final Player player){
        player.getActionSender().sendUpdateItems(26571, player.getInventory().getItems());
        player.getActionSender().sendInterfaceInventory(22670, 26570);
    }

    public static void setItemUnitPrice(final Player player, final int unitPrice){
        final String formatted = unitPrice > 0 ? String.format("%,d PKT", unitPrice) : "";
        player.getActionSender().sendString(ENTRY_UNIT_PRICE_LABEL, formatted);
        player.getActionSender().sendString(UNIT_PRICE_LABEL, formatted);
    }

    public static void setItemQuantity(final Player player, final int quantity){
        final String formatted = quantity > 0 ? String.format("%,d", quantity) : "";
        player.getActionSender().sendString(ITEM_QUANTITY_LABEL, formatted);
    }

    public static void setTotalPrice(final Player player, final int total){
        final String formatted = total > 0 ? String.format("%,d PKT", total) : "";
        player.getActionSender().sendString(TOTAL_PRICE_LABEL, formatted);
    }

    public static void setItemQuantityAndTotalPrice(final Player player, final int quantity, final int total){
        setItemQuantity(player, quantity);
        setTotalPrice(player, total);
    }

    public static void setItemUnitPriceAndTotalPrice(final Player player, final int unitPrice, final int total){
        setItemUnitPrice(player, unitPrice);
        setTotalPrice(player, total);
    }

    public static void setEntry(final Player player, final Entry entry){
        setEntryType(player, entry != null ? entry.type : null);
        setEntryItem(player, entry != null ? entry.item() : null);
        setItemUnitPrice(player, entry != null ? entry.unitPrice : -1);
        setItemQuantity(player, entry != null ? entry.itemQuantity : -1);
        setTotalPrice(player, entry != null ? entry.totalPrice : -1);
    }

    public static void setNewEntry(final Player player, final EntryBuilder entry){
        setEntryType(player, entry != null ? entry.type() : null);
        setEntryItem(player, entry != null ? entry.item() : null);
        setItemUnitPrice(player, entry != null ? entry.unitPrice() : -1);
        setItemQuantity(player, entry != null ? entry.itemQuantity() : -1);
        setTotalPrice(player, entry != null ? entry.totalPrice() : -1);
    }
}
