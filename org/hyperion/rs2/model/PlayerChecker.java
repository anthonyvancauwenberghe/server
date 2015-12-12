package org.hyperion.rs2.model;

import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ContainerListener;

public class PlayerChecker {
    private ContainerListener inventoryListener;
    private ContainerListener bankListener;
    private Container inventoryContainer;
    private Container bankContainer;

    private PlayerChecker() {

    }

    /**
     * Create methods always help with arguments etc because eclipse auto-fills them
     * unlike constructors
     */

    public static PlayerChecker create() {
        return new PlayerChecker();
    }

    public synchronized ContainerListener getBankListener() {
        return bankListener;
    }

    public synchronized void setBankListener(final ContainerListener listener) {
        this.bankListener = listener;
    }

    public synchronized ContainerListener getInvListener() {
        return inventoryListener;
    }

    public synchronized void setInvListener(final ContainerListener listener) {
        this.inventoryListener = listener;
    }

    public synchronized Container getBank() {
        return bankContainer;
    }

    public synchronized void setBank(final Container bankContainer) {
        this.bankContainer = bankContainer;
    }

    public synchronized Container getInv() {
        return inventoryContainer;
    }

    public synchronized void setInv(final Container inventoryContainer) {
        this.inventoryContainer = inventoryContainer;
    }

}
