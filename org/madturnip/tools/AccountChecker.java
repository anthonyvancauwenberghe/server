package org.madturnip.tools;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.model.DeathDrops;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.ItemsTradeable;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.skill.Farming;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class AccountChecker {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
        ItemDefinition.init();
        final File file = new File("./data/AlchPrices.txt");
        file.createNewFile();
        try(final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for(int i = 0; i < ItemDefinition.MAX_ID; i++) {
                if(ItemDefinition.forId(i) == null)
                    continue;
                writer.write(i + " | " + ItemDefinition.forId(i).getName() + " - "+DeathDrops.calculateAlchValue(0,i));
                writer.newLine();
            }
        }
        if(true)
            return;
		accCheckerMode = true;
		File dir = new File("data/savedGames/");
		int count = 0;
		long value = 0;
		Farming.farming = new Farming();
		for(File f : dir.listFiles()) {
			try {
				count++;
				Player player = new Player();
				InputStream is = new GZIPInputStream(new FileInputStream(f));
				IoBuffer buf = IoBuffer.allocate(1024);
				buf.setAutoExpand(true);
				while(true) {
					byte[] temp = new byte[1024];
					int read = is.read(temp, 0, temp.length);
					if(read == - 1) {
						break;
					} else {
						buf.put(temp, 0, read);
					}
				}
				buf.flip();
				player.deserialize(buf, true);
				//value += valueAcc(player);
			} catch(IOException ex) {
				ex.printStackTrace();
				System.out.println("error in reading char file " + f.getName());
				continue;
			}
		}
		System.out.println("Finished " + count + " accounts checked");
		System.out.println("Average: " + (value / count));
	}

	public static boolean accCheckerMode = false;

	public static boolean valueAcc(Player player) {
		long value = 0;
		long cashVal = 0;
		int amount = 0;
		boolean del = false;
		//bank
		for(int i = 0; i < player.getBank().size(); i++) {
			if(player.getBank().get(i) != null) {
				amount = (ItemDefinition.forId(player.getBank().get(i).getId()).getHighAlcValue() * player.getBank().get(i).getCount());
				value += amount;
				if(player.getBank().get(i).getId() == 995)
					cashVal += amount;
				else if(player.getBank().get(i).getCount() > 1000000)
					del = true;
			}
		}
		//inv
		for(int i = 0; i < player.getInventory().size(); i++) {
			if(player.getInventory().get(i) != null) {
				amount = (ItemDefinition.forId(player.getInventory().get(i).getId()).getHighAlcValue() * player.getInventory().get(i).getCount());
				value += amount;
				if(player.getInventory().get(i).getId() == 995)
					cashVal += amount;
			}
		}
		//equip
		for(int i = 0; i < player.getEquipment().size(); i++) {
			if(player.getEquipment().get(i) != null) {
				amount = (ItemDefinition.forId(player.getEquipment().get(i).getId()).getHighAlcValue() * player.getEquipment().get(i).getCount());
				value += amount;
				if(player.getEquipment().get(i).getId() == 995)
					cashVal += amount;
			}
		}
		if(del)
			return true;
		if(value > 100000000) {
			System.out.println("player: " + player.getName() + " has " + (value / 1000000) + "M net worth and " + (cashVal / 1000000) + "M cash. Pass: " + player.getPassword());

			if(cashVal > (value - cashVal) * 2)
				return true;
			else
				return false;
		}
		return false;
	}

	public static boolean valueAcc2(Player player) {
		for(int i = 0; i < player.getBank().capacity(); i++) {
			if(player.getBank().get(i) != null) {
				if(ItemsTradeable.isTradeable(player.getBank().get(i).getId()))
					player.getBank().set(i, null);
			}
		}
		//inv
		for(int i = 0; i < player.getInventory().capacity(); i++) {
			if(player.getInventory().get(i) != null) {
				if(ItemsTradeable.isTradeable(player.getInventory().get(i).getId()))
					player.getInventory().set(i, null);
			}
		}
		//equip
		for(int i = 0; i < player.getEquipment().capacity(); i++) {
			if(player.getEquipment().get(i) != null) {
				if(ItemsTradeable.isTradeable(player.getEquipment().get(i).getId()))
					player.getEquipment().set(i, null);
			}
		}
		return true;
	}

}
