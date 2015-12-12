package org.hyperion.rs2.model.container.bank;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.PacketBuilder;

import java.util.Objects;

/**
 * Holds all the getters and setters for the variables used in the player's {@linkplain Bank}.
 *
 * @author Michael | Chex
 */
public class BankField {

    private final Player player;
    private int tabAmount = 2;
    private boolean loadError;
    private boolean isBanking;
    private String searchText = null;
    private boolean isWithdrawAsNote;
    private boolean isSearching = false;
    private boolean isInserting;
    private int tabIndex;
    private int loadTab;

    public BankField(final Player player) {
        this.player = Objects.requireNonNull(player, "player");
    }


    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(final String searchText) {
        this.searchText = searchText;
    }

    public int[] getTabAmounts() {
        final int[] sizes = new int[tabAmount];
        for(int i = 0; i < player.getBank().capacity(); i++){
            final BankItem item = (BankItem) player.getBank().get(i);
            if(item != null){
                if(item.getTabIndex() >= tabAmount){
                    item.setTabSlot(0);
                    player.getBank().remove(item);
                    player.getBank().add(item);
                    System.err.println("BANK TAB OVERFLOW SIZE FOR " + player.getName() + " BY ITEM: " + item.getDefinition().getName());
                }
                sizes[item.getTabIndex()]++;
            }

        }
        return sizes.clone();
    }

    public boolean isLoadError() {
        return loadError;
    }

    public void setLoadError(final boolean loadError) {
        this.loadError = loadError;
    }

    public int getLoadTab() {
        return loadTab;
    }

    public void setLoadTab(final int loadTab) {
        this.loadTab = loadTab;
    }

    public int getTabForSlot(final int slot) {
        int offset = 0;
        final int[] sizes = getTabAmounts();
        for(int index = 0; index < sizes.length; index++){
            if(slot >= offset && slot < offset + sizes[index]){
                return index;
            }else if(getTabAmounts()[index] > 0){
                offset += sizes[index];
            }
        }
        return 0;
    }

    public BankItem[] itemsForTab(final int tab) {
        final int itemSlot = player.getBankField().getOffset(tab);
        final int initialTabAmount = player.getBankField().getTabAmounts()[tab];
        final BankItem[] items = new BankItem[initialTabAmount];

        for(int i = itemSlot; i < initialTabAmount + itemSlot; i++){
            items[i - itemSlot] = (BankItem) player.getBank().get(i);
        }
        return items;
    }

    public int getUsedTabs() {
        int tabs = 0;
        for(final int amount : getTabAmounts()){
            if(amount > 0){
                tabs++;
            }
        }
        return tabs;
    }

    public int getOffset(final int tab) {
        int offset = 0;
        for(int index = 0; index < getTabAmounts().length; index++){
            if(index == tab){
                break;
            }else if(getTabAmounts()[index] > 0){
                offset += getTabAmounts()[index];
            }
        }
        return offset;
    }

    public boolean isBanking() {
        return isBanking;
    }

    public void setBanking(final boolean isBanking) {
        this.isBanking = isBanking;
        if(!isBanking){
            setSearching(false);
        }
    }

    public boolean isWithdrawAsNote() {
        return isWithdrawAsNote;
    }

    public void setWithdrawAsNote(final boolean isWithdrawAsNote) {
        this.isWithdrawAsNote = isWithdrawAsNote;
    }

    public boolean isSearching() {
        return isSearching;
    }

    public void setSearching(final boolean isSearching) {
        this.isSearching = isSearching;
        if(!isSearching){
            player.getActionSender().sendClientConfig(1010, 0);
            final PacketBuilder bldr2 = new PacketBuilder(26);
            setSearchText(null);
            player.getBank().fireItemsChanged();
        }else{
            player.getActionSender().sendClientConfig(1010, 1);
        }
    }

    public boolean isInserting() {
        return isInserting;
    }

    public void setInserting(final boolean isInserting) {
        this.isInserting = isInserting;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(final int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int getTabAmount() {
        return tabAmount;
    }

    public void setTabAmount(final int tabAmount) {
        this.tabAmount = tabAmount;
    }


}
