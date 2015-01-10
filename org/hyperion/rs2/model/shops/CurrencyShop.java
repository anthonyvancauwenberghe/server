package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

public class CurrencyShop extends Shop {

	private int currency;

	/**
	 * @param id
	 * @param name
	 * @param container
	 * @param currency
	 * @param general
	 */
	public CurrencyShop(int id, String name, Container container, int currency,
	                    boolean general) {
		super(id, name, container, general);
		this.currency = currency;
	}


	@Override
	public void sellToShop(Player player, Item item) {
        if(player.hardMode()) {
            player.sendMessage("You cannot sell items to currency shops in this mode");
            return;
        }
		int payment = calculateUnitSellPrice(item) * item.getCount();
		player.getInventory().remove(item);
		getContainer().add(item);
		payment *= 0.9; // Cause Shops wanna scam u!
		if(payment > 0)
			player.getInventory().add(new Item(currency, payment));
		player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
		updatePlayers();
	}

	@Override
	public void buyFromShop(Player player, Item item) {
		Item coins = player.getInventory().getById(currency);
		if(coins == null) {
			player.getActionSender().sendMessage(
					"You don't have enough "
							+ ItemDefinition.forId(currency).getName()
							+ " to buy this item.");
			return;
		}
        if(player.hardMode()) {
            player.sendMessage("You cannot buy from this shop in this mode");
            return;
        }
		int price = calculateUnitBuyPrice(item) * item.getCount();
		if(coins.getCount() >= price) {
			player.getInventory().remove(new Item(currency, price));
			this.getContainer().remove(item);
			player.getInventory().add(item);
			player.getActionSender().sendUpdateItems(3823,
					player.getInventory().toArray());
			updatePlayers();
		} else {
			player.getActionSender().sendMessage(
					"You don't have enough "
							+ ItemDefinition.forId(currency).getName()
							+ " to buy this item.");
		}

	}

	@Override
	public void valueBuyItem(Player player, Item item) {
		int price = calculateUnitBuyPrice(item);
		String message = "The shop will sell a "
				+ item.getDefinition().getProperName() + " for " + price + " "
				+ ItemDefinition.forId(currency).getName() + ".";
		player.getActionSender().sendMessage(message);

	}

	@Override
	public void valueSellItem(Player player, Item item) {
		int price = calculateUnitSellPrice(item);
		String message = "The shop will buy a "
				+ item.getDefinition().getProperName() + " for " + price + " "
				+ ItemDefinition.forId(currency).getName() + ".";
		player.getActionSender().sendMessage(message);
	}

	/**
	 * The sell price per unit.
	 *
	 * @param item
	 * @return
	 */
	private int calculateUnitSellPrice(Item item) {
		int price = - 1;
		if(currency == Shop.COINS_ID) {
			price = (int) (item.getDefinition().getHighAlcValue() * 0.9);
			if(this.isGeneral())
				price *= 0.9;
		} else {
			price = getSpecialPrice(item);
		}
		return Math.max(1, price);
	}

	private int calculateUnitBuyPrice(Item item) {
		int price = - 1;
		if(currency == Shop.COINS_ID) {
			price = item.getDefinition().getHighAlcValue();
			if(this.isGeneral())
				price *= 0.9;
		} else {
			price = getSpecialPrice(item);
			price *= 0.9;
		}
		return price;
	}

	private int getSpecialPrice(Item item) {
		switch(item.getId()) {
			case 6585:
				return 20000;
		}
		return 10000;
	}

	@Override
	public void process() {
		for(Item item : getStaticItems()) {
			if(item == null)
				continue;
			if(getContainer().contains(item.getId())) {
				Item shopItem = getContainer().getById(item.getId());
				if(shopItem.getCount() < item.getCount()) {
					getContainer().add(new Item(item.getId()));
				}
			} else {
				getContainer().add(new Item(item.getId()));
			}
		}
		for(Item item : getContainer().toArray()) {
			if(item == null)
				continue;
			if(! isStatic(item.getId()))
				getContainer().remove(new Item(item.getId()));
		}
		updatePlayers();
	}

}
