package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.rs2.GenericWorldLoader;
import org.hyperion.rs2.model.container.BoB;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ContainerListener;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.grandexchange.GrandExchange;
import org.hyperion.rs2.model.content.grandexchange.GrandExchangeV2;
import org.hyperion.rs2.model.content.jge.itf.JGrandExchangeInterface;
import org.hyperion.rs2.model.content.misc2.RunePouch;
import org.hyperion.rs2.saving.MergedSaving;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.Misc;

import java.util.ArrayList;
import java.util.List;
//updatedderpusherpus

/**
 * Contains information about the state of interfaces open in the client.
 *
 * @author Graham Edgecombe
 */
public class InterfaceState {

    /**
     * A list of container listeners used on interfaces that have containers.
     */
    private final List<ContainerListener> containerListeners = new ArrayList<ContainerListener>();
    private final int[] nextDialogueId = new int[5];
    public String string_input_listener = "";
    /**
     * The current open interface.
     */
    private int currentInterface = -1;
    /**
     * The active enter amount interface.
     */
    private int enterAmountInterfaceId = -1;
    /**
     * The active enter amount id.
     */
    private int enterAmountId;
    /**
     * The active enter amount slot.
     */
    private int enterAmountSlot;
    /**
     * The player.
     */
    private Player player;
    private int openDialogueId;

    /**
     * Creates the interface state.
     */
    public InterfaceState(final Player player) {
        this.player = player;
    }

    /**
     * Checks if the specified interface is open.
     *
     * @param id The interface id.
     * @return <code>true</code> if the interface is open, <code>false</code> if not.
     */
    public boolean isInterfaceOpen(final int id) {
        return currentInterface == id;
    }

    /**
     * Gets the current open interface.
     *
     * @return The current open interface.
     */
    public int getCurrentInterface() {
        return currentInterface;
    }

    /**
     * Called when an interface is opened.
     *
     * @param id The interface.
     */
    public void interfaceOpened(final int id) {
        /*if(currentInterface != -1) {
            interfaceClosed();
		}*/
        currentInterface = id;
    }

    /**
     * Called when an interface is closed.
     */
    public void interfaceClosed() {
        currentInterface = -1;
        enterAmountInterfaceId = -1;
        Trade.declineTrade(player);
        Duel.declineTrade(player);
        for(final ContainerListener c : containerListeners){
            player.getInventory().removeListener(c);
            player.getEquipment().removeListener(c);
            player.getBank().removeListener(c);
            player.getTrade().removeListener(c);
            player.getDuel().removeListener(c);
        }
    }

    public void resetContainers() {
        containerListeners.clear();
        player.getInventory().removeAllListeners();
        player.getEquipment().removeAllListeners();
        player.getBank().removeAllListeners();
        player.getTrade().removeAllListeners();
        player.getDuel().removeAllListeners();
    }

    public void resetInterfaces() {
        /*if(player.tutIsland != 10){
			TutorialQuest.walkDialogue(player);
		}*/
		/*else*/
        if(getCurrentInterface() > 10 || player.closeChatInterface || getOpenDialogueId() >= 0){
            setOpenDialogueId(0);
            interfaceClosed();
            ContentEntity.removeAllWindows(player);
            player.closeChatInterface = false;
        }
    }

    /**
     * Adds a listener to an interface that is closed when the inventory is closed.
     *
     * @param container         The container.
     * @param containerListener The listener.
     */
    public void addListener(final Container container, final ContainerListener containerListener) {
        container.addListener(containerListener);
        containerListeners.add(containerListener);
    }

    /**
     * Called to open the enter amount interface.
     *
     * @param interfaceId The interface id.
     * @param slot        The slot.
     * @param id          The id.
     */
    public void openEnterAmountInterface(final int interfaceId, final int slot, final int id) {
        enterAmountInterfaceId = interfaceId;
        enterAmountSlot = slot;
        enterAmountId = id;
        player.getActionSender().sendEnterAmountInterface();
    }

    public void openEnterAmountInterface(final int interfaceId, final int id) {
        enterAmountInterfaceId = interfaceId;
        enterAmountSlot = player.getBank().getSlotById(id);
        enterAmountId = id;
        player.getActionSender().sendEnterAmountInterface();
    }

    /**
     * Checks if the enter amount interface is open.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean isEnterAmountInterfaceOpen() {
        return enterAmountInterfaceId != -1;
    }

    /**
     * Called when the enter amount interface is closed.
     *
     * @param amount The amount that was entered.
     */
    public void closeEnterAmountInterface(final int amount) {
        try{
            if(amount <= 0)
                return;
            switch(enterAmountInterfaceId){
                case GrandExchange.GEInterfaceId:
                    //World.getWorld().getGrandExchange().addItem(player, enterAmountId, ((Integer) player.getExtraData().get("geamount")), enterAmountSlot, amount);
                    break;
                case BoB.PLAYER_INVENTORY_INTERFACE:
                    BoB.deposit(player, enterAmountSlot, enterAmountId, amount);
                    break;
                case RunePouch.INVENTORY_INTERFACE:
                    if(player.openedBoB)
                        BoB.deposit(player, enterAmountSlot, enterAmountId, amount);
                    else if(enterAmountSlot >= 0 && enterAmountSlot < Inventory.SIZE){
                        RunePouch.deposit(player, enterAmountSlot, enterAmountId, amount);
                    }
                    break;
                case RunePouch.RUNE_INTERFACE:
                    if(player.openedBoB)
                        BoB.withdraw(player, enterAmountSlot, enterAmountId, amount);
                    else if(enterAmountSlot >= 0 && enterAmountSlot < RunePouch.SIZE)
                        RunePouch.withdraw(player, enterAmountId, amount);
                    break;
                case Bank.PLAYER_INVENTORY_INTERFACE:
                    if(player.openedBoB)
                        BoB.deposit(player, enterAmountSlot, enterAmountId, amount);
                    else if(enterAmountSlot >= 0 && enterAmountSlot < Inventory.SIZE){
                        Bank.deposit(player, enterAmountSlot, enterAmountId, amount, true);
                    }
                    break;
                case Bank.BANK_INVENTORY_INTERFACE:
                case Bank.BANK_INVENTORY_INTERFACE + 1:
                case Bank.BANK_INVENTORY_INTERFACE + 2:
                case Bank.BANK_INVENTORY_INTERFACE + 3:
                case Bank.BANK_INVENTORY_INTERFACE + 4:
                case Bank.BANK_INVENTORY_INTERFACE + 5:
                case Bank.BANK_INVENTORY_INTERFACE + 6:
                case Bank.BANK_INVENTORY_INTERFACE + 7:
                case Bank.BANK_INVENTORY_INTERFACE + 8:
                    if(player.openedBoB)
                        BoB.withdraw(player, enterAmountSlot, enterAmountId, amount);
                    else if(enterAmountSlot >= 0 && enterAmountSlot < Bank.SIZE)
                        Bank.withdraw(player, enterAmountId, amount);
                    break;
                case BoB.BOB_INVENTORY_INTERFACE:
                    if(enterAmountSlot >= 0 && enterAmountSlot < BoB.SIZE){
                        BoB.withdraw(player, enterAmountSlot, enterAmountId, amount);
                    }
                    break;
                case Trade.PLAYER_INVENTORY_INTERFACE:
                    if(player.currentInterfaceStatus == 1){
                        if(enterAmountSlot >= 0 && enterAmountSlot < Inventory.SIZE){
                            Trade.deposit(player, enterAmountSlot, enterAmountId, amount);
                        }
                    }else{
                        if(player.currentInterfaceStatus == 2){
                            if(enterAmountSlot >= 0 && enterAmountSlot < Inventory.SIZE){
                                Duel.deposit(player, enterAmountSlot, enterAmountId, amount);
                            }
                        }
                    }//arsen is tard cause this wont update OJ
                    break;
                case Trade.TRADE_INVENTORY_INTERFACE:
                    Trade.withdraw(player, enterAmountSlot, enterAmountId, amount);
                    break;
                case Duel.DUEL_INVENTORY_INTERFACE:
                    Duel.withdraw(player, enterAmountSlot, enterAmountId, amount);
                    break;
                case Bank.DEPOSIT_INVENTORY_INTERFACE:
                    Bank.deposit(player, enterAmountSlot, enterAmountId, amount, true);
                    break;
                case 28000://GEIntergace
                    GrandExchangeV2.buyItem(player, enterAmountId, enterAmountSlot, amount);
                    break;
                case 29000://GEIntergace
                    if(enterAmountId == 0)
                        GrandExchangeV2.setAmount(player, amount);
                    else if(enterAmountId == 1)
                        GrandExchangeV2.setPrice(player, amount);
                    break;
                case ShopManager.SHOP_INVENTORY_INTERFACE:
                    if(enterAmountSlot >= 0 && player.getShopId() == -2){
                        //World.getWorld().getGrandExchange().buyItem(player, enterAmountId, amount, player.geItem[enterAmountSlot].getName(),enterAmountSlot);
                    }else if(enterAmountSlot >= 0 && enterAmountSlot < ShopManager.SIZE){
                        ShopManager.buyItem(player, enterAmountId, enterAmountSlot, amount);
                    }
                    break;
                case ShopManager.PLAYER_INVENTORY_INTERFACE:
                    if(enterAmountSlot >= 0 && enterAmountSlot < ShopManager.SIZE){
                        ShopManager.sellItem(player, enterAmountId, enterAmountSlot, amount);
                    }
                    break;
            }
        }finally{
            enterAmountInterfaceId = -1;
        }
    }

    public int getOpenDialogueId() {
        return openDialogueId;
    }

    /**
     * @param openDialogueId the openDialogueId to set
     */
    public void setOpenDialogueId(final int openDialogueId) {
        this.openDialogueId = openDialogueId;
    }

    /**
     * @return the nextDialogueId
     */
    public int getNextDialogueId(final int index) {
        return nextDialogueId[index];
    }

    /**
     * @param nextDialogueId the nextDialogueId to set
     */
    public void setNextDialogueId(final int index, final int nextDialogueId) {
        this.nextDialogueId[index] = nextDialogueId;
    }

    public void destroy() {
        player = null;
    }

    public void setStringListener(final String listener) {
        this.string_input_listener = listener;
        player.getActionSender().sendEnterStringInterface();
    }

    public boolean receiveStringListener(String result) {
        if(string_input_listener == null || string_input_listener.length() == 0){
            return false;
        }
        result = result.replaceAll("_", " ");
        final String finalResult = result;
        switch(string_input_listener){
            case "doublecharinstant":
                if(player.doubleChar()){
                    boolean allowed = true;
                    if(MergedSaving.exists(result)){
                        allowed = false;
                    }
                    if(!NameUtils.isValidName(result)){
                        allowed = false;
                    }
                    if(allowed){
                        final boolean success = MergedSaving.renameInstant(player.getName(), result);
                        if(success){
                            System.out.println("Successfully renamed instant file");
                            if(Server.getConfig().getBoolean("logssql"))
                                World.getWorld().getLogsConnection().offer("INSERT INTO mergelogs(username, source, message) VALUES('" + player.getName() + "', " + player.getSource() + ",'renamed instant file to: " + result + "')");
                            player.getSession().close(false);
                        }else{
                            System.out.println("Failed to rename instant file");
                        }
                    }else{
                        DialogueManager.openDialogue(player, 513);
                    }
                }
                return true;
            case "doublecharartero":
                if(player.doubleChar()){
                    boolean allowed = true;
                    if(MergedSaving.exists(result)){
                        allowed = false;
                    }
                    if(!NameUtils.isValidName(result)){
                        allowed = false;
                    }
                    if(allowed){

                        final boolean success = MergedSaving.renameArtero(player.getName(), result);
                        if(success){
                            System.out.println("Succesfully renamed artero file");
                            if(Server.getConfig().getBoolean("logssql"))
                                World.getWorld().getLogsConnection().offer("INSERT INTO mergelogs(username, source, message) VALUES('" + player.getName() + "', " + player.getSource() + ",'renamed artero file to: " + result + "')");
                            player.getSession().close(false);
                        }else{
                            System.out.println("Failed to rename artero file");
                        }

                    }else{
                        DialogueManager.openDialogue(player, 514);
                    }
                }
                return true;
            case "namechange":
                if(player.needsNameChange()){
                    boolean allowed = true;
                    if(MergedSaving.exists(result)){
                        allowed = false;
                    }
                    if(!NameUtils.isValidName(result)){
                        allowed = false;
                    }
                    if(allowed){
                        final String rename = player.getExtraData().getString("rename");
                        if(rename != null){
                            if(rename.equalsIgnoreCase(result)){
                                boolean success = false;
                                if(player.getSource() == GenericWorldLoader.ARTERO){
                                    success = MergedSaving.cleanArteroFile(player.getName());
                                }else if(player.getSource() == GenericWorldLoader.INSTANT){
                                    success = MergedSaving.cleanInstantFile(player.getName());
                                }
                                final String initialName = player.getName();
                                if(success){
                                    player.setName(result);
                                    player.display = result;
                                    player.getActionSender().sendMessage("@blu@You've changed your name to: " + result);
                                    player.setNeedsNameChange(false);
                                    MergedSaving.save(player);
                                    if(Server.getConfig().getBoolean("logssql"))
                                        World.getWorld().getLogsConnection().offer("INSERT INTO mergelogs(username, source, message) VALUES('" + initialName + "', " + player.getSource() + ",'changed name to: " + result + "')");
                                }else{
                                    if(Server.getConfig().getBoolean("logssql"))
                                        World.getWorld().getLogsConnection().offer("INSERT INTO mergelogs(username, source, message) VALUES('" + initialName + "', " + player.getSource() + ",'failed change name to: " + result + "')");
                                }
                                player.getSession().close(false);
                            }else{
                                DialogueManager.openDialogue(player, 406);
                            }
                        }else{
                            player.getExtraData().put("rename", result);
                            DialogueManager.openDialogue(player, 405);
                        }
                    }else{
                        DialogueManager.openDialogue(player, 403);
                    }
                }

                return true;
            case "ge_set_quantity":
                player.getGrandExchangeTracker().ifNewEntry(e -> {
                    try{
                        final int quantity = Misc.expandNumber(finalResult.replace(' ', '.'));
                        if(quantity < 1){
                            player.sendf("Invalid quantity");
                            return;
                        }
                        if(e.itemQuantity(quantity))
                            JGrandExchangeInterface.NewEntry.setQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice(), e.currency());
                    }catch(final Exception ex){
                        ex.printStackTrace();
                        player.sendf("Invalid quantity");
                    }
                }, "You are not building a new entry right now");
                return true;
            case "ge_set_price":
                player.getGrandExchangeTracker().ifNewEntry(e -> {
                    try{
                        final int unitPrice = Misc.expandNumber(finalResult.replace(' ', '.'));
                        if(unitPrice < 1){
                            player.sendf("Invalid price");
                            return;
                        }
                        if(e.unitPrice(unitPrice))
                            JGrandExchangeInterface.NewEntry.setUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice(), e.currency());
                    }catch(final Exception ex){
                        player.sendf("Invalid price");
                    }
                }, "You are not building a new entry right now");
                return true;
            default:
                return false;
        }
    }

}
