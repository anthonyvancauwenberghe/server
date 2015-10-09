package org.hyperion.rs2.model.content.skill;

import org.hyperion.data.PersistenceManager;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
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

	private static final int[] FIRE_OBJECTS = {2732};

	private static final int[] RANGE_OBJECTS = {114, 2728, 4172, 8750, 2732, 2728, 2729, 2730, 2731, 2859, 3039};

	public boolean interfaceLook(final Player player, final int item, final int objId) {
		if(player.isBusy())
			return false;

		player.closeChatInterface = true;
		player.getExtraData().put("cookFishId", item);
		player.getActionSender().sendPacket164(1743);
		player.getActionSender().sendInterfaceModel(13716, 250, item);
		return false;
	}

	/**
	 * Cooks an item.
	 *
	 * @param item   The item that has to be cooked.
	 */
	public boolean cookItem(final Player client, final int item, final boolean fireCook, final int amount) {
		final CookingItem cookItem;
		try {
			cookItem = cookingItems.get(getIndex(item));
		} catch(Exception e) {
			return false;
		}
		ContentEntity.removeAllWindows(client);

		if(ContentEntity.returnSkillLevel(client, 7) < cookItem.getLevel()) {
			ContentEntity.sendMessage(client, "You need a cooking level of " + cookItem.getLevel() + " to cook this item.");
			return true;
		}
		if(client.isBusy())
			return false;
		client.setBusy(true);

		if(! fireCook) {
			ContentEntity.startAnimation(client, COOKING_ANIM_RANGE);
		} else {
			ContentEntity.startAnimation(client, COOKING_ANIM_FIRE);
		}

		World.getWorld().submit(new Event(2500) {
			int amount2 = amount;

			@Override
			public void execute() {
				if(!client.isBusy()) {
					stop();
					return;
				}
				if(client.getRandomEvent().skillAction(2)) {
					stop();
					return;
				}

				amount2--;
				ContentEntity.deleteItem(client, item);
				String name = ContentEntity.getItemName(item);

				if(Combat.random(client.getSkills().getLevel(7) + 3 - cookItem.getLevel()) != 1) {
					ContentEntity.addItem(client, cookItem.getCookedItem(), 1);
					client.getAchievementTracker().itemSkilled(Skills.COOKING, cookItem.getCookedItem(), 1);
					ContentEntity.addSkillXP(client, cookItem.getExperience() * EXPMULTIPLIER, 7);
					ContentEntity.sendMessage(client, "You succesfully cook some " + ContentEntity.getItemName(cookItem.getCookedItem()).toLowerCase() + ".");
				} else {
					ContentEntity.addItem(client, cookItem.getBurnedItem(), 1);
					client.getAchievementTracker().itemSkilled(Skills.COOKING, cookItem.getBurnedItem(), 1);
					ContentEntity.sendMessage(client, "You accidentally burn the " + name.toLowerCase() + ".");
				}

				if(amount2 <= 0 || ContentEntity.getItemSlot(client, item) == - 1) {
					stop();
					return;
				}

				if (!fireCook) {
					ContentEntity.startAnimation(client, COOKING_ANIM_RANGE);
				} else {
					ContentEntity.startAnimation(client, COOKING_ANIM_FIRE);
				}
			}

			@Override
			public void stop() {
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
			int id = (Integer)client.getExtraData().get("cookFishId");
			if(itemId == 13720)
				cookItem(client, id, fire, 1);
			if(itemId == 13719)
				cookItem(client, id, fire, 5);
			if(itemId == 13718)
				cookItem(client, id, fire, 10);
			if(itemId == 13717)
				cookItem(client, id, fire, 28);
			return true;
		} else if(type == 14) {
			for(int i = 0; i < FIRE_OBJECTS.length; i++)
				if (objId == FIRE_OBJECTS[i]) {
					client.getExtraData().put("fire", true);
					return interfaceLook(client, itemId, objId);
				}
			for(int i = 0; i < RANGE_OBJECTS.length; i++)
				if (objId == RANGE_OBJECTS[i]) {
					client.getExtraData().put("fire", false);
					return interfaceLook(client, itemId, objId);
				}
			return false;
		} else {
			return false;
		}
	}

}