package org.hyperion.rs2.model.content.skill;

import org.hyperion.data.PersistenceManager;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc.CookingItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;


/**
 * Handles the cooking.
 *
 * @author Jonas(++);
 */

public class Cooking implements ContentTemplate {

	private static final int EXPMULTIPLIER = Constants.XPRATE * 5;

	/**
	 * Range cooking animation
	 */
	private final int COOKING_ANIM_RANGE = 896;

	/**
	 * Fire cooking animation
	 */
	private final int COOKING_ANIM_FIRE = 897;

	/**
	 * Class constructor.
	 */
	public Cooking() {

	}

	/**
	 * List with all the cookingItems in.
	 */

	private List<CookingItem> cookingItems;

	private final Random r = new Random();

	/**
	 * Checks if the item is cookable.
	 *
	 * @param item The item that needs to be checked.
	 */

	public boolean isCookItem(int item) {
		for(CookingItem c : cookingItems) {
			int id = c.getCookId();
			if(id == item)
				return true;
		}
		return false;
	}

	/**
	 * Gets the index of the item
	 *
	 * @param item The item of which index has to be returned.
	 */

	public int getIndex(int item) {
		for(CookingItem c : cookingItems) {
			if(c.getCookId() == item)
				return cookingItems.indexOf(c);
		}
		return - 1;
	}

	private static final int[] FIRE_OBJECTS = {2732,};

	private static final int[] RANGE_OBJECTS = {114, 2728, 4172, 8750, 2732, 2728, 2729, 2730, 2731, 2859, 3039,};

	public boolean interfaceLook(final Player player, final int item, final int objId) {
		if(player.isBusy())
			return false;
		//Check if we are using a proper object
		boolean fire = false;
		for(int i = 0; i < FIRE_OBJECTS.length; i++) {
			if(i == objId) {
				fire = true;
			}
		}
	    /*boolean canCook = false;
        for(int i = 0; i < RANGE_OBJECTS.length; i++) {
			if(i == objId) {
				canCook = true;
			}
		}
		for(int i = 0; i < FIRE_OBJECTS.length; i++) {
			if(i == objId) {
				canCook = true;
			}
		}
		if(canCook == false) {
			return false;
		}*/
		player.closeChatInterface = true;
		player.getExtraData().put("fire", fire);
		player.getExtraData().put("cookFishId", item);
		player.getActionSender().sendPacket164(1743);
		player.getActionSender().sendInterfaceModel(13716, 250, item);
		return false;
	}

	/**
	 * Cooks an item.
	 *
	 * @param client The {@link Client}.
	 * @param item   The item that has to be cooked.
	 */
	public boolean cookItem(final Player client, final int item, final boolean fireCook, final int amount) {
		final CookingItem cookItem;
		try {
			// Create the variable.
			cookItem = cookingItems.get(getIndex(item));
		} catch(Exception e) {
			return false;
		}
		ContentEntity.removeAllWindows(client);
		//TODO: Add in face object
		// Check if the player can cook this item.
		if(ContentEntity.returnSkillLevel(client, 7) < cookItem.getLevel()) {
			ContentEntity.sendMessage(client, "Your Cooking level is not high enough.");
			return true;
		}
		client.setBusy(true);
		// Start animation.
		if(! fireCook) {
			ContentEntity.startAnimation(client, COOKING_ANIM_RANGE);
		} else {
			ContentEntity.startAnimation(client, COOKING_ANIM_FIRE);
		}

		World.getWorld().submit(new Event(3000) {
			int amount2 = amount;

			@Override
			public void execute() {
				if(! client.isBusy()) {
					stop2();
					return;
				}
				// Check if the player still have the item that has to be cooked.
				if(ContentEntity.getItemAmount(client, item) != 0 || amount2 > 0) {
					amount2--;
					// Remove the item.
					ContentEntity.deleteItem(client, item);

					String name = ContentEntity.getItemName(item);

					// Check if the item has a succes level.
					//if(cookItem.getSuccesLevel() != -1 && ContentEntity.returnSkillLevel(client,7) >= cookItem.getSuccesLevel()) {
					if(Combat.random(client.getSkills().getLevel(7) + 3 - cookItem.getLevel()) != 1) {
						ContentEntity.addItem(client, cookItem.getCookedItem(), 1);
						ContentEntity.addSkillXP(client, cookItem.getExperience() * EXPMULTIPLIER, 7);
						ContentEntity.sendMessage(client, "You succesfully cook the " + name + ".");

					} else {
						// Add the cooked/burned item.
						//if(r.nextInt(3) != 1) {
						/*if (Combat.random(client.getSkills().getLevel(7) + 3 - cookItem.getLevel()) == 1) {
							ContentEntity.addItem(client,cookItem.getCookedItem(), 1);
							ContentEntity.addSkillXP(client,cookItem.getExperience(), 7);
							ContentEntity.sendMessage(client,"You succesfully cook the "+name+".");
						} else {*/
						ContentEntity.addItem(client, cookItem.getBurnedItem(), 1);
						ContentEntity.sendMessage(client, "You accidentally burn the " + name + ".");
						//}
					}

					// Start animation.
					if(ContentEntity.getItemSlot(client, item) != - 1) {
						if(! fireCook) {
							ContentEntity.startAnimation(client, COOKING_ANIM_RANGE);
						} else {
							ContentEntity.startAnimation(client, COOKING_ANIM_FIRE);
						}
					} else {
						stop2();
					}

				} else {
					stop2();
				}

			}

			public void stop2() {
				ContentEntity.startAnimation(client, - 1);
				client.setBusy(false);
				this.stop();
			}

		});
		return true;
	}

	/**
	 * Loads the XML file of cooking.
	 *
	 * @throws FileNotFoundException
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws FileNotFoundException {
		cookingItems = (List<CookingItem>) PersistenceManager.load(new FileInputStream("./data/cooking.xml"));
	}

	@Override
	public int[] getValues(int type) {
		if(type == 0) {
			int[] j = {13720, 13719, 13718, 13717,};
			return j;
		}
		if(type == 14) {
			int[] j = new int[cookingItems.size()];
			int i = 0;
			for(CookingItem cI : cookingItems) {
				if(cI.getCookId() > 0) {
					j[i] = cI.getCookId();
					i++;
				}
			}
			return j;
		}
		return null;
	}

	@Override
	public boolean clickObject(final Player client, final int type, final int itemId, final int slot, final int objId, final int a) {
		if(type == 0) {
			boolean fire = (Boolean) client.getExtraData().get("fire");
			if(itemId == 13720)
				cookItem(client, (Integer) client.getExtraData().get("cookFishId"), fire, 1);
			if(itemId == 13719)
				cookItem(client, (Integer) client.getExtraData().get("cookFishId"), fire, 5);
			if(itemId == 13718)
				cookItem(client, (Integer) client.getExtraData().get("cookFishId"), fire, 10);
			if(itemId == 13717)
				cookItem(client, (Integer) client.getExtraData().get("cookFishId"), fire, 28);
			return true;
		} else if(type == 14 && (objId == 114 || objId == 2728 || objId == 4172 || objId == 8750 || objId == 2732 || objId == 2728 || objId == 2729 || objId == 2730 || objId == 2731 || objId == 2859 || objId == 3039)) {
			return interfaceLook(client, itemId, objId);
		} else {
			return false;
		}
	}

}