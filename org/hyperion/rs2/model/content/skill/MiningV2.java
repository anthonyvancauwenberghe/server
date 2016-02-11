package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.util.ArrayUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MiningV2 implements ContentTemplate {

	private static final int EXPMULTIPLIER = Constants.XPRATE + 5;

	public enum Pickaxe {
		BRONZE(1265,
				1,/*level*/
				625,/*ANIM*/
				1/*value*/
		),
		IRON(1267,
				1,/*level*/
				626,/*ANIM*/
				2/*value*/
		),
		STEEL(1269,
				5,/*level*/
				627,/*ANIM*/
				3/*value*/
		),
		MITH(1273,
				20,/*level*/
				629,/*ANIM*/
				4/*value*/
		),
		ADDY(1271,
				30,/*level*/
				628,/*ANIM*/
				5/*value*/
		),
		RUNE(1275,
				40,/*level*/
				624,/*ANIM*/
				6/*value*/
		),
		DRAGON(15259,
				99,
				12188,
				7
		);

		Pickaxe(int pick, int level, int anim, int value) {
			this.pick = pick;
			this.level = level;
			this.anim = anim;
			this.value = value;
		}

		public int pick;
		public int level;
		public int anim;
		public int value;

	}


	public static Pickaxe hasPickaxe(Player client) {
		Item wep = client.getEquipment().get(3);
		Pickaxe pick;
		if(wep != null && (pick = pickaxes.get(wep.getId())) != null)
			if(ContentEntity.returnSkillLevel(client, Skills.MINING) >= pick.level)
				return pick;
		for(Item item : client.getInventory().toArray()) {
			if(item != null)
				if((pick = pickaxes.get(item.getId())) != null)
					if(ContentEntity.returnSkillLevel(client, Skills.MINING) >= pick.level)
						return pick;
		}
		return null;
	}

	public enum Rock {
		TIN(438,
				1,/*level*/
				80,/*xp*/
				5,/*time*/
				3,/*respawndelay*/
				new int[]{2094, 2095,14902}/*rock ids*/
		),

		COPPER(436,
				1,/*level*/
				80,/*xp*/
				5,/*time*/
				3,/*respawndelay*/
				new int[]{2090, 2091, 14906}/*rock ids*/
		),

		IRON(440,
				15,/*level*/
				135,/*xp*/
				10,/*time*/
				5,/*respawndelay*/
				new int[]{2092, 2093, 14856, 14913}/*rock ids*/
		),
		SILVER(442,
				20,/*level*/
				180,/*xp*/
				12,/*time*/
				8,/*respawndelay*/
				new int[]{2101, 2100, 11187}/*rock ids*/
		),
		ESSENCE(1436,
				30,/*level*/
				50,/*xp*/
				12,/*time*/
				- 1,/*respawndelay*/
				new int[]{2491,}/*rock ids*/
		),
		COAL(453,
				30,/*level*/
				300,/*xp*/
				14,/*time*/
				20,/*respawndelay*/
				new int[]{2096, 2097, 14850, 14851, 14850}/*rock ids*/
		),
		GOLD(444,
				40,/*level*/
				320,/*xp*/
				14,/*time*/
				14,/*respawndelay*/
				new int[]{2098, 2099}/*rock ids*/
		),
		MITHRIL(447,
				55,/*level*/
				480,/*xp*/
				18,/*time*/
				15,/*respawndelay*/
				new int[]{2102, 2103, 14855, 14853, 14854}/*rock ids*/
		),
		ADAMANTITE(449,
				70,/*level*/
				520,/*xp*/
				20,/*time*/
				15,/*respawndelay*/
				new int[]{2104, 2105,14862}/*rock ids*/
		),
		RUNITE(451,
				85,/*level*/
				600,/*xp*/
				30,/*time*/
				15,/*respawndelay*/
				new int[]{2106, 2107, 14859, 14860,}/*rock ids*/
		);

		Rock(int oreId, int level, int xp, int mineTime, int respawn, int[] rockIds) {
			this.oreId = oreId;
			this.level = level;
			this.xp = xp;
			this.mineTime = mineTime;
			this.respawn = respawn;
			this.rockIds = rockIds;
		}

		public int oreId;
		public int level;
		public int xp;
		public int mineTime;
		public int respawn;
		public int[] rockIds;
	}

	private Map<Integer, Rock> rockOres = new HashMap<Integer, Rock>();
	private static Map<Integer, Pickaxe> pickaxes = new HashMap<Integer, Pickaxe>();

	@Override
	public void init() throws FileNotFoundException {
		for(Rock rock : Rock.values()) {
			for(int i = 0; i < rock.rockIds.length; i++)
				rockOres.put(rock.rockIds[i], rock);
		}
		for(Pickaxe pic : Pickaxe.values()) {
			pickaxes.put(pic.pick, pic);
		}
		//miningTest();
	}

	@Override
	public int[] getValues(int type) {
		if(type == 6 || type == 7) {
			List<Integer> oreIds = new ArrayList<>();
			for(Rock rock : Rock.values()) {
				for(int i = 0; i < rock.rockIds.length; i++)
					oreIds.add(rock.rockIds[i]);
			}
			return ArrayUtils.fromList(oreIds);
		}
		return null;
	}

	private Map<Location, Integer> rockLocationStatus = new HashMap<Location, Integer>();

	@Override
	public boolean clickObject(Player player, int type, int rockId, int x, int y,
	                           int d) {
		if(type == 7) {
			prospect(player, rockId);
		} else {
			mine(player, rockId, x, y);
		}
		return true;
	}

	private void mine(final Player player, final int rockId, final int x, final int y) {
		final Location l = Location.create(x, y, 0);
		if(rockLocationStatus.get(l) != null && rockId != 2491) {
			return;
		}
		if(player.getExtraData().get("minetimer") != null)
			return;
		final Rock rock = rockOres.get(rockId);
		if(rock == null)
			return;
		if(rock.level > ContentEntity.getLevelForXP(player, Skills.MINING)) {
			ContentEntity.sendMessage(player, "You need " + rock.level + " to mine this rock.");
			return;
		}
		if(ContentEntity.freeSlots(player) == 0) {
			ContentEntity.sendMessage(player,
					"There is not enough space in your inventory.");
			return;
		}
		final Pickaxe pick = hasPickaxe(player);
		if(pick == null) {
			ContentEntity.sendMessage(player, "You need a pickaxe which you can use to mine this rock.");
			return;
		}
		ContentEntity.startAnimation(player, pick.anim);
		ContentEntity.turnTo(player, x, y);
		ContentEntity.sendMessage(player, "You swing your pick at the rock...");
		player.setBusy(true);
		final long timeNow = System.currentTimeMillis();
		player.getExtraData().put("minetimer", timeNow);
		int cycle2 = (rock.mineTime - pick.value - (ContentEntity.returnSkillLevel(player, Skills.MINING) / 10));
		if(cycle2 <= 1)
			cycle2 = 2;
		final int cycle = cycle2;
		World.getWorld().submit(new Event(1000) {
			int cycleCount = cycle;

			@Override
			public void execute() {
				if(! player.isBusy()) {
					this.stop2();
					return;
				}
				if(ContentEntity.freeSlots(player) == 0) {
					ContentEntity.sendMessage(player,
							"There is not enough space in your inventory.");
					stop2();
					return;
				}
				if(player.getRandomEvent().skillAction(4)) {
					this.stop2();
					return;
				}
				if(cycleCount > 0) {
					cycleCount--;
					ContentEntity.startAnimation(player, pick.anim);
					return;
				} else {
					ContentEntity.startAnimation(player, - 1);
					player.getAchievementTracker().itemSkilled(Skills.MINING, rock.oreId, 1);
					ContentEntity.addItem(player, rock.oreId);
					int xp = rock.xp * EXPMULTIPLIER;
					if(pick == Pickaxe.DRAGON)
						xp *= 1.1;
					ContentEntity.addSkillXP(player, xp, Skills.MINING);
					if(rock.respawn > 0 && rockId != 2491) {
						if(Edgeville.LOCATION.distance(Location.create(x, y, 0)) > 50 && Location.create(3370, 3240, 0).distance(Location.create(x, y, 0)) > 70) {
							final GameObject blank_rock = new GameObject(GameObjectDefinition.forId(450), l, 10, 0);
							final GameObject new_rock = new GameObject(GameObjectDefinition.forId(rockId), l, 10, 0);
							ObjectManager.addObject(blank_rock);
							rockLocationStatus.put(l, 1);
							World.getWorld().submit(new Event(rock.respawn * 1000) {
								@Override
								public void execute() {
									ObjectManager.replace(blank_rock, new_rock);
									rockLocationStatus.remove(l);
									this.stop();
								}
							});
						}
					}
					if(rockId != 2491)
						stop2();
					else
						cycleCount = cycle;
				}
			}

			public void stop2() {
				player.getExtraData().remove("minetimer");
				ContentEntity.startAnimation(player, - 1);
				this.stop();
			}
		});
	}

	public void miningTest() {
		for(Rock rock : Rock.values()) {
			for(int i = 1; i < 7; i++) {
				for(int i2 = 1; i2 < 10; i2++) {
					System.out.println("mining time pick: " + i + " level: " + i2 + " rock: " + rock.toString() + " time: " +
							"" + (rock.mineTime - i - i2));
				}
			}
		}
	}

	private void prospect(Player player, int rockId) {

	}

}
