package org.hyperion.rs2.model.shops;

import org.hyperion.Server;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

/**
 * @author Arsen Maxyutov.
 */
public class PkShop extends PointsShop {

	public PkShop(int id, String name, Container container) {
		super(id, name, container);
	}

	@Override
	public void valueSellItem(Player player, Item item) {
		int price = (int) Math.round(getPrice(item.getId()) * .55);
		if(item.getId() == 5020) {
			price = 10;
		}
		if(price <= 0) {
			player.getActionSender().sendMessage("Cannot buy this item! Please contact a staff member about this issue!");
			return;
		}

        if(item.getId() == LEGENDARY_TICKET) {
            player.sendMessage("You cannot sell this back to the shop");
            return;
        }

		String message = "The shop will buy a "
				+ item.getDefinition().getProperName() + " for " + price + " pk points.";
		if(price == 1) {
			message = message.replace("points", "point");
		}

		player.getActionSender().sendMessage(message);
	}

	@Override
	public void sellToShop(Player player, Item item) {
        if(item.getId() == LEGENDARY_TICKET) {
            player.sendMessage("You cannot sell this back to the shop");
            return;
        }
		int payment = this.getPrice(item.getId());
		player.getInventory().remove(item);
		getContainer().add(item);
		payment = (int) Math.round(payment * .55); // Cause Shops wanna scam u!
		if(item.getId() == 5020) {
			payment = 10;
		}
		payment = payment * item.getCount();
		if(payment > 0)
			player.getPoints().setPkPoints(player.getPoints().getPkPoints() + payment);
		player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
		updatePlayers();
	}

	@Override
	public int getPrice(int itemId) {
		if(itemId >= 8845 && itemId <= 8850) {
			return (itemId - 8844) * 100;
		}
		switch(itemId) {
            case 13663:
                return 80_000;
			case 15486:
				return 50;
			case 15272:
				return 1;
			case 6570:
			case 8842:
				return 500;
			case 8839:
			case 8840:
			case 10547:
			case 10548:
			case 10549:
			case 10550:
				return 750;
			case 11663:
			case 11664:
			case 11665:
			case 10551:
				return 1000;
			case 18333:
			case 18335:
				return 1500;
			case 15243:
				return 1;
			case 13902:
			case 19111:
			case 13899:
				return 5000;
			case 13887:
			case 13893:
			case 13884:
			case 13890:
			case 13896:
				return 2500;
			case 5020:
			case 13879:
			case 13883:
				return 10;

		}
		return 5000;
	}


	@Override
	public String getPointsName() {
		return Server.NAME + " Points";
	}


	@Override
	protected int getPointsAmount(Player player) {
		return player.getPoints().getPkPoints();
	}


	@Override
	protected void setPointsAmount(Player player, int value) {
		player.getPoints().setPkPoints(value);
	}


}
