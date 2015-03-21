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

    private int[] tabAmounts = new int[9];
    private boolean loadError;
    private boolean isBanking;
    private String searchText = null;
    private boolean isWithdrawAsNote;
    private boolean isSearching = false;
    private boolean isInserting;
    private int tabIndex;
    private int loadTab;
    private final Player player;

    public BankField(Player player) {
        this.player = Objects.requireNonNull(player, "player");
    }


    public String getSearchText() {
        return searchText;
    }

    public int[] getTabAmounts() {
        return tabAmounts;
    }

    public void setLoadError(boolean loadError) {
        this.loadError = loadError;
    }

    public boolean isLoadError() {
        return loadError;
    }

    public int getLoadTab() {
        return loadTab;
    }

    public int getTabForSlot(int slot) {
        int offset = 0;
        for (int index = 0; index < getTabAmounts().length; index++) {
            if (slot >= offset && slot < offset + getTabAmounts()[index]) {
                return index;
            } else if (getTabAmounts()[index] > 0) {
                offset += getTabAmounts()[index];
            }
        }
        return 0;
    }

    public int getUsedTabs() {
        int tabs = 0;
        for (int amount : getTabAmounts()) {
            if (amount > 0) {
                tabs++;
            }
        }
        return tabs;
    }

    public int getOffset(int tab) {
        int offset = 0;
        for (int index = 0; index < getTabAmounts().length; index++) {
            if (index == tab) {
                break;
            } else if (getTabAmounts()[index] > 0) {
                offset += getTabAmounts()[index];
            }
        }
        return offset;
    }

    public boolean isBanking() {
        return isBanking;
    }

    public boolean isWithdrawAsNote() {
        return isWithdrawAsNote;
    }

    public boolean isSearching() {
        return isSearching;
    }

    public boolean isInserting() {
        return isInserting;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setBanking(boolean isBanking) {
        this.isBanking = isBanking;
        if (!isBanking) {
            setSearching(false);
        }
    }

    public void setWithdrawAsNote(boolean isWithdrawAsNote) {
        this.isWithdrawAsNote = isWithdrawAsNote;
    }

    public void setSearching(boolean isSearching) {
        this.isSearching = isSearching;
        if (!isSearching) {
            player.getActionSender().sendClientConfig(1010, 0);
            PacketBuilder bldr2 = new PacketBuilder(26);
            setSearchText(null);
            player.getBank().fireItemsChanged();
        } else {
            player.getActionSender().sendClientConfig(1010, 1);
        }
    }

    public void setInserting(boolean isInserting) {
        this.isInserting = isInserting;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void setLoadTab(int loadTab) {
        this.loadTab = loadTab;
    }

}