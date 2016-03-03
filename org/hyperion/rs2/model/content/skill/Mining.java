package org.hyperion.rs2.model.content.skill;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc2.Edgeville;

import java.io.FileNotFoundException;


/**
 * Mining skill handler
 *
 * @author Graham
 */
public class Mining implements ContentTemplate {

	private static final int EXPMULTIPLIER = Constants.XPRATE;

	// TODO a better method of this than these ugly arrays
	private static final int[] ROCKS = {2491, 2108, 2109, 2090, 2091, 2094,
			2095, 2110, 4030, 2092, 2093, 2101, 3403, 2096, 2097, 2099, 2102, 2103, 2104, 2105,
			2107,/*doors into guild*/ 2112, 1755, 2113};
	private static final int[] ROCKS_ORES = {1436, 434, 434, 436, 436, 438,
			438, 668, 3211, 440, 440, 442, 2892, 453, 453, 444, 447, 447, 449, 449, 451};
	private static final int[] ROCK_REQUIRED_LEVELS = {1, 1, 1, 1, 1, 1, 1,
			10, 10, 10, 10, 15, 20, 20, 30, 40, 55, 55, 55, 70, 85};
	private static final int[] ROCKS_ORES_XPS = {5, 5, 5, 17, 17, 17, 17, 17,
			26, 35, 35, 40, 0, 50, 65, 80, 80, 80, 95, 125};
	private static final String[] ROCKS_ORES_NAMES = {"rune essence", "clay",
			"clay", "copper", "copper", "tin", "tin", "bluerite", "limestone",
			"iron", "iron", "silver", "elemental", "coal", "gold", "mithril", "mithril", "mithril",
			"adamant", "runite"};
	private static final int[] PICKAXES = {1275, 1271, 1273, 1269, 1267, 1265};
	private static final int[] PICKAXE_ANIMATIONS = {624, 628, 629, 627, 626,
			625};
	private static final int[] PICKAXES_REQUIRED_LEVELS = {41, 31, 21, 6, 1, 1};
	private static final int MINING_DELAY = 600;
	private static final int PROSPECTING_DELAY = 1000;
	//private static double MINING_FACTOR = 0.2;
	private static final int EXPIRED_ORE = 450;
	private static final int ORE_RESPAWN_TIME = 10000;

	public static boolean prospect(final Player player, final int type,
	                               final int x, final int y) {
		if(! isRock(type)) {
			return false;
		} else {
			if(player.isBusy()) {
				return true;
			} else {
				player.setBusy(true);
			}
			final String name = getOreName(type);
			player.getActionSender().sendMessage(
					"You examine the rock for ores...");
			ContentEntity.turnTo(player, x, y);
			World.submit(new Task(PROSPECTING_DELAY,"prospecting") {
				@Override
				public void execute() {
					player.getActionSender().sendMessage(
							"This rock contains " + name + ".");
					stop2();
				}


				public void stop2() {
					player.setBusy(false);
					this.stop();
				}
			});
			return true;
		}
	}

	public static boolean mine(final Player player, final int objectID,
	                           final int objectX, final int objectY) {
		if(objectID == 450) {
			player.getActionSender().sendMessage(
					"That rock contains no ore.");
			return true;
		}
		if(! isRock(objectID)) {
			return false;
		} else {
			if(player.isBusy()) {
				return true;
			}
			if(ContentEntity.freeSlots(player) == 0) {
				player.getActionSender().sendMessage(
						"There is not enough space in your inventory.");
				return true;
			}
			final int pickaxe = hasPickaxe(player);
			if(pickaxe == - 1) {
				player.getActionSender().sendMessage(
						"You do not have a pickaxe that you can use.");
				return true;
			}
			if(player.getSkills().getLevel(14) < getOreLevel(objectID)) {
				player.getActionSender().sendMessage(
						"You need a mining level of " + getOreLevel(objectID)
								+ " to mine this rock.");
				return true;
			}
			final int oreType = getOre(objectID);
			if(oreType == - 1) {
				player.getActionSender().sendMessage(
						"You cannot mine that ore.");
				return true;
			}
			GameObject g = ObjectManager.getObjectAt(objectX, objectY,
					player.getPosition().getZ());
			if(g != null && g.getType() == EXPIRED_ORE) {
				player.getActionSender().sendMessage(
						"There is no ore currently available in this rock.");
				return true;
			}
			player.getActionSender().sendMessage(
					"You swing your pick at the rock...");
			player.setBusy(true);
			ContentEntity.turnTo(player, objectX, objectY);
			ContentEntity.startAnimation(player,
					PICKAXE_ANIMATIONS[pickaxe]);
			World.submit(new Task(MINING_DELAY,"mining delay") {
				@Override
				public void execute() {
					GameObject g = ObjectManager.getObjectAt(objectX, objectY,
							player.getPosition().getZ());


					if(! player.isBusy()) {
						stop2();
						return;
					}
					if(g != null && g.getType() == EXPIRED_ORE) {
						stop2();
						player
								.getActionSender()
								.sendMessage(
										"There is currently no ore available in this rock.");
						return;
					}
					if(ContentEntity.freeSlots(player) == 0) {
						player.getActionSender().sendMessage(
								"Your inventory is too full to hold any more "
										+ getOreName(objectID) + ".");
						stop2();
					} else {
						//if (Math.random() > MINING_FACTOR) {
						if(ContentEntity.random(((player.getSkills().getLevel(14) / 10) + 11)) >= (11 + (ContentEntity.random(getOreLevel(objectID)) / 10))) {
							ContentEntity.addItem(player, oreType, 1);
							player.getActionSender().sendMessage(
									"You manage to mine some "
											+ getOreName(objectID) + ".");
							int xp = getOreXp(objectID);
							ContentEntity.addSkillXP(player, xp * EXPMULTIPLIER,
									14);

							if(objectID != 2491) {
								stop2();
								if(Edgeville.POSITION.distance(Position.create(objectX, objectY, 0)) > 50 && Position.create(3370, 3240, 0).distance(Position.create(objectX, objectY, 0)) > 70) {
									final GameObject expired = new GameObject(GameObjectDefinition.forId(EXPIRED_ORE), Position.create(objectX, objectY, player.getPosition().getZ()), 10, 0);
									final GameObject normal = new GameObject(GameObjectDefinition.forId(objectID), Position.create(objectX, objectY, player.getPosition().getZ()), 10, 0);

									ContentEntity.startAnimation(player, - 1);
									ObjectManager.addObject(expired);
									World.submit(new Task(ORE_RESPAWN_TIME,"Ore respawn Time") {
										@Override
										public void execute() {
											ObjectManager.replace(expired, normal);
											this.stop();
										}
									});
								}
							}
						} else {
							ContentEntity.startAnimation(player,
									PICKAXE_ANIMATIONS[pickaxe]);
						}
					}
				}


				public void stop2() {
					player.setBusy(false);
					this.stop();
				}
			});
			return true;
		}
	}

	/**
	 * Gets the ore item id for a rock type.
	 *
	 * @param rock The rock.
	 * @return The ore id.
	 */
	public static int getOre(int rock) {
		int ct = 0;
		for(int obj : ROCKS) {
			if(obj == rock) {
				return ROCKS_ORES[ct];
			}
			ct++;
		}
		return - 1;
	}

	/**
	 * Gets level needed to mine ore
	 *
	 * @param rock The rock.
	 * @return The level needed to mine the ore.
	 */
	public static int getOreLevel(int rock) {
		int ct = 0;
		for(int obj : ROCKS) {
			if(obj == rock) {
				return ROCK_REQUIRED_LEVELS[ct];
			}
			ct++;
		}
		return - 1;
	}

	/**
	 * Checks if an object is in the rocks array.
	 *
	 * @param id The object id.
	 * @return True if the object is a rock, false if not.
	 */
	public static boolean isRock(int id) {
		// check if id is a valid rock
		for(int obj : ROCKS) {
			if(obj == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks ore name
	 *
	 * @param rock The object id.
	 * @return The rock ores name
	 */
	public static String getOreName(int rock) {
		int ct = 0;
		for(int obj : ROCKS) {
			if(obj == rock) {
				return ROCKS_ORES_NAMES[ct];
			}
			ct++;
		}
		return "-1";
	}

	/**
	 * Gets ore XP.
	 *
	 * @param rock
	 * @return
	 */
	public static int getOreXp(int rock) {
		int ct = 0;
		for(int obj : ROCKS) {
			if(obj == rock) {
				return ROCKS_ORES_XPS[ct];
			}
			ct++;
		}
		return - 1;
	}

	/**
	 * Checks if the player has a pick axe.
	 *
	 * @param client The player.
	 * @return True if the player has one, false if not.
	 */
	public static int hasPickaxe(Player client) {
		int ct = 0;
		int level = client.getSkills().getLevel(14);
		for(int id : PICKAXES) {
			if(level >= PICKAXES_REQUIRED_LEVELS[ct]) {
				if(client.getEquipment().get(3) != null && client.getEquipment().get(3).getId() == id) {
					return (ct);
				} else if(ContentEntity.isItemInBag(client, id)) {
					return (ct);
				}
			}
			ct++;
		}
		return (- 1);
	}

	/**
	 * Gets level needed to use pick axe
	 *
	 * @param pickaxe The pick axe.
	 * @return The level needed to use the pick axe.
	 */
	public static int getPickLevel(int pickaxe) {
		int ct = 0;
		for(int obj : PICKAXES) {
			if(obj == pickaxe) {
				return PICKAXES_REQUIRED_LEVELS[ct];
			}
			ct++;
		}
		return - 1;
	}

	@Override
	public void init() throws FileNotFoundException {
	}

	@Override
	public int[] getValues(int type) {
	    /*if(type == 6 || type == 7){
            return ROCKS;
		}*/
		return null;
	}

	@Override
	public boolean clickObject(final Player client, final int type, final int id, final int slot, final int itemId2, final int npcSlot) {
		if(id == 2112) {
			if(ContentEntity.getLevelForXP(client, Skills.MINING) < 60) {
				ContentEntity.sendMessage(client, "You need 60 mining to enter the guild.");
				return true;
			} else
				return false;
		}
		if(id == 1755) {
			client.setTeleportTarget(Position.create(client.getPosition().getX(), client.getPosition().getY() - 6400, 0));
			return true;
		}
		if(id == 2113) {
			if(ContentEntity.getLevelForXP(client, Skills.MINING) < 60) {
				ContentEntity.sendMessage(client, "You need 60 mining to enter the guild.");
				return true;
			}
			client.setTeleportTarget(Position.create(client.getPosition().getX(), client.getPosition().getY() + 6400, 0));
			return true;
		}
		if(type == 6)
			return mine(client, id, slot, itemId2);
		else
			return prospect(client, id, slot, itemId2);
	}

}
