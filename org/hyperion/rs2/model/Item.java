package org.hyperion.rs2.model;

import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.shops.DonatorShop;

/**
 * Represents a single item.
 *
 * @author Graham Edgecombe
 */
public class Item {

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The number of items.
	 */
	private int count;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static Item create(int id) {
		return new Item(id, 1);
	}
	/**
	 * 
	 * @param id
	 * @param count
	 * @return
	 */
	public static Item create(int id, int count) {
		return count <= 0 ? null : new Item(id, count);
	}
	/**
	 * Creates a single item.
	 *
	 * @param id The id.
	 */
	public Item(int id) {
		this(id, 1);
	}

	/**
	 * Creates a stacked item.
	 *
	 * @param id    The id.
	 * @param count The number of items.
	 * @throws IllegalArgumentException if count is negative.
	 */
	public Item(int id, int count) {
		if(count < 0) {
			System.out.println("Count is " + count);
			throw new IllegalArgumentException("Count cannot be negative.");
		}
		this.id = id;
		this.count = count;
	}

	/**
	 *
	 * Compares the price between this item and the specified item
	 *
	 * @param item the item in which to compare prices with
	 * @return the item with the highest high alchemy price
	 */
	public Item comparePriceWith(Item item) {
		int dpValue1 = DonatorShop.getPrice(getId());
		int dpValue2 = item != null ? DonatorShop.getPrice(item.getId()) : -1;
		if(dpValue1 > dpValue2)
			return this;
		else if(dpValue1 < dpValue2)
			return item;
		int item1Value = getDefinition().getHighAlcValue();
		int item2Value = item == null ? - 1 : item.getDefinition().getHighAlcValue();
		return item1Value > item2Value ? this : item;
	}

	/**
	 * Compares the price between this item and an item with the specified id
	 *
	 * @param itemId the item id in which to compare prices with
	 * @return the item id with the highest high alchemy price
	 */
	public int comparePriceWith(int itemId) {
		ItemDefinition itemDef = ItemDefinition.forId(itemId);
		int dpValue1 = DonatorShop.getPrice(getId());
		int dpValue2 = DonatorShop.getPrice(itemId);
		if(dpValue1 > dpValue2)
			return getId();
		else if(dpValue1 < dpValue2)
			return itemId;
		int item1Value = getDefinition().getHighAlcValue();
		int item2Value = itemDef == null ? - 1 : itemDef.getHighAlcValue();
		return item1Value > item2Value ? getId() : itemId;
	}


    public BankItem toBankItem(int tab) {
        return new BankItem(tab, this.id, this.count);
    }

	/**
	 * Gets the definition of this item.
	 *
	 * @return The definition.
	 */
	public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
	}

	/**
	 * Gets the item id.
	 *
	 * @return The item id.
	 */
	public int getId() {
		return id;
	}

	public void setId(int ID) {
		id = ID;
	}

	/**
	 * Gets the count.
	 *
	 * @return The count.
	 */
	public int getCount() {
		if(count < 0) {
			count = 0;
			throw new IllegalArgumentException("Huge bug in Item class!:id,count:" + id + "," + count);
		}
		return count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object object) {
		Item item;
		if(object instanceof Item)
			item = (Item) object;
		else
			return false;
		return id == item.id;
	}

	public Item copy() {
		return new Item(id, count);
	}

	@Override
	public String toString() {
		return Item.class.getName() + " [id=" + id + ", count=" + count + "]";
	}

}
