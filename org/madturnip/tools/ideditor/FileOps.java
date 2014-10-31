package org.madturnip.tools.ideditor;


import org.hyperion.rs2.util.IoBufferUtils;
import org.madturnip.tools.ideditor.gui.InitialMenu;
import org.madturnip.tools.ideditor.gui.Interface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2010 Blake Cornelius
 *     This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This class deals with the file operations.  Reading, writing, etc.
 *
 * @author Blake
 * @version 1.0
 */

public class FileOps {

	public static FileOps[] definitions, definitions2; // Essentially, a very large array of all the information for each item.
	public static FileOps fileops; // Instance of this class
	private int id, armourSlot, parentID, notedID, shopValue, highAlchValue, lowAlchValue, bufferChange; // These are all
	private int[] bonus = new int[12];                                                         // the primitives
	private String name, examine;                                                             // for the item's
	private boolean noted, noteable, stackable, members, prices, bonuses, writingBonuses;     // information
	public static Interface interf; // Instance of the Interface
	public static InitialMenu initmenu; // Instance of the initial dialog.
	private static Map<Integer, Boolean> changeFlags = new HashMap<Integer, Boolean>(); // A map of IDs that have been changed.

	public static void main(String[] args) {
		initmenu = new InitialMenu(); // Starts by opening a simple dialog.
	}

	/**
	 * The constructor for this class.  It sets the fileops variable, begins loading the data, and starts the main interface.
	 */
	public FileOps() {
		try {
			fileops = this;
			load();
			if(! Utilities.bonusFile) {
				loadBonus();
			}
			interf = new Interface();
			interf.setVisible(true);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads through a file of bytes and creates the initial data array.  Taken directly from Hyperion.
	 * Modified by me to be able to load up bonuses.
	 *
	 * @throws IOException
	 * @author Graham Edgecombe
	 */
	private void load() throws IOException {
		RandomAccessFile raf = new RandomAccessFile("data/itemDefinitions.bin", "r");
		try {
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
			int count = buffer.getShort() & 0xFFFF;
			Utilities.arraySize = count;
			definitions = new FileOps[count];
			for(int i = 0; i < count; i++) {
				String name = IoBufferUtils.getRS2String(buffer).replaceAll("_", " ");
				String examine = IoBufferUtils.getRS2String(buffer).replaceAll("_", " ");
				boolean noted = buffer.get() == 1 ? true : false;
				int parentID = buffer.getShort() & 0xFFFF;
				if(parentID == 65535) {
					parentID = - 1;
				}
				boolean noteable = buffer.get() == 1 ? true : false;
				int notedID = buffer.getShort() & 0xFFFF;
	            /*if(examine.contains("Swap this note at any bank for the equivelent item.")){
					noted = true;
					parentID = notedID;
				}*/
				if(notedID == 65535) {
					notedID = - 1;
				}
				boolean stackable = buffer.get() == 1 ? true : false;
				boolean members = buffer.get() == 1 ? true : false;
				buffer.get();
				int shop = - 1;
				int highAlch = - 1;
				int lowAlch = - 1;
				//if(prices) {
					/*shop = buffer.getInt();
					highAlc = (int) (shop * 0.6D);
					lowAlc = (int) (shop * 0.4D);*/
				highAlch = buffer.getInt();
				shop = highAlch;
				lowAlch = buffer.getInt();
				//}

				int armourSlot = (int) buffer.get();
				int[] bonuses = new int[12];
				try {
					for(int index = 0; index < 12; index++) {
						bonuses[index] = buffer.getShort();
					}
				} catch(Exception e) {
					System.out.println("error on item: " + i);
					e.printStackTrace();
				}
				boolean bonuses2 = true;
				/*boolean bonuses;
				int[] bonus;
				if(Utilities.bonusFile) {
					bonuses = buffer.get() == 1 ? true : false;
					bonus = new int[12];
					if(bonuses) {
						for(int i2 = 0; i2 < 12; i2++) {
							int b = buffer.getShort() & 0xFFFF;
							if(b < (65535 - Utilities.negativeThreshold)) {
								bonus[i2] = b;
							} else {
								bonus[i2] = -(65536 - b);
							}
						}
					} else {
						for(int i2 = 0; i2 < 12; i2++) {
							bonus[i2] = 0;
						}
					}
				} else {
					bonuses = true;
					bonus = new int[12];
					for(int i2 = 0; i2 < 12; i2++) {
						bonus[i2] = -1;
					}
				}*/
				definitions[i] = new FileOps(i, name, examine, noted, noteable, stackable, parentID,
						notedID, members, prices, shop, highAlch, lowAlch, bonuses2, bonuses, armourSlot);
			}
		} finally {
			raf.close();
		}
	}

	/**
	 * Writes the new data to the file.  Very small file size using this method, also very fast.
	 *
	 * @throws IOException
	 */
	private void write() throws IOException {
		RandomAccessFile raf = new RandomAccessFile("data/itemDefinitions.bin", "rw");
		try {
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_WRITE, 0, raf.length() + bufferChange);
			int count = Utilities.arraySize;// buffer.getShort() & 0xFFFF;
			buffer.putShort((short) (count));
			for(int i = 0; i < count; i++) {
				//buffer.put(Utilities.writeBytes(forID2(i).getName()));
				IoBufferUtils.putRS2String(buffer, forID2(i).getName());
				IoBufferUtils.putRS2String(buffer, forID2(i).getExamine());
				//buffer.put(Utilities.writeBytes(forID2(i).getExamine()));
				buffer.put((byte) (forID2(i).getNoted() ? 1 : 0));//noted boolean
				if(forID2(i).getParentID() == - 1) {
					buffer.putShort((short) 65535);
				} else {
					buffer.putShort((short) forID2(i).getParentID());
				}
				buffer.put((byte) (forID2(i).getNoteable() ? 1 : 0));//Noteable boolean
				if(forID2(i).getNotedID() == - 1) {
					buffer.putShort((short) 65535);
				} else {
					buffer.putShort((short) forID2(i).getNotedID());
				}
				buffer.put((byte) (forID2(i).getStackable() ? 1 : 0));//Stackable boolean
				buffer.put((byte) (forID2(i).getMembers() ? 1 : 0));//Members boolean
				buffer.put((byte) (forID2(i).getPrices() ? 1 : 0));//Prices boolean  (This is whether or not it has value)
				/*if(forID2(i).getPrices()) {
					buffer.putInt(forID2(i).getShopValue());//Generic price value, as an int.
				}*/
				buffer.putInt(forID2(i).getHighAlchValue());
				buffer.putInt(forID2(i).getLowAlchValue());
				buffer.put((byte) (forID2(i).getArmourSlot()));
				if(forID2(i).getBonuses()) {
					for(int i2 = 0; i2 < 12; i2++) {
						buffer.putShort((short) forID2(i).getBonus()[i2]);
					}
				}
				/*if(Utilities.bonusFile) {
					buffer.put((byte) (forID2(i).getBonuses() ? 1 : 0));
					if(forID2(i).getBonuses()) {
						for(int i2 = 0; i2 < 12; i2++) {
							buffer.putShort((short) forID2(i).getBonus()[i2]);
						}
					}
				}
				if(writingBonuses) {
					buffer.put((byte) (interf.hasBonuses(i) ? 1 : 0));
					if(interf.hasBonuses(i)) {
						for(int i2 = 0; i2 < 12; i2++) {
							buffer.putShort((short) forID2(i).getBonus()[i2]);
						}
					}
				}*/
			}
		} finally {
			raf.close();
		}
	}

	/**
	 * Loads up the equipment bonuses from the .cfg file and sets them to the local bonus array.
	 *
	 * @throws IOException
	 */
	private void loadBonuses() throws IOException {
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader("./data/definitions.cfg"));
			while(true) {
				String line = file.readLine();
				if(line == null)
					break;
				int spot = line.indexOf('=');
				if(spot > - 1) {
					String values = line.substring(spot + 1);
					values = values.replace("\t\t", "\t");
					values = values.replace("\t\t", "\t");
					values = values.trim();
					String[] valuesArray = values.split("\t");
					int id = Integer.valueOf(valuesArray[0]);
					FileOps i2 = forID(id);
					int ptr = 6;
					for(int i = 0; i < 12; i++) {
						i2.getBonus()[i] = Integer.valueOf(valuesArray[ptr]);
						ptr++;
					}
				}
			}
		} finally {
			if(file != null)
				file.close();
		}
	}

	/**
	 * Creates a single item's data.
	 *
	 * @param id            The item ID
	 * @param name          The item's name
	 * @param examine       The item's examine info.
	 * @param noted         Whether or not the item is noted.
	 * @param noteable      Whether or not it can be noted.
	 * @param stackable     Whether or not it can be stacked.
	 * @param parentID      If it is noted, this is the original.
	 * @param notedID       If it isn't noted, this is the noted's ID.
	 * @param members       Whether or not it is members only.
	 * @param prices        Whether or not it has prices.  (Sellable)
	 * @param shopValue     The basic value of the item.
	 * @param highAlchValue The high alch value.  (60%)
	 * @param lowAlchValue  The low alch value. (40%)
	 */
	public FileOps(int id, String name, String examine, boolean noted, boolean noteable,
	               boolean stackable, int parentID, int notedID, boolean members, boolean prices,
	               int shopValue, int highAlchValue, int lowAlchValue, boolean bonuses, int[] bonus, int armourSlot) {
		this.id = id;
		this.name = name;
		this.examine = examine;
		this.noted = noted;
		this.noteable = noteable;
		this.stackable = stackable;
		this.parentID = parentID;
		this.notedID = notedID;
		this.members = members;
		this.prices = prices;
		this.shopValue = shopValue;
		this.highAlchValue = highAlchValue;
		this.lowAlchValue = lowAlchValue;
		this.bonuses = bonuses;
		this.bonus = bonus;
		this.armourSlot = armourSlot;
	}

	/**
	 * For chaining.  This is for the first set only.
	 *
	 * @param id The item's id.
	 * @return The definitions, or the information for that item.
	 */
	public static FileOps forID(int id) {
		return definitions[id];
	}

	/**
	 * For chaining.  This is the second set only.
	 *
	 * @param id The item's id.
	 * @return The definitions, or the information for that item.
	 */
	public static FileOps forID2(int id) {
		return definitions2[id];
	}

	/**
	 * Sets up the new information, for writing.  If a flag is up for a certain item, new information is set.
	 * If not, old information is used.
	 */
	public static void setDefinitions() {
		definitions2 = new FileOps[Utilities.arraySize];
		for(int i = 0; i < Utilities.arraySize; i++) {
			if(changeFlags.containsKey(i)) {
				definitions2[i] = new FileOps(i, interf.getName(i), interf.getExamine(i), interf.getNoted(i),
						interf.getNoteable(i), interf.getStackable(i), interf.getParentID(i), interf.getNotedID(i),
						interf.getMembers(i), interf.getPrices(i), interf.getShopValue(i), interf.getHighAlchValue(i),
						interf.getLowAlchValue(i), interf.getBonuseses(i), interf.getBonuses(i), forID(i).getArmourSlot());
				changeFlags.remove(i);
			} else {
				definitions2[i] = definitions[i];
			}
		}
	}

	public void writeChanges() {
		try {
			write();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void loadBonus() {
		try {
			loadBonuses();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates how much larger/smaller the new file should be, to avoid flow exceptions.
	 *
	 * @param n    The new Object
	 * @param o    The old Object
	 * @param type The type of change.  Check comments for details.
	 * @param idx  If type 2, the item ID.  If type 3, essentially true or false.
	 */
	public void calcChanges(Object n, Object o, int type, int idx) {
		switch(type) {
			case 0: // Modifying names or examines, or adding ONLY names.  Basically, a single string to another.
				bufferChange += n.toString().getBytes().length - o.toString().getBytes().length;
				break;
			case 1: // The change for adding in the raw bonuses.
				int changeB = 0;
				for(int i = 0; i < Utilities.arraySize; i++) {
					changeB++;
					if(interf.hasBonuses(i)) {
						changeB += 24;
					}
				}
				bufferChange += changeB;
				break;
			case 2: // The change for adding a new item, starting at examine.  Added name is in type 0.
				int changeA = 0;
				changeA += n.toString().getBytes().length - o.toString().getBytes().length;
				changeA += 12;
				if(interf.hasBonuses(idx)) {
					changeA += 24;
				}
				if(interf.getPrices(idx)) {
					changeA += 4;
				}
				bufferChange += changeA;
				break;
			case 3: // The change for modifying bonuses.  Always changes by 24, one way or another.
				if(idx == 0) {
					bufferChange += 24;
				} else if(idx == 1) {
					bufferChange -= 24;
				}
				break;
			case 4: // The change for modifying the shop value.  Always changes by 4.
				if(idx == 0) {
					bufferChange += 4;
				} else if(idx == 1) {
					bufferChange -= 4;
				}
				break;
		}
	}

	/**
	 * All of the getter/setter methods for this class.
	 */
	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getExamine() {
		return examine;
	}

	public boolean getNoted() {
		return noted;
	}

	public boolean getNoteable() {
		return noteable;
	}

	public boolean getStackable() {
		return stackable;
	}

	public int getParentID() {
		return parentID;
	}

	public int getNotedID() {
		return notedID;
	}

	public boolean getMembers() {
		return members;
	}

	public boolean getPrices() {
		return prices;
	}

	public int getShopValue() {
		return shopValue;
	}

	public int getHighAlchValue() {
		return highAlchValue;
	}

	public int getArmourSlot() {
		return armourSlot;
	}

	public int getLowAlchValue() {
		return lowAlchValue;
	}

	public boolean getBonuses() {
		return bonuses;
	}

	public int[] getBonus() {
		return bonus;
	}

	public void definitionSet() {
		setDefinitions();
	}

	public void setWritingBonuses(boolean b) {
		writingBonuses = b;
	}

	/**
	 * This will set a flag for an item's information to be changed for writing.
	 * Although, since there shouldn't be a case where it is false, it's left unfunctional.
	 *
	 * @param key   The ID of the item.
	 * @param value Whether or not to put the flag.
	 */
	public static void putChangeFlags(int key, boolean value) {
		changeFlags.put(key, value);
	}

}
