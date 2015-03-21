package org.hyperion.rs2.util;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.shops.DonatorShop;

/**
 * @author Arsen Maxyutov.
 */
public class AccountValue {

	private Player player;

	public AccountValue(Player player) {
		this.player = player;
	}

	/**
	 * Gets the total account value in donator points.
	 *
	 * @return
	 */
	public int getTotalValue() {
		int counter = 0;
		counter += getInventoryValue();
		counter += getEquipmentValue();
		counter += getBankValue();
		counter += getBobValue();
		counter += getTradeValue();
		counter += getDuelValue();
		counter += player.getPoints().getDonatorPoints();
		counter += player.getPoints().getVotingPoints();
		return counter;
	}

	public int getInventoryValue() {
		return getContainerValue(player.getInventory());
	}

	public int getEquipmentValue() {
		return getContainerValue(player.getEquipment());
	}

	public int getBankValue() {
		return getContainerValue(player.getBank());
	}

	public int getBobValue() {
		return getContainerValue(player.getBoB());
	}

	public int getTradeValue() {
		return getContainerValue(player.getTrade());
	}

	public int getDuelValue() {
		return getContainerValue(player.getDuel());
	}

	public static int getContainerValue(Container container) {
		int counter = 0;
		if(container == null)
			return counter;
        for (Item item : container.toArray()) {
            counter += getItemValue(item);
        }
		return counter;
	}
	/**
	 * Gets the account value of the item, not forgetting about the item amount/items being noted.
	 *
	 * @param item
	 * @return
	 */
	public static int getItemValue(Item item) {
		if(item == null)
			return 0;
		return DonatorShop.getPrice(item.getId()) * item.getCount();
	}

	static {
		CommandHandler.submit(new Command("accountvalue", Rank.PLAYER) {

			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage("Acc value: " + player.getAccountValue().getTotalValue());
				return true;
			}

		});
	}
}
