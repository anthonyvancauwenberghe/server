package org.hyperion.rs2.model.content.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.content.misc2.MysteryBox;
import org.hyperion.rs2.model.content.misc2.VoteRewardBox;
import org.hyperion.rs2.model.shops.DonatorShop;
import org.hyperion.rs2.sql.requests.QueryRequest;
import org.hyperion.rs2.util.AccountValue;

public class ContainerCleaner {

	public static final int DEFAULT_MAX_ITEM_AMOUNT = 10;

	private Container[] containers;

	private int donators_bought;

	private String name;

	public ContainerCleaner(Player player, Container... containers) {
		this.donators_bought = player.getPoints().getDonatorPointsBought();
		this.containers = containers;
		this.name = player.getName().toLowerCase();
	}

	public void cleanContainer(Container container) {
		if(container == null)
			return;
		for(Item item : container.toArray())
			cleanItem(item);
	}

	public void cleanItem(Item item) {
		if(item == null)
			return;
	    /*
         * Items that should not be cleaned.
		 */
		switch(item.getId()) {
			case MysteryBox.ID:
			case VoteRewardBox.ID:
				return;
		}
		/*
		 * Items that should not exist.
		 */
		switch(item.getId()) {
			case 1391:
			//case 12744: //max and comp capes?
			//case 12747:
				item.setId(1);
				return;
		}
		/*
		 * Overload Potions.
		 */
		if(item.getId() >= 15332 && item.getId() <= 15335) {
			if(item.getCount() > 500) {
				item.setCount(500);
			}
			return;
		}
		int amount = item.getCount();
		if(amount == 0)
			return;
		int unit_value = AccountValue.getItemValue(item) / amount;
		if(unit_value == 0)
			return;
		int max_amount = DEFAULT_MAX_ITEM_AMOUNT;
		if(unit_value < 10) {
			max_amount = DEFAULT_MAX_ITEM_AMOUNT * 30;
		}
		max_amount *= 3;
		if(donators_bought > 10000) {
			max_amount *= 6;
		} else if(donators_bought > 100) {
			max_amount *= 4;
		}
		if(amount > max_amount) {
			int deleted_amount = amount - max_amount;
			int deleted_value = deleted_amount * DonatorShop.getPrice(item.getId());
			if(deleted_value > 0) {
				String query = "INSERT INTO cleanings(username, item_id, item_count, deleted_value) VALUES('" + name + "'," + item.getId() + "," + deleted_amount + "," + deleted_value + ")";
				World.getWorld().getLogsConnection().offer(new QueryRequest(query));
			}
			item.setCount(max_amount);
		}
	}


	public static void writeDupeLog(String name, String line, int count) {
		int folder = 20;
		if(count >= 1000) {
			folder = 1000;
		} else if(count >= 100) {
			folder = 100;
		} else if(count >= 50) {
			folder = 50;
		}
		File f = new File("./logs/dupes/" + folder + "/" + name + ".txt");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write(line);
			out.newLine();
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
