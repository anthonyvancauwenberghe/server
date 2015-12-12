package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

public class CurrencyShop extends Shop {

    private final int currency;

    /**
     * @param id
     * @param name
     * @param container
     * @param currency
     * @param general
     */
    public CurrencyShop(final int id, final String name, final Container container, final int currency, final boolean general) {
        super(id, name, container, general);
        this.currency = currency;
    }


    @Override
    public void sellToShop(final Player player, final Item item) {
        if(player.needsNameChange() || player.doubleChar()){
            return;
        }
        if(currency == COINS_ID && player.hardMode() && !player.getDungeoneering().inDungeon()){
            player.sendMessage("You cannot sell items to currency shops in this mode");
            return;
        }
        final int payment = calculateUnitSellPrice(item) * item.getCount();
        player.getInventory().remove(item);
        player.getExpectedValues().sellToStore(item);
        getContainer().add(item);
        if(payment > 0){
            player.getInventory().add(new Item(currency, payment));
            player.getExpectedValues().addItemtoInventory("Selling to store", Item.create(currency, payment));
        }
        player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
        updatePlayers();
    }

    @Override
    public void buyFromShop(final Player player, final Item item) {
        if(player.needsNameChange() || player.doubleChar()){
            return;
        }
        final Item coins = player.getInventory().getById(currency);
        if(coins == null){
            player.getActionSender().sendMessage("You don't have enough " + ItemDefinition.forId(currency).getName().toLowerCase() + " to buy this item.");
            return;
        }
        if(currency == COINS_ID && player.hardMode() && !player.getDungeoneering().inDungeon()){
            player.sendMessage("You cannot buy from this shop in this mode");
            return;
        }
        final int price = calculateUnitBuyPrice(item) * item.getCount();
        if(coins.getCount() >= price){
            player.getInventory().remove(new Item(currency, price));
            player.getExpectedValues().removeItemFromInventory("Buying from store", Item.create(currency, price));
            this.getContainer().remove(item);
            player.getExpectedValues().buyFromStore(item);
            player.getInventory().add(item);
            player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
            updatePlayers();
        }else{
            player.getActionSender().sendMessage("You don't have enough " + ItemDefinition.forId(currency).getName().toLowerCase() + " to buy this item.");
        }

    }

    @Override
    public void valueBuyItem(final Player player, final Item item) {
        final int price = calculateUnitBuyPrice(item);
        final String message = "The shop will sell a '@dre@" + item.getDefinition().getProperName() + "@bla@' for " + price + " " + ItemDefinition.forId(currency).getName().toLowerCase() + ".";
        player.getActionSender().sendMessage(message);

    }

    @Override
    public void valueSellItem(final Player player, final Item item) {
        final int price = calculateUnitSellPrice(item);
        final String message = "The shop will buy a '@dre@" + item.getDefinition().getProperName() + "@bla@' for " + price + " " + ItemDefinition.forId(currency).getName().toLowerCase() + ".";
        player.getActionSender().sendMessage(message);
    }

    /**
     * The sell price per unit.
     *
     * @param item
     * @return
     */
    private int calculateUnitSellPrice(final Item item) {
        int price = -1;
        if(currency == Shop.COINS_ID){
            price = (int) (item.getDefinition().getHighAlcValue() * 0.9);
            if(this.isGeneral())
                price *= 0.9;
        }else{
            price = getSpecialPrice(item);
            price *= 0.5;
        }
        return Math.max(1, price);
    }

    private int calculateUnitBuyPrice(final Item item) {
        int price = -1;
        if(currency == Shop.COINS_ID){
            price = item.getDefinition().getHighAlcValue();
            if(this.isGeneral())
                price *= 0.9;
        }else{
            price = getSpecialPrice(item);
        }
        return price;
    }

    protected int getSpecialPrice(final Item item) {
        switch(item.getId()){
            case 6585:
                return 20000;
        }
        return 10000;
    }

    @Override
    public void process() {
        for(final Item item : getStaticItems()){
            if(item == null)
                continue;
            if(getContainer().contains(item.getId())){
                final Item shopItem = getContainer().getById(item.getId());
                if(shopItem.getCount() < item.getCount()){
                    getContainer().add(new Item(item.getId()));
                }
            }else{
                getContainer().add(new Item(item.getId()));
            }
        }
        for(final Item item : getContainer().toArray()){
            if(item == null)
                continue;
            if(!isStatic(item.getId()))
                getContainer().remove(new Item(item.getId()));
        }
        updatePlayers();
    }

}
