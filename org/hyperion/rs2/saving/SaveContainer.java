package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.SpiritShields;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.bank.BankItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class SaveContainer extends SaveObject {

    /**
     * The default item amount.
     */
    public static final int DEFAULT_ITEM_AMOUNT = 1;

    /**
     * The default item id.
     */
    public static final int DEFAULT_ITEM_ID = 1;

    /**
     * Constructs a new SaveContainer.
     *
     * @param name
     */
    public SaveContainer(final String name) {
        super(name);
    }

    public static BankItem lineToBankItem(final String line, int slot) {
        final String[] parts = line.split(" ");
        final int id = Integer.parseInt(parts[0]);
        final int amount = Integer.parseInt(parts[1]);
        int tab = 0;
        if(parts.length > 2){
            tab = Integer.parseInt(parts[2]);
            slot = Integer.parseInt(parts[3]);
        }
        return new BankItem(tab, id, amount);
    }

    public static Item lineToItem(final String line) {
        final String[] parts = line.split(" ");
        final int id = Integer.parseInt(parts[0]);
        int amount = DEFAULT_ITEM_AMOUNT;
        if(parts.length > 1){
            amount = Integer.parseInt(parts[1]);
        }
        boolean duped = false;
        if(id >= 1038 && id <= 1058)
            duped = true;
        switch(id){
            case 14484:
            case 19713:
            case 19714:
            case 19715:
            case 16909:
            case SpiritShields.DIVINE_SPIRIT_SHIELD_ID:
                duped = true;
        }
        if(duped && amount > 20)
            amount = 20;
        return new Item(id, amount);
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        writer.write(getName());
        writer.newLine();
        for(final Item item : getContainer(player)){
            if(item == null)
                continue;
            writer.write(item.getId() + " " + item.getCount());
            writer.newLine();
        }
        return true;
    }

    public boolean saveBank(final Player player, final BufferedWriter writer) throws IOException {
        writer.write(getName());
        writer.newLine();
        for(final BankItem bankItem : getBankContainer(player)){
            if(bankItem == null)
                continue;
            writer.write(bankItem.getId() + " " + bankItem.getCount() + " " + bankItem.getTabIndex() + " " + player.getBank().getSlotById(bankItem.getId()));
            writer.newLine();
        }
        return true;
    }

    public void load(final Player player, final String values, final BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()).length() > 0){
            final Item nextItem = lineToItem(line);
            loadItem(player, nextItem);
        }
    }

    public void load(final Player player, final String values, final BufferedReader reader, final boolean debug) throws IOException {
        String line;
        while((line = reader.readLine()).length() > 0){
            final Item nextItem = lineToItem(line);
            loadItem(player, nextItem);
        }
    }

    public void loadBank(final Player player, final String values, final BufferedReader reader) throws IOException {
        String line;
        int slot = 0;
        while((line = reader.readLine()).length() > 0){
            final BankItem nextItem = lineToBankItem(line, slot);
            loadItem(player, nextItem);
            slot++;
        }
    }

    public BankItem[] getBankContainer(final Player player) {
        final BankItem[] items = new BankItem[Bank.SIZE];
        for(int i = 0; i < player.getBank().size(); i++){
            items[i] = (BankItem) player.getBank().get(i);
        }
        return items;
    }

    ;

    public abstract Item[] getContainer(Player player);

    public abstract void loadItem(Player player, Item item);
}
