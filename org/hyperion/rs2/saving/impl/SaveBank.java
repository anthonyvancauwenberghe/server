package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.saving.SaveContainer;

public class SaveBank extends SaveContainer {

    /**
     * Constructs a new SaveBank.
     *
     * @param name
     */
    public SaveBank(final String name) {
        super(name);
    }

    @Override
    public Item[] getContainer(final Player player) {
        return player.getBank().toArray();
    }

    @Override
    public void loadItem(final Player player, final Item item) {
        final BankItem bankItem = (BankItem) item;
        if(!player.getBank().add(new BankItem(bankItem.getTabIndex(), bankItem.getId(), bankItem.getCount()))){
            return;
        }
    }

}
