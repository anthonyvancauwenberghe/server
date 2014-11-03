package org.hyperion.rs2.model.content.misc2;

import org.hyperion.data.PersistenceManager;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.OverloadDrinkingEvent;
import org.hyperion.rs2.event.impl.OverloadStatsEvent;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc.FoodItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Food and potion system.
 *
 * @author Martin
 */
public class Food implements ContentTemplate {

	/**
	 * Animation IDs sent to the client.
	 */
	public final int ANIMATION_EAT_ID = 829,
			ANIMATION_DRINK_ID = 829;//1652

	private static List<FoodItem> foods;

    /** Holds the item ids of combo foods.*/
    public static final int[] COMBO_FOODS = {3144, 2185,9553,2229};

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws FileNotFoundException {
		foods = (List<FoodItem>) PersistenceManager.load(new FileInputStream("./data/food.xml"));
		System.out.println(foods.size() + " food loaded.");
	}

	@Override
	public int[] getValues(int type) {
		if(type != 1)
			return null;
		int[] j = new int[foods.size()];
		int i2 = 0;
		for(FoodItem i : foods) {
			j[i2++] = i.getId();
		}
		return j;
	}


	public static FoodItem get(int id) {
		for(FoodItem i : foods) {
			if(i.getId() == id) {
				return i;
			}
		}
		return null;
	}


	public void antiFire(final Player player, boolean superFire) {
		final long timer = player.antiFireTimer = System.currentTimeMillis();
		player.superAntiFire = superFire;
		World.getWorld().submit(new Event(1000, "dfire") {
			boolean warned = false;

			@Override
			public void execute() {
				if(player.isHidden() || ! player.isActive() || player.antiFireTimer != timer) {
					this.stop();
					return;
				}
				if(! warned && System.currentTimeMillis() - timer > 345000) {
					warned = true;
					player.getActionSender().sendMessage("Your resistance to dragonfire is about to run out!");
				}
				if(System.currentTimeMillis() - timer > 360000) {
					player.getActionSender().sendMessage("Your resistance to dragonfire has run out!");
					this.stop();
					return;
				}
			}

		});
	}

	/**
	 * Eating hook.
	 *
	 * @param player
	 * @param id
	 * @return
	 */
	@Override
	public boolean clickObject(final Player player, final int type, final int id, final int slot, final int c, final int d) {
		final FoodItem foodItem = get(id);

        /** Combo food eating */
        if(System.currentTimeMillis() - (player.comboFoodTimer) < 1500 || player.isDead())
            return true;
            for (int comboFood : COMBO_FOODS) {
                if (id == comboFood) {
                    switch (id) {
                        case 3144:
                            eatComboFood(player, slot, id, 18, "cooked karambwan");
                            break;
                        case 2185:
                        case 2229:
                        case 9553:
                            eatComboFood(player, slot, id, 15, "chocolate bomb");
                            break;
                    }
                }
            }
			if(foodItem == null)
                return false;
		//TODO FIX THE MASSING
		player.cE.deleteSpellAttack();

		if(System.currentTimeMillis() - (! foodItem.isDrink() ? player.foodTimer : player.potionTimer) < 1500 || player.isDead())
			return true;
		if(! ContentEntity.isItemInBag(player, id, slot)) return true;
		if(! foodItem.isDrink()) {
			if(player.duelRule[6] && player.duelAttackable > 0) {
				player.getActionSender().sendMessage("You cannot use food in this duel.");
				return true;
			}
			player.foodTimer = System.currentTimeMillis();
			//client.potionTimer = System.currentTimeMillis();
			player.cE.predictedAtk = Math.max(System.currentTimeMillis() + 2000, player.cE.predictedAtk);
		} else {
			if(player.duelRule[5] && player.duelAttackable > 0) {
				player.getActionSender().sendMessage("You cannot use drinks in this duel.");
				return true;
			}

			player.foodTimer = System.currentTimeMillis();
		}

		boolean doAnim = true;
		switch(foodItem.getId()) {

		case 15272:
			rocktail(player, slot);
			return true;
			case 2452:
			case 2454:
			case 2456:
			case 2458:
				antiFire(player, false);
				break;
			case 6685:
			case 6687:
			case 6689:
			case 6691:
				saraBrew(player);
				break;
			case 15304:
			case 15305:
			case 15306:
			case 15307:
				antiFire(player, true);
				break;
			case 2430:
				if(foodItem.getId() == 2430 && System.currentTimeMillis() - player.specPotionTimer < 20000) {
					ContentEntity.sendMessage(player, "You can only drink special restore potions every 20 seconds!");
					return true;
				}
				ContentEntity.startAnimation(player, ANIMATION_DRINK_ID);
				//client.getActionSender().sendMessage("You drink a Special restore potion.");

				player.getSpecBar().increment(SpecialBar.FULL / 2);
				player.getSpecBar().sendSpecAmount();
				player.specPotionTimer = System.currentTimeMillis();
				break;
			case 15332:
			case 15333:
			case 15334:
			case 15335:
				if(player.getSkills().getLevel(3) < 51) {
					ContentEntity.sendMessage(player, "Not enough hitpoints to use an overload potion");
					return true;
				}
				doAnim = false;
				player.setOverloaded(true);
				player.resetOverloadCounter();
				World.getWorld().submit(new OverloadDrinkingEvent(player));
				if(!player.getExtraData().getBoolean(OverloadStatsEvent.KEY))
					World.getWorld().submit(new OverloadStatsEvent(player));
				break;
		}
		if(doAnim)
			if(! foodItem.isDrink())
				ContentEntity.startAnimation(player, ANIMATION_EAT_ID);
			else
				ContentEntity.startAnimation(player, ANIMATION_DRINK_ID);

		ContentEntity.deleteItem(player, id, slot, 1);

		if(foodItem.getNewId() != - 1) {
			ContentEntity.replaceItem(player, slot, foodItem.getNewId(), 1);
		}
		if(foodItem.isDrink()) {
			ContentEntity.sendMessage(player, "You drink the " + ItemDefinition.forId(id).getName() + ".");
            player.potionTimer = System.currentTimeMillis();
		} else {
			ContentEntity.sendMessage(player, "You eat the " + ItemDefinition.forId(id).getName() + ".");
		}


		if(foodItem.getSkillId() == 20 && foodItem.getId() != 2430) {
			player.cE.setPoisoned(false);
			ContentEntity.sendMessage(player, "Your poison clears up.");
			return true;
		}
		if(foodItem.getSkillId() == 5) {
			int standardLevel = ContentEntity.getLevelForXP(player, 5);
			int levelAtm = ContentEntity.returnSkillLevel(player, 5);
			if(levelAtm < standardLevel) {
				int amount = 7 + ((int) (standardLevel * 0.25));
				if(amount > standardLevel - levelAtm)
					amount = standardLevel - levelAtm;
				ContentEntity.increaseSkill(player, 5, amount);
			}
			//System.out.println("HIERo'");
			return true;
		}
		if(foodItem.getSkillId() == 21) {
			for(int i = 0; i < Skills.SKILL_NAME.length; i++) {
				if(i == 3)
					continue;
				int standardLevel = ContentEntity.getLevelForXP(player, i);
				int levelAtm = ContentEntity.returnSkillLevel(player, i);
				if(levelAtm < standardLevel) {
					int amount = (int) (standardLevel * 0.33);
					if(amount > standardLevel - levelAtm)
						amount = standardLevel - levelAtm;
					ContentEntity.increaseSkill(player, i, amount);
				}
			}
			return true;
		}
		if(foodItem.getHeal() < 0 || (foodItem.getHeal() > 0)) {
			ContentEntity.heal(player, foodItem.getHeal());
		}
		if(foodItem.getSkillId() != - 1 && foodItem.getSkillAdd() != - 1) {
			ContentEntity.increaseSkill(player, foodItem.getSkillId(), foodItem.getSkillAdd());
		}
	            /*this.stop();
            }
		});*/
		return true;
	}

	private void rocktail(Player player, int slot) {
		int heal = 23;
		int newHpLevel = player.getSkills().getLevel(Skills.HITPOINTS) + 23;
		if(newHpLevel >= player.getSkills().calculateMaxLifePoints() + 10) {
			heal = (player.getSkills().calculateMaxLifePoints() + 10) - player.getSkills().getLevel(3);
		}
		player.heal(heal, true);
		player.getActionSender().sendMessage("You eat the rocktail");
		ContentEntity.startAnimation(player, ANIMATION_EAT_ID);
		ContentEntity.deleteItem(player, 15272, slot, 1);
	}

    private void eatComboFood(Player player, int slot, int id, int healAmt, String foodName) {
        int heal = healAmt;
        player.heal(heal, true);
        ContentEntity.startAnimation(player, ANIMATION_EAT_ID);
        ContentEntity.deleteItem(player, id, slot, 1);
        player.comboFoodTimer = System.currentTimeMillis();
       // player.potionTimer = System.currentTimeMillis();
        player.getActionSender().sendMessage("You eat the "+foodName+".");

    }
	private void saraBrew(Player client) {
		final int newDefLevel = (int) (0.25 * ContentEntity.getLevelForXP(client, 1));
		final int newHpLevel = (int) (0.15 * client.getSkills().calculateMaxLifePoints());


		final int newAttackLevel = (int) (0.1 * ContentEntity.getLevelForXP(client, 0));
		final int newStrLevel = (int) (0.1 * ContentEntity.getLevelForXP(client, 2));
		final int newRange = (int) (0.1 * ContentEntity.getLevelForXP(client, 4));
		final int newMagic = (int) (0.1 * ContentEntity.getLevelForXP(client, 6));
		ContentEntity.increaseSkill(client, 1, newDefLevel);
		client.heal(newHpLevel, true);

		ContentEntity.decreaseSkill(client, 0, newAttackLevel);
		ContentEntity.decreaseSkill(client, 2, newStrLevel);
		ContentEntity.decreaseSkill(client, 4, newRange);
		ContentEntity.decreaseSkill(client, 6, newMagic);
	}

	
	/*
	 * We are a wholly class.
	 */


}
