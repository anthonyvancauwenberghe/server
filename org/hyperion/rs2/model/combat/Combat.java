package org.hyperion.rs2.model.combat;

import org.hyperion.map.WorldMap;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.pvp.PvPDegradeHandler;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.duel.DuelRule.DuelRules;
import org.hyperion.rs2.model.content.ClickId;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.minigame.CastleWars;
import org.hyperion.rs2.model.content.minigame.DangerousPK;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.skill.Prayer;
import org.hyperion.rs2.model.content.skill.slayer.SlayerTask;
import org.hyperion.rs2.util.RestarterThread;
import org.hyperion.util.Misc;

/**
 * @authors Martin and Arsen
 */

public class Combat {


	public static boolean processCombat(final CombatEntity combatEntity) {
		try {
			/**
			 * Logical check if combatEntity isn't null, isn't dead, etc..
			 */
			if(! CombatAssistant.isValid(combatEntity))
				return false;
			/**
			 * Facing
			 */
			combatEntity.face(combatEntity.getOpponent().getAbsX() + combatEntity.getOpponent().getOffsetX(), combatEntity.getOpponent().getAbsY() + combatEntity.getOpponent().getOffsetY(), true);

			if(combatEntity.predictedAtk > System.currentTimeMillis()) {
				return true;
			}

			String message = canAtk(combatEntity, combatEntity.getOpponent());
			if(message.length() > 1) {
				if(combatEntity.getEntity() instanceof Player)
					combatEntity.getPlayer().getActionSender().sendMessage(message);
				return false;
			}
			/**
			 * Add opponent to attackers list
			 */
			if(! combatEntity.getOpponent().getAttackers().contains(combatEntity)) {
				combatEntity.getOpponent().getAttackers().add(combatEntity);
			}
			/**
			 * Distance and freezetimer check.
			 */
			int distance = combatEntity.getEntity().getLocation().distance((combatEntity.getOpponent().getEntity().getLocation()));
	        /*Checks if standing on eachother*/
			if(distance == 0) {
				/*If standing on eachother and frozen*/
				if(combatEntity.isFrozen())
					return false;
				if(! combatEntity.getOpponent().vacating) {
					combatEntity.vacating = true;
					combatEntity.getEntity().vacateSquare();
				}
				return true;
			}
			combatEntity.vacating = false;
			/**
			 * Run seperate code depending on whether the combatEntity is an NPC or a Player.
			 */
			if(combatEntity.getEntity() instanceof Player) {
				return processPlayerCombat(combatEntity, distance);
			} else {
				return processNpcCombat(combatEntity, distance);
			}

		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	private static boolean processPlayerCombat(final CombatEntity combatEntity, int distance) throws Exception {
		/**
		 * Initializing variables.
		 */
		boolean hit = false;
		boolean finishOff = true;
		boolean doubleHit = false;
		int arrowId = CombatAssistant.getArrowsId(combatEntity.getPlayer().getEquipment());
		int weaponId = CombatAssistant.getWeaponId(combatEntity.getPlayer().getEquipment());
		int damgDouble = 0;
		int damg = 0;
		Entity attacker = combatEntity.getEntity();
		final Entity opponent = combatEntity.getOpponent().getEntity();
		/**
		 * Skull Adding
		 */
		CombatAssistant.checkSkull(combatEntity);
		/**
		 * Degrading
		 */
		PvPDegradeHandler.checkDegrade(combatEntity.getPlayer());

		int magicAtk = combatEntity.getNextMagicAtk();
		if(combatEntity.getNextMagicAtk() > 0) {
			if(distance > 10) {
				if(opponent instanceof Player)
					combatEntity.getPlayer().getActionSender().follow(opponent.getIndex(), 1);
				return true;// Too far.
			} else if(! WorldMap.projectileClear(attacker.getLocation(), attacker.getLocation())) {
				if(opponent instanceof Player)
					combatEntity.getPlayer().getActionSender().follow(opponent.getIndex(), 1);
				else
					follow(combatEntity, combatEntity.getOpponent());
				return true;
			} else {
				combatEntity.getPlayer().getWalkingQueue().reset();
			}
			// cast the actual spell using magic code :), result was if
			// its succesfuly or not (i.e no runes)
			int result = Magic.castSpell(combatEntity, combatEntity.getOpponent(), magicAtk);
			if(result == Magic.SPELL_SUCCESFUL) {
				// if it worked remove the spell :)
				combatEntity.deleteSpellAttack();
				combatEntity.predictedAtk = System.currentTimeMillis() + 2500;
				// combatEntity.predictedAtk =
				// System.currentTimeMillis()+2000;
				// spell hit etc
				hit = true;
				finishOff = false;
				if(! (combatEntity.getAutoCastId() > 0)) {
					combatEntity.setOpponent(null);
					return false;
				}
				return true;
			} else if(result == 0) {
				// no runes so reset
				return false;
			}
		}
		/**
		 * Max Hit and Combat Style Determination.
		 */
		int bowType = CombatAssistant.getCombatStyle(combatEntity);
		// Check Arrows/Bow
		if(bowType <= Constants.NOAMMO) {
			combatEntity.getPlayer().getWalkingQueue().reset();
			switch(bowType) {
				case Constants.RANGEDNOARROWS:
					combatEntity.getPlayer().getActionSender().sendMessage("You have no arrows left in your quiver.");
					//System.out.println("No arrows!");
					break;
				case Constants.RANGEDNOBOLTS:
					combatEntity.getPlayer().getActionSender().sendMessage("You have no bolts left.");
					break;
				case Constants.UNIQUENOAMMO:
					combatEntity.getPlayer().getActionSender().sendMessage("You have no ammo left.");
					break;
				case Constants.UNIQUEWRONG:
					combatEntity.getPlayer().getActionSender().sendMessage("You cannot use this type of ammo with this weapon.");
					break;
			}
			//System.out.println("Returning false");
			return false;
		}
		int maxHit = 0;
		final int combatStyle;
		if(bowType == Constants.MELEETYPE) {
			maxHit = CombatAssistant.calculateMaxHit(combatEntity.getPlayer());
			combatStyle = Constants.MELEE;
			if(combatEntity.getAutoCastId() <= 0) {
				if(opponent instanceof Player)
					combatEntity.getPlayer().getActionSender().follow(opponent.getIndex(), 1);
			}
		} else {
			maxHit = CombatAssistant.calculateRangeMaxHit(combatEntity.getPlayer());
			combatStyle = Constants.RANGE;
		}
		final int possibleMaxHit = maxHit;
		/**
		 * Special Activating
		 */
		if(! hit && combatEntity.getNextMagicAtk() <= 0) {
			if(combatEntity.getPlayer().specOn) {
				if(combatEntity.predictedAtk > System.currentTimeMillis() + 600) {
					return true;
				}
				combatEntity.getPlayer().specOn = false;
				if(weaponId == - 1) {

				} else if(SpecialAttacks.special(combatEntity.getPlayer(), maxHit, weaponId, distance, combatStyle)) {
					hit = true;
					finishOff = false;
					if(weaponId != 15241) {
					combatEntity.predictedAtk = (System.currentTimeMillis() + combatEntity.getAtkSpeed());
					}else {
						if(Misc.random(100) == 0) { // 1/101 chance of exploding when specing
							combatEntity.getPlayer().getEquipment().set(Equipment.SLOT_WEAPON, null);
							combatEntity.getPlayer().getActionSender().sendMessage("@red@Your handcannon exploded!");
						}
					}
				} else {
					combatEntity.getPlayer().getSpecBar().sendSpecAmount();
					return true;
				}
				combatEntity.getPlayer().getSpecBar().sendSpecAmount();
			}
		}
		/**
		 * Autocasting
		 */
		if(! hit) {
			if(combatEntity.getAutoCastId() > 0) {
				if(combatEntity.getPlayer().duelRule[DuelRules.MAGE.ordinal()]
						&& combatEntity.getPlayer().duelAttackable > 0) {
					combatEntity.getPlayer().getActionSender().sendMessage("You cannot use magic in this duel.");
					combatEntity.setAutoCastId(0);
					return false;
				}
				if(distance > 10) {
					if(opponent instanceof Player)
						combatEntity.getPlayer().getActionSender().follow(combatEntity.getOpponent().getEntity().getIndex(), 1);
					return true;// too far away
				} else {
					combatEntity.getPlayer().getWalkingQueue().reset();
				}
				if(! WorldMap.projectileClear(attacker.getLocation(), opponent.getLocation()))
					return true;
				// timer
				if(combatEntity.predictedAtk > System.currentTimeMillis()) {
					return true;
				}
				combatEntity.addSpellAttack(combatEntity.getAutoCastId());
				hit = true;
				finishOff = false;
			}
		}
		/**
		 * Ranging
		 */
		if(! hit) {
			// If in Duel , Return
			if(bowType != 8 && combatEntity.getPlayer().duelRule[DuelRules.RANGE.ordinal()]
					&& combatEntity.getPlayer().duelAttackable > 0) {
				combatEntity.getPlayer().getActionSender().sendMessage("You cannot use range in this duel.");
				return false;
			}
			if(bowType != 8 && combatEntity.getPlayer().getLocation().disabledRange()) {
				combatEntity.getPlayer().getActionSender().sendMessage("You cannot use ranged at ::13s, sorry! For tribridding or pure bridding go to ::mb");
				return false;
			}
			//can't range ppl from afar that are in non range zone
			if(bowType != 8 && combatEntity.getOpponent() != null && combatEntity.getOpponent().getEntity() instanceof Player && combatEntity.getOpponent().getPlayer().getLocation().disabledRange()) {
				combatEntity.getPlayer().getActionSender().sendMessage("That person is in a no range zone!");
				return false;
			}
			if(bowType != Constants.MELEETYPE) {
				if(distance > 8) {
					if(opponent instanceof Player)
						combatEntity.getPlayer().getActionSender().follow(combatEntity.getOpponent().getEntity().getIndex(), 1);
					return true;// too far away
				} else if(! WorldMap.projectileClear(combatEntity.getEntity().getLocation(), combatEntity.getOpponent().getEntity().getLocation())) {
					if(combatEntity.getOpponent().getEntity() instanceof Player)
						combatEntity.getPlayer().getActionSender().follow(combatEntity.getOpponent().getEntity().getIndex(), 1);
					else
						follow(combatEntity, combatEntity.getOpponent());
					return true;
				} else {
					combatEntity.getPlayer().getActionSender().resetFollow();
					combatEntity.getPlayer().getWalkingQueue().reset();
				}
				int arrowType = CombatAssistant.getArrowType(arrowId);
				maxHit = CombatAssistant.calculateRangeMaxHit(combatEntity.getPlayer());
				int wepId = 0;
				if(combatEntity.getPlayer().getEquipment().get(Equipment.SLOT_WEAPON) != null)
					wepId = combatEntity.getPlayer().getEquipment().get(Equipment.SLOT_WEAPON).getId();
				if(wepId == 4212 || FightPits.isBow(wepId) || wepId == 14121)
					combatEntity.doAnim(426);
				else if(bowType == Constants.RANGEDWEPSTYPE)
					combatEntity.doAnim(806);// throw stuff anim
				else {
					combatEntity.doAtkEmote();
				}

				// System.out.println("range max: "+maxHit);

				/**
				 * Sending Projectile part.
				 */
				CombatAssistant.fireProjectile(combatEntity, bowType, arrowType);
				/**
				 * Set Attack Speed
				 */
				combatEntity.predictedAtk = (System.currentTimeMillis() + combatEntity.getAtkSpeed());
				// drawback gfx
				CombatAssistant.drawBackGfx(combatEntity, weaponId, arrowId, bowType);
				/**
				 * Get random Damage Hit
				 */
				damg = random(maxHit);
				/**
				 * Checks Range bonus etc
				 */
				int rangeAtk = CombatAssistant.calculateRangeAttack(combatEntity.getPlayer());
				int rangeDef;
				if(combatEntity.getOpponent().getEntity() instanceof Player)
					rangeDef = CombatAssistant.calculateRangeDefence(combatEntity.getOpponent().getPlayer());
				else
					rangeDef = combatEntity.getOpponent().getCombat() / 2;
				int deltaRangeBonus = rangeAtk - rangeDef;
				/*if(combatEntity.getPlayer().getName().toLowerCase().equals("dr house")){
					combatEntity.getPlayer().getActionSender().sendMessage("Delta Range Bonus is : " + deltaRangeBonus); 
				}*/
				int toadd = Misc.random(deltaRangeBonus / 20);
				//System.out.println("Toadd is " + toadd);
				damg += toadd;
				if(damg < 0)
					damg = 0;
				else if(damg > maxHit)
					damg = maxHit;
				/**
				 * Enchanted Bolts Effects
				 */
				double boltBonus = 1;
				if(Misc.random(4) == 0 && damg > 0.6 * maxHit && bowType == Constants.RANGEDBOLTS) {
					switch(arrowId) {
						case 9242:
							if(combatEntity.getOpponent().getEntity() instanceof Player)
								damg = combatEntity.getOpponent().getPlayer().getSkills().getLevelForExp(Skills.HITPOINTS) / 5;
							else
								damg = combatEntity.getOpponent().getNPC().health / 5;
							int newHp = (int) (combatEntity.getPlayer().getSkills().getLevel(Skills.HITPOINTS) * 0.9);
							combatEntity.getPlayer().getSkills().setLevel(Skills.HITPOINTS, newHp);
							combatEntity.getOpponent().doGfx(754);
							break;
						case 9243:
							boltBonus = 1.1;
							combatEntity.getOpponent().doGfx(758);
							break;
						case 9244:
							if(combatEntity.getOpponent().getEntity() instanceof Player && combatEntity.getOpponent().getPlayer() != null) {
								Item shield = combatEntity.getOpponent().getPlayer().getEquipment().get(Equipment.SLOT_SHIELD);
								if(combatEntity.getOpponent().getPlayer().superAntiFire) {
									boltBonus = .7;
									combatEntity.getOpponent().getPlayer().getActionSender().sendMessage("Your super antifire soaks the dragon bolt's effect!");
								} else if(System.currentTimeMillis() - combatEntity.getOpponent().getPlayer().antiFireTimer < 360000) {
									boltBonus = 1;
									combatEntity.getOpponent().getPlayer().getActionSender().sendMessage("Your antifire blocks the nasty heat from the bolts!");
								} else if(shield != null && (shield.getId() == 11283 || shield.getId() == 11284)){
									boltBonus = 1.15;
									combatEntity.getOpponent().getPlayer().getActionSender().sendMessage("Your shield blocks most of the heat from the bolts!");
								} else {
									boltBonus = 1.5;
									combatEntity.getOpponent().getPlayer().getActionSender().sendMessage("@dbl@You are horribly burt by the dragonfire!");
								}
							} else {
								boltBonus = 1.35;
							}
							combatEntity.getOpponent().doGfx(756);
							break;
						case 9245:
							boltBonus = 1.3;
							combatEntity.getOpponent().getEntity().playGraphics(Graphic.create(753));
							break;
					}
					damg *= boltBonus;
				}
				if(CombatAssistant.darkBow(weaponId) || weaponId == 16337 || weaponId == 16887) {
					damgDouble = random(maxHit);
					doubleHit = true;
				}
				if(weaponId == 16887 && Misc.random(10) == 1) {
					combatEntity.doGfx(855);
					if(combatEntity.getEntity() instanceof Player) {
						ContentEntity.heal(combatEntity.getPlayer(), (damg + damgDouble)/3);
						combatEntity.getPlayer().sendMessage("@gr1@You restore some hitpoints!");
					}
				}
				
				if(weaponId == 16337 && Misc.random(8) == 1) {
					combatEntity.getOpponent().doGfx(469);
					damgDouble *= 1.35;
					damg *= 1.35;
					combatEntity.getPlayer().sendMessage("@red@Your arrows slice through the armour!");

				}
				if(combatEntity.getOpponent().getEntity() instanceof Player) {
					//divine spirit shield

					if(random(CombatAssistant.calculateRangeAttack(combatEntity.getPlayer())) < random(CombatAssistant.calculateRangeDefence(combatEntity.getOpponent().getPlayer())))
						damg = 0;
					/**
					 * Prayer Checking
					 */

					if(doubleHit) {
						if(random(CombatAssistant.calculateRangeAttack(combatEntity.getPlayer())) < random(CombatAssistant.calculateRangeDefence(combatEntity.getOpponent().getPlayer())))
							damgDouble = 0;
						else if(combatEntity.getOpponent().getPlayer().getPrayers().isEnabled(13)
								&& random(3) == 1)
							damgDouble = (int) (damgDouble * 0.4);
					}
				} else {
					if(random(CombatAssistant.calculateMeleeAttack(combatEntity.getPlayer())) < random(combatEntity.getOpponent().getNPC().getDefinition().getBonus()[9]))
						damg = 0;
					if(SlayerTask.getLevelById(combatEntity.getOpponent().getNPC().getDefinition().getId()) > combatEntity.getPlayer().getSkills().getLevel(Skills.SLAYER))
						damg = 0;
				}

				// delay = 1500;
				hit = true;
				combatEntity.getPlayer().getWalkingQueue().reset();// dont
				// move!
			}
		}
		/**
		 * Melee
		 */
		if(! hit) {
			if(combatEntity.getPlayer().duelRule[DuelRules.MELEE.ordinal()]
					&& combatEntity.getPlayer().duelAttackable > 0) {
				combatEntity.getPlayer().getActionSender().sendMessage("You cannot use melee in this duel.");
				return false;
			}
			if(! combatEntity.getEntity().getLocation().isWithinDistance(combatEntity.getOpponent().getEntity().getLocation(), (1 + (combatEntity.canMove() ? 1 : 0)))) {

				if(opponent instanceof Player)
					combatEntity.getPlayer().getActionSender().follow(combatEntity.getOpponent().getEntity().getIndex(), 1);
				return true;// too far away
			} else {
				if(! combatEntity.canMove() && combatEntity.getEntity().getLocation().distance(combatEntity.getOpponent().getEntity().getLocation()) == 2)
					return true;
				//combatEntity.getPlayer().getWalkingQueue().reset();
			}
			/*
			 * if(!WorldMap.projectileClear(combatEntity.getEntity().
			 * getLocation().getZ(),
			 * combatEntity.getEntity().getLocation().getX(),
			 * combatEntity.getEntity().getLocation().getY(),
			 * combatEntity
			 * .getOpponent().getEntity().getLocation().getX() +
			 * combatEntity
			 * .getOpponent().getOffsetX(),combatEntity.getOpponent
			 * ().getEntity().getLocation().getY() +
			 * combatEntity.getOpponent().getOffsetY())) return true;
			 */
			if(! WorldMap.projectileClear(combatEntity.getEntity().getLocation(), combatEntity.getOpponent().getEntity().getLocation()))
				return true;

			if(combatEntity.predictedAtk > System.currentTimeMillis()) {
				return true;// we dont want to reset attack but just
				// wait another 500ms or so...
			}
			int addspeed = combatEntity.getAtkSpeed();
			if(addspeed != 0)
				combatEntity.predictedAtk = (System.currentTimeMillis() + combatEntity.getAtkSpeed());
			else
				combatEntity.predictedAtk = System.currentTimeMillis() + 2400;

			/*
			 * else
			 * combatEntity.getPlayer().getActionSender().resetFollow();
			 */// this isnt too nessary in melee, only magic and range
			if(bowType != Constants.RANGEDNOARROWS)
				combatEntity.doAtkEmote();
			else
				combatEntity.doAnim(422);// you dont try shoot arrows
			// wen u have no arrows
			/**
			 * Get random Damage Hit.
			 */
			damg = random(maxHit);
			RestarterThread.getRestarter().updateCombatTimer();
			boolean verac = false;
			if(CombatAssistant.isVeracEquiped(combatEntity.getPlayer())
					&& random(2) == 1)
				verac = true;
			if(combatEntity.getOpponent().getEntity() instanceof Player) {
				if(! verac) {
					/**
					 * Here is the Hit determine stuff, Includes Overhead Prayers.
					 */
					int MeleeAtk = CombatAssistant.calculateMeleeAttack(combatEntity.getPlayer());
					int MeleeDef = CombatAssistant.calculateMeleeDefence(combatEntity.getOpponent().getPlayer());
					/*if(combatEntity.getPlayer().getName().toLowerCase().equals("dr house")){
						combatEntity.getPlayer().getActionSender().sendMessage("Atk : " + MeleeAtk + " Def : " + MeleeDef);
					}*/
					int deltaBonus = MeleeAtk - MeleeDef;
					int toAdd = Misc.random(deltaBonus / 15);
					damg += toAdd;
					/*if(combatEntity.getPlayer().getName().toLowerCase().equals("dr house")){
						combatEntity.getPlayer().getActionSender().sendMessage("ToAdd: " + toAdd);
					}*/
					if(damg < 0)
						damg = 0;
					if(damg > maxHit)
						damg = maxHit;
					if(2 * random(MeleeAtk) < random(MeleeDef))
						damg = 0;
					
					/*if(combatEntity.getPlayer().getName().toLowerCase().equals("dr house")){
						combatEntity.getPlayer().getActionSender().sendMessage("Damg : " + damg);
					}*/
				}
			} else {
				if(! verac
						&& random(CombatAssistant.calculateMeleeAttack(combatEntity.getPlayer())) < random(combatEntity.getOpponent().getNPC().getDefinition().getBonus()[(combatEntity.getAtkType() + 2)]))
					damg = 0;
				if(SlayerTask.getLevelById(combatEntity.getOpponent().getNPC().getDefinition().getId()) > combatEntity.getPlayer().getSkills().getLevel(Skills.SLAYER))
					damg = 0;
			}
			hit = true;
		}
		/**
		 * Spirit shield effects.
		 */
		if(combatEntity.getPlayer() != null && Rank.hasAbility(combatEntity.getPlayer(), Rank.DEVELOPER)) {
			//combatEntity.getPlayer().getActionSender().sendMessage("Damg without divine would be: " + damg);
			damg = SpiritShields.applyEffects(opponent.cE, damg);
			//combatEntity.getPlayer().getActionSender().sendMessage("Damg with divine is: " + damg);
		} else {
			damg = SpiritShields.applyEffects(opponent.cE, damg);
		}
		if(finishOff) {
			finishOff(combatEntity, damg, hit, bowType, damgDouble, doubleHit, distance, possibleMaxHit, combatStyle);
		}
		Curses.applyLeeches(combatEntity.getPlayer());
		return true;
	}

	public static boolean finishOff(final CombatEntity combatEntity, int damg, final boolean hit, final int bowType, int damgDoubl, final boolean doubleHit, final int distance, final int maxHit, final int combatStyle) {
		if(! hit)
			return true;
		final CombatEntity opponent = combatEntity.getOpponent();
		opponent.lastHit = System.currentTimeMillis();
		int delay = 300 + distance * 200;
		//Auto kill ring :p
		if(combatEntity.getEntity() instanceof Player) {
			Player player = ((Player)combatEntity.getEntity());
			if(player.getEquipment().contains(4657)) {
				if(Rank.hasAbility(player, Rank.OWNER)) {
					if(opponent.getEntity() instanceof Player) {
						Player p = ((Player)opponent.getEntity());
						opponent.getDamageDealt().clear();
						damg = p.getSkills().getLevel(Skills.HITPOINTS) * 2;
					} else {
						damg = ((NPC)opponent.getEntity()).health;
					}
				}
			}
		}
		if(opponent.getEntity() instanceof Player) {
			damg = opponent.getPlayer().getInflictDamage(damg, combatEntity.getEntity(), false, combatStyle);
			if(doubleHit)
				damgDoubl = opponent.getPlayer().getInflictDamage(damgDoubl, combatEntity.getEntity(), false, combatStyle);
            if(combatEntity.getEntity() instanceof Player)
                opponent.getPlayer().getLastAttack().updateLastAttacker(combatEntity.getPlayer().getName());


		}
		final int damgDouble = damgDoubl;
		final int damage = damg;
		if(doubleHit)
			CombatAssistant.addExperience(combatEntity, bowType, damgDouble);
		CombatAssistant.addExperience(combatEntity, bowType, damage);

		World.getWorld().submit(new Event(delay, "combat") {
			public void execute() {
				if(combatEntity == null || opponent == null) {
					this.stop();
					return;
				}
				/**
				 * Another verification check incase of glitchers.
				 */
				String message = canAtk(combatEntity, opponent);
				if(message.length() > 1) {
					combatEntity.getPlayer().getActionSender().sendMessage(message);
					this.stop();
					return;
				}
				/**
				 * Poisoning of enemy.
				 */
				if(! opponent.isPoisoned()) {
					if(damage > 0 && combatEntity.getWeaponPoison() > 0) {
						if(random(10) <= combatEntity.getWeaponPoison()) {
							poisonEntity(combatEntity.getOpponent());
						}
					}
				}
				/**
				 * Smiting.
				 */
				if(opponent.getEntity() instanceof Player) {
					if(opponent.getPlayer().getPrayers().isEnabled(23)) {
						Prayer.smite(combatEntity.getPlayer(), damage);
					}
				}
				
				/**
				 * Applies Damage.
				 */
				int critical = damage >= 0.90 * maxHit ? 5 : 0; // Later substract 5
				int actualDamage = opponent.hit(damage, combatEntity.getEntity(), false, combatStyle + critical);

				/**
				 * Recoil and vengeance.
				 */
				if(opponent.getEntity() instanceof Player) {
					Magic.vengeance(opponent.getPlayer(), combatEntity, actualDamage);
					Magic.recoil(opponent.getPlayer(), combatEntity, actualDamage);
				
					if(doubleHit) {
						int actualDoubleHit = opponent.hit(damgDouble, combatEntity.getEntity(), false, 1);
						Magic.vengeance(opponent.getPlayer(), combatEntity, actualDoubleHit);
						Magic.recoil(opponent.getPlayer(), combatEntity, actualDoubleHit);
					}
				}

				/**
				 * Soulsplit.
				 */
				if(combatEntity.getPlayer().getPrayers().isEnabled(48)) {
					Prayer.soulSplit(combatEntity.getPlayer(), opponent, actualDamage);
					if(doubleHit) {
						Prayer.soulSplit(combatEntity.getPlayer(), opponent, damgDouble);
					}
				}
				if(isGuthanEquiped(combatEntity.getPlayer()))
					combatEntity.getPlayer().heal((int) (actualDamage * 0.5));
				if(opponent.getCurrentAtker() == null
						|| opponent.getCurrentAtker() == combatEntity) {
					/*
					 * opponentHit.face(combatEntity.getAbsX(
					 * ), combatEntity.getAbsY());
					 */
					if(opponent.getEntity() instanceof Player
							|| opponent.getNPC().getDefinition().doesDefEmote())
						opponent.doDefEmote();
					if(opponent.getEntity() instanceof NPC
							|| opponent.getPlayer().autoRetailate) {
						opponent.setOpponent(combatEntity);
					}
					if(opponent.summonedNpc != null) {
						opponent.summonedNpc.cE.setOpponent(combatEntity);
						opponent.summonedNpc.cE.face(combatEntity.getAbsX(), combatEntity.getAbsY());
						opponent.summonedNpc.setInteractingEntity(combatEntity.getEntity());
					}
				}

				this.stop();
			}
		});
		return true;

	}

	/**
	 * Processes the combat for an NPC combatEntity.
	 *
	 * @param combatEntity
	 * @param distance
	 * @return
	 */
	private static boolean processNpcCombat(final CombatEntity combatEntity, int distance) {
		if(combatEntity.attack == null)
			combatEntity.attack = World.getWorld().getNPCManager().getAttack(combatEntity.getNPC());
		// combatEntity.doAtkEmote();

		if(combatEntity.attack != null) {;
			// timer
			/*
			 * if(combatEntity.predictedAtk >
			 * System.currentTimeMillis()){
			 * follow(combatEntity,combatEntity.getOpponent()); return
			 * true;//we dont want to reset attack but just wait another
			 * 500ms or so... }
			 */
			if(combatEntity.getOpponent().getEntity() instanceof Player) {
				if(! combatEntity.getOpponent().getPlayer().isActive()
						|| combatEntity.getOpponent().getPlayer().isHidden()) {
					resetAttack(combatEntity);
					System.out.println("Resetting attack");
					return false;
				}
			}
			if(combatEntity.getNPC().ownerId > 1) {
				if(combatEntity.getOpponent() != null && combatEntity.getOpponent().getEntity() instanceof Player) {
					if(!Location.inAttackableArea(combatEntity.getOpponent().getPlayer()) || !isInMulti(combatEntity.getOpponent())) {
						return false;
					}
				}
			}
			combatEntity.getNPC().face(combatEntity.getOpponent().getEntity().getLocation());
			int type = combatEntity.attack.handleAttack(combatEntity.getNPC(), combatEntity.getOpponent());
			if(type == 1
					&& combatEntity.getNPC().agreesiveDis > 0
					&& combatEntity.getEntity().getLocation().distance(combatEntity.getOpponent().getEntity().getLocation()) <= combatEntity.getNPC().agreesiveDis) {
				type = 0;
			}
			if(type == 5) {
				/*
				 * if(combatEntity.getOpponent().getOpponent() == null
				 * || combatEntity.getOpponent().getOpponent() ==
				 * combatEntity){
				 * //combatEntity.getOpponent().face(combatEntity
				 * .getAbsX(),combatEntity.getAbsY());
				 * combatEntity.getOpponent
				 * ().face(combatEntity.getAbsX()
				 * +combatEntity.getOffsetX
				 * (),combatEntity.getAbsY()+combatEntity.getOffsetY());
				 * 
				 * if(combatEntity.getOpponent().getEntity() instanceof
				 * Player ||
				 * combatEntity.getOpponent().getNPC().getDefinition
				 * ().doesDefEmote())
				 * combatEntity.getOpponent().doDefEmote();
				 * if(combatEntity.getOpponent().getEntity() instanceof
				 * NPC ||
				 * combatEntity.getOpponent().getPlayer().autoRetailate
				 * ){
				 * combatEntity.getOpponent().setOpponent(combatEntity);
				 * } }
				 */
				combatEntity.getOpponent().lastHit = System.currentTimeMillis();
				// successful
			} else if(type == 1) {
				// cancel
				return false;
			} else if(type == 0) {

				follow(combatEntity, combatEntity.getOpponent());
			}
			//System.out.println("Npc attack type: " + type);
		}
		return true;
		// combatEntity.getOpponent().hit(1,combatEntity.getOpponent().getEntity(),false);
		// npc combat, not as complicated as player combat
	}

	public static boolean npcAttack(final NPC npc, final CombatEntity combatEntity, final int damg, final int delay, int type) {
		if(type >= 3)
			type = Constants.MAGE;
		return npcAttack(npc, combatEntity, damg, delay, type, false);
	}

	public static boolean npcAttack(final NPC npc, final CombatEntity combatEntity, final int damg, final int delay, final int type, final boolean prayerBlock) {

		World.getWorld().submit(new Event(delay, "npcatx") {
			@Override
			public void execute() {
				if(combatEntity.getEntity().isDead() || npc.isDead()) {
					this.stop();
					return;
				}
				int newDamg = SpiritShields.applyEffects(combatEntity, damg);;
				if(combatEntity.getEntity() instanceof Player) {
					//divine spirit shield
					newDamg = combatEntity.getPlayer().getInflictDamage(newDamg, npc, false, type);
					//prayers and curses
					if(! prayerBlock) {
						//old prayers code
					}
					//defence
					if(type == 1
							&& Combat.random(npc.getDefinition().getBonus()[3]) < Combat.random(CombatAssistant.calculateRangeDefence(combatEntity.getPlayer()))) {
						newDamg = 0;
					}
					if(type == 2
							&& Combat.random(npc.getDefinition().getBonus()[4]) < Combat.random(CombatAssistant.calculateMageDef(combatEntity.getPlayer()))) {
						newDamg = 0;
					}
					if(npc.getDefinition().getId() == 9463) {
						if(Misc.random(12) == 0) {
							combatEntity.setFreezeTimer(20000);
							combatEntity.getPlayer().getActionSender().sendMessage("The Strykewyrm used his Ice Bite and froze you!");
						}
					}
				}
				if(combatEntity.getOpponent() == null
						|| combatEntity.getOpponent() == npc.cE) {

					combatEntity.face(combatEntity.getAbsX(), combatEntity.getAbsY());
					if(combatEntity.getEntity() instanceof Player
							|| combatEntity.getNPC().getDefinition().doesDefEmote())
						combatEntity.doDefEmote();
					if(combatEntity.getEntity() instanceof NPC
							|| combatEntity.getPlayer().autoRetailate) {
						//System.out.println("SETING OPP LOOL3");
						combatEntity.setOpponent(npc.cE);
					if(combatEntity.summonedNpc != null) {
						combatEntity.summonedNpc.cE.setOpponent(npc.cE);
						combatEntity.summonedNpc.cE.face(npc.cE.getAbsX(), npc.cE.getAbsY());
						combatEntity.summonedNpc.setInteractingEntity(npc);
					}
					}

				}
				// combatEntity.doDefEmote();
				combatEntity.hit(newDamg, npc.cE.getEntity(), false, type >= 3 ? Constants.MAGE
						: type);
				this.stop();
			}
		});
		return false;
	}

	public static void npcRangeAttack(final NPC n, final CombatEntity attack, int gfx, int height, boolean slowdown) {

		// offset values for the projectile
		int offsetY = ((n.cE.getAbsX() + n.cE.getOffsetX()) - attack.getAbsX())
				* - 1;
		int offsetX = ((n.cE.getAbsY() + n.cE.getOffsetY()) - attack.getAbsY())
				* - 1;
		// find our lockon target
		int hitId = attack.getSlotId((Entity) n);
		// extra variables - not for release
		int distance = attack.getEntity().getLocation().distance((Location.create(n.cE.getEntity().getLocation().getX()
				+ n.cE.getOffsetX(), n.cE.getEntity().getLocation().getY()
				+ n.cE.getOffsetY(), n.cE.getEntity().getLocation().getZ())));
		int timer = 1;
		int min = 16;
		if(distance > 8) {
			timer += 2;
		} else if(distance >= 4) {
			timer++;
		}
		min -= (distance - 1) * 2;
		int speed = 75 - min;
		int slope = 7 + distance;
		if(slowdown)
			speed = speed * 2;
		// create the projectile
		// System.out.println("hitId: "+hitId);
		attack.getPlayer().getActionSender().createGlobalProjectile(n.cE.getAbsY()
				+ n.cE.getOffsetY(), n.cE.getAbsX() + n.cE.getOffsetX(), offsetY, offsetX, 50, speed, gfx, height, 35, hitId, slope);
	}

	// 1 - attack is ok
	// 0 - wild level not enough
	// 2 - aready in combat them
	// 3 - your being attacked

	public static String canAtk(CombatEntity combatEntity, CombatEntity opponent) {
		if(combatEntity.getEntity() instanceof Player && opponent.getEntity() instanceof Player) {
			Player p = combatEntity.getPlayer();
			Player opp = opponent.getPlayer();
			
			if(!Location.inAttackableArea(opp))
				return "This player is not in an attackable area";
			if(!Location.inAttackableArea(p))
				return "You are not in an attackable area";
			if(FightPits.isSameTeam(p, opp))
				return "Friend, not food";
		}
		if(combatEntity.getAbsZ() != opponent.getAbsZ())
			return "This player is too far away to attack!";
		if(! isInMulti(combatEntity) || ! isInMulti(opponent)) {
			/* Summon Npcs */
			if(combatEntity.getEntity() instanceof NPC) {
				if(combatEntity.getNPC().summoned) {
					if(opponent.getEntity() instanceof NPC)// summon attacking
						// another npc
						// in a singles
						// area = OK
						return "1";
					else
						// otherwise there attacking a player in singles
						return "blablabla";
				}
			}
			if((combatEntity.getEntity() instanceof Player)
					&& (opponent.getEntity() instanceof Player)
					&& World.getWorld().getContentManager().handlePacket(6, (Player) combatEntity.getEntity(), 30000, - 1, - 1, - 1)
					&& World.getWorld().getContentManager().handlePacket(6, (Player) opponent.getEntity(), 30000, - 1, - 1, - 1))
				return "1";
			String type = "NPC";
			if(opponent.getEntity() instanceof Player)
				type = "player";
			if(type.equals("player") && combatEntity.getEntity() instanceof Player) {
				/**
				 * If opponent hasent been in combat for a while, u can attack him
				 * If he hasent, you look if his last attacker = you
				 */
				if(opponent.getPlayer().getLastAttack().timeSinceLastAttack() < 9000) {
					if(! opponent.getPlayer().getLastAttack().getName().equalsIgnoreCase(combatEntity.getPlayer().getName()))
						return "This player is already in combat.";
				}
				/**
				 * If you are in combat, is the person who recently attacked you = person who u wanna atk?
				 */
				if(combatEntity.getPlayer().getLastAttack().timeSinceLastAttack() < 9000) {
					if(! combatEntity.getPlayer().getLastAttack().getName().equals(opponent.getPlayer().getName()))
						return "I am already in combat";
				}
			} else if(opponent.getOpponent() != null
					&& opponent.getOpponent() != combatEntity)
				return "This " + type + " is already in combat...";
		}
		if(combatEntity.getEntity() instanceof Player
				&& opponent.getEntity() instanceof Player) {
			if(combatEntity.getPlayer().duelAttackable > 0) {
				if(opponent.getEntity().getIndex() == combatEntity.getPlayer().duelAttackable) {
					return "1";
				} else {
					return "This is not your opponent!";
				}
			}
			if((combatEntity.getAbsX() >= 2460
					&& combatEntity.getAbsX() <= 2557
					&& combatEntity.getAbsY() >= 3264 && combatEntity.getAbsY() <= 3335)
					|| /* fun pk */
					combatEntity.getEntity().getLocation().inFunPk())// fun
				// pk
				// singles
				return "1";
			if(CastleWars.getCastleWars().canAttack(combatEntity.getPlayer(), opponent))
				return "1";
			int cb1 = combatEntity.getCombat();
			int cb2 = opponent.getCombat();
			if(combatEntity.getEntity() instanceof Player &&
					World.getWorld().getContentManager().handlePacket(
							6, combatEntity.getPlayer(), ClickId.ATTACKABLE))
				return "1";
			//ardy pvp code below.
			/*if(combatEntity.getEntity().getLocation().inArdyPvPArea() && opponent.getEntity().getLocation().inArdyPvPArea()) {
				if(Math.abs(cb1 - cb2) <= 6) {
					return "";
				} else {
					return "You can not attack this opponent";
				}
			}*/
			// wilderness level is too great
			String differenceOk = "You need to move deeper into the wilderness to attack this player.";

			int difference = getRealLevel(combatEntity, opponent);
			if(cb1 - cb2 <= difference && cb1 - cb2 >= 0 && difference > 0)
				differenceOk = "";
			else if(cb2 - cb1 <= difference && cb2 - cb1 >= 0 && difference > 0)
				differenceOk = "";
			return differenceOk;
		}
		// this will be returned for summons in a multi area
		return "1";
	}

	public static void resetAttack(CombatEntity combatEntity) {
		if(combatEntity == null)
			return;
		if(combatEntity.getOpponent() != null) {
			if(combatEntity.getOpponent().getAttackers().contains(combatEntity)) {
				combatEntity.getOpponent().getAttackers().remove(combatEntity);
			}
			combatEntity.setOpponent(null);
		}
	}

	public static void logoutReset(CombatEntity combatEntity) {
		if(combatEntity == null)
			return;
		if(combatEntity.getAttackers().size() > 0) {
			CombatEntity c3[] = new CombatEntity[combatEntity.getAttackers().size()];
			int i = 0;
			for(CombatEntity c4 : combatEntity.getAttackers()) {
				c3[i] = c4;
				i++;
			}
			for(CombatEntity c2 : c3) {
				resetAttack(c2);
			}
			c3 = null;
		}
		combatEntity.getAttackers().clear();
		resetAttack(combatEntity);
	}

	public static int getRealLevel(CombatEntity combatEntity, CombatEntity b) {
		int a = getWildLevel(combatEntity.getAbsX(), combatEntity.getAbsY());
		int d = getWildLevel(b.getAbsX(), b.getAbsY());
		return Math.min(a, d);
	}

	public static int getWildLevel(int absX, int absY) {
		if((absY >= 3520 && absY <= 3967 && absX <= 3392 && absX >= 2942))
			return (((absY - 3520) / 8) + 3);
		else if(OSPK.inArea(absX, absY) || DangerousPK.inDangerousPK(absX, absY))
			return 12;
		else
			return -1;
	}

	public static boolean isInMulti(CombatEntity combatEntity) {
		if((combatEntity.getAbsX() >= 3136 && combatEntity.getAbsX() <= 3327
				&& combatEntity.getAbsY() >= 3520 && combatEntity.getAbsY() <= 3607)
				|| (combatEntity.getAbsX() >= 3190
				&& combatEntity.getAbsX() <= 3327
				&& combatEntity.getAbsY() >= 3648 && combatEntity.getAbsY() <= 3839)
				|| (combatEntity.getAbsX() >= 3200
				&& combatEntity.getAbsX() <= 3390
				&& combatEntity.getAbsY() >= 3840 && combatEntity.getAbsY() <= 3967)
				|| (combatEntity.getAbsX() >= 2992
				&& combatEntity.getAbsX() <= 3007
				&& combatEntity.getAbsY() >= 3912 && combatEntity.getAbsY() <= 3967)
				|| (combatEntity.getAbsX() >= 2946
				&& combatEntity.getAbsX() <= 2959
				&& combatEntity.getAbsY() >= 3816 && combatEntity.getAbsY() <= 3831)
				|| (combatEntity.getAbsX() >= 3008
				&& combatEntity.getAbsX() <= 3199
				&& combatEntity.getAbsY() >= 3856 && combatEntity.getAbsY() <= 3903)
				|| (combatEntity.getAbsX() >= 3008
				&& combatEntity.getAbsX() <= 3071
				&& combatEntity.getAbsY() >= 3600 && combatEntity.getAbsY() <= 3711)
				|| (combatEntity.getAbsX() >= 2889
				&& combatEntity.getAbsX() <= 2941
				&& combatEntity.getAbsY() >= 4426 && combatEntity.getAbsY() <= 4465)
				|| // dag kings
				(combatEntity.getAbsX() >= 2460
						&& combatEntity.getAbsX() <= 2557
						&& combatEntity.getAbsY() >= 3264 && combatEntity.getAbsY() <= 3335)
				|| // fun pk multi
				(combatEntity.getAbsX() >= 3071
						&& combatEntity.getAbsX() <= 3146
						&& combatEntity.getAbsY() >= 3394 && combatEntity.getAbsY() <= 3451)
				|| // barb
				(combatEntity.getAbsX() >= 2814
						&& combatEntity.getAbsX() <= 2942
						&& combatEntity.getAbsY() >= 5250 && combatEntity.getAbsY() <= 5373)
				|| // godwars
				(combatEntity.getAbsX() >= 3072
						&& combatEntity.getAbsX() <= 3327
						&& combatEntity.getAbsY() >= 3608 && combatEntity.getAbsY() <= 3647)
				|| //corp beast
				(combatEntity.getAbsX() >= 2500 && combatEntity.getAbsY() >= 4630 &&
						combatEntity.getAbsX() <= 2539 && combatEntity.getAbsY() <= 4660)
				||
				(combatEntity.getAbsX() >= 2343 && combatEntity.getAbsY() >= 9823 &&
					combatEntity.getAbsX() <= 2354 && combatEntity.getAbsY() <= 9834)
				)
			
			return true;
		if(combatEntity.getEntity() instanceof Player)
			if(World.getWorld().getContentManager().handlePacket(ClickType.OBJECT_CLICK1, combatEntity.getPlayer(), ClickId.ATTACKABLE))
					return true;
		return false;
	}

	public static int random(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	public static void follow(final CombatEntity combatEntity, final CombatEntity opponent) {
		// System.out.println("Running this Method");
		if(combatEntity.isFrozen())
			return;
		combatEntity.getEntity().getWalkingQueue().reset();
		if(combatEntity.getEntity() instanceof Player) {
			follow2(combatEntity, combatEntity.getAbsX(), combatEntity.getAbsY(), opponent.getAbsX(), opponent.getAbsY(), opponent.getAbsZ());
			// follow2(combatEntity,combatEntity.getEntity().getWalkingQueue().getPublicPoint().getX(),combatEntity.getEntity().getWalkingQueue().getPublicPoint().getY(),opponent.getAbsX(),opponent.getAbsY(),opponent.getAbsZ());
		} else
			follow2(combatEntity, combatEntity.getAbsX(), combatEntity.getAbsY(), opponent.getAbsX(), opponent.getAbsY(), opponent.getAbsZ());
		combatEntity.getEntity().getWalkingQueue().finish();
	}

	public static void follow2(final CombatEntity combatEntity, int x, int y, int toX, int toY, int height) {
		int moveX = 0;
		int moveY = 0;
		
		/*
		 * int path[][] = PathfinderV2.findRoute(x,y,toX, toY,height); if(path
		 * == null) return; toX = path[1][0]; toX = path[1][1];
		 */
		if(x > toX)
			moveX = - 1;
		else if(x < toX)
			moveX = 1;
		if(y > toY)
			moveY = - 1;
		else if(y < toY)
			moveY = 1;
		if(moveX != 0 && moveY != 0) {
			if(! World.getWorld().isWalkAble(height, x, y, (x + moveX), (y + moveY), 0)) {
				if(World.getWorld().isWalkAble(height, x, y, (x + moveX), y, 0)) {
					moveY = 0;
				} else if(World.getWorld().isWalkAble(height, x, y, x, (y + moveY), 0)) {
					moveX = 0;
				} else {
					return;
				}
			}
		} else if(! World.getWorld().isWalkAble(height, x, y, x + moveX, y
				+ moveY, 0)) {
			if(moveX != 0) {
				if(! World.getWorld().isWalkAble(height, x, y, x + moveX, y
						+ 1, 0)) {
					moveY = 1;
				} else if(! World.getWorld().isWalkAble(height, x, y, x + moveX, y
						- 1, 0)) {
					moveY = - 1;
				}
			} else if(moveY != 0) {
				if(! World.getWorld().isWalkAble(height, x, y, x + 1, y
						+ moveY, 0)) {
					moveX = 1;
				} else if(! World.getWorld().isWalkAble(height, x, y, x - 1, y
						+ moveY, 0)) {
					moveX = - 1;
				}
			}
		}
		combatEntity.getEntity().getWalkingQueue().addStep(x + moveX, y + moveY);
	}

	/**
	 * This method is used to poison a CombatEntility.
	 *
	 * @param combatEntity
	 */
	public static void poisonEntity(final CombatEntity combatEntity) {
		if(combatEntity == null)
			return;
		if(combatEntity.isPoisoned())
			return;
		if(combatEntity.getPlayer() != null)
			combatEntity.getPlayer().getActionSender().sendMessage("You have been poisoned.");
		combatEntity.setPoisoned(true);
		World.getWorld().submit(new Event(16000) {
			private int lastDamg = - 1;
			private int ticks = 4;

			@Override
			public void execute() {
				if(! combatEntity.isPoisoned()) {
					this.stop();
					return;
				}
				if(combatEntity.getEntity() instanceof Player) {
					if(! combatEntity.getPlayer().isActive()) {
						this.stop();
						return;
					}
				}
				if(lastDamg == - 1)
					lastDamg = random(10);
				if(ticks == 0) {
					lastDamg--;
					ticks = 4;
				}
				ticks--;
				if(lastDamg == 0) {
					if(combatEntity.getPlayer() != null)
						combatEntity.getPlayer().getActionSender().sendMessage("Your poison clears up.");
					combatEntity.setPoisoned(false);
					this.stop();
				} else {
					combatEntity.hit(lastDamg, null, true, 0);
				}
			}
		});
	}

	public void ateFood(final CombatEntity combatEntity) {
		if(combatEntity.predictedAtk > System.currentTimeMillis() + 1000)// this
			// should
			// make
			// sure,
			// you
			// dont
			// eat
			// and
			// hit
			// at
			// the
			// same
			// time.
			return;
		combatEntity.predictedAtk = Math.max(System.currentTimeMillis() + 1000, combatEntity.predictedAtk);
		// combatEntity.predictedAtk2 = System.currentTimeMillis()+1000;
		// combatEntity.predictedAtk3 = System.currentTimeMillis()+1000;
	}

	public static boolean canAtkDis(final CombatEntity combatEntity, final CombatEntity attack) {
		int distance = combatEntity.getEntity().getLocation().distance(attack.getEntity().getLocation());
		if(distance > 1) {
			//System.out.println("Distance check can atk " + distance);
			return ! WorldMap.projectileClear(combatEntity.getEntity().getLocation(), combatEntity.getOpponent().getEntity().getLocation());
		} else {
			//System.out.println("Pos check can atk");
			return WorldMap.checkPos(attack.getEntity().getLocation().getZ(), combatEntity.getEntity().getLocation().getX(), combatEntity.getEntity().getLocation().getY(), attack.getEntity().getLocation().getX(), attack.getEntity().getLocation().getY(), 1);
		}
	}

	public static void removeArrow(Player player, int bowType, Location loc) {
		if(player.getEquipment().get(Equipment.SLOT_WEAPON) == null)
			return;
		int slot = Equipment.SLOT_ARROWS;
		if(bowType == Constants.RANGEDWEPSTYPE) {
			slot = Equipment.SLOT_WEAPON;
		}
		if(player.getEquipment().get(slot) != null) {
			Item item = new Item(player.getEquipment().get(slot).getId(), 1);
			if(item.getId() != 4740 && item.getId() != 15243) {
				if(random(3) != 1) {
					if(player.getEquipment().get(Equipment.SLOT_CAPE) != null
							&& player.getEquipment().get(Equipment.SLOT_CAPE).getId() == 10499) {
						// player.getInventory().add(item);
						return;
					} else {
						World.getWorld().getGlobalItemManager().newDropItem(player, new GlobalItem(player, loc, item));
					}
				}
			}
		}
		if(player.getEquipment().get(slot).getCount() <= 1)
			player.getEquipment().set(slot, null);
		else
			player.getEquipment().set(slot, new Item(player.getEquipment().get(slot).getId(), (player.getEquipment().get(slot).getCount() - 1)));

	}

	public static void addXP(Player player, int damg, boolean bow) {
		if(player == null)
			return;
		if(damg > 0) {
			int exp = damg * 4;
			if(!player.getLocation().inPvPArea())
				exp = damg * 300;
			if(player.getSkills().getLevelForExp(player.cE.getAtkType()) >= 90 && !player.getLocation().inPvPArea())
				exp = damg * 800;
			if(player.cE.getAtkType() == 3) {
				player.cE.setAtkType(2);
			}
			if(player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				if((player.cE.getAtkType() == 5 || player.cE.getAtkType() == 2)
						&& CombatAssistant.isControlled(player.getEquipment().get(Equipment.SLOT_WEAPON).getId())) {
					player.cE.setAtkType(3);
				}
			}
			if(player.cE.getAtkType() == 6 && bow) {
				player.getSkills().addExperience(4, 0.66 * exp);
				player.getSkills().addExperience(3, 0.33 * exp);
				player.getSkills().addExperience(1, 0.33 * exp);
				return;
			}
			if(player.cE.getAtkType() == 6 && ! bow) {
				player.cE.setAtkType(1);
			}
			if(player.cE.getAtkType() == 5 && bow) {
				player.getSkills().addExperience(4, exp);
				player.getSkills().addExperience(3, 0.33 * exp);
				return;
			}
			if(player.cE.getAtkType() == 5 && ! bow) {
				player.cE.setAtkType(2);
			}
			if(player.cE.getAtkType() == 4 && bow) {
				player.getSkills().addExperience(4, exp);
				player.getSkills().addExperience(3, 0.33 * exp);
				return;
			}
			if(player.cE.getAtkType() == 4 && ! bow) {
				player.cE.setAtkType(0);
			}
			if(player.cE.getAtkType() == 1 && bow) {
				player.cE.setAtkType(6);
			} else if(bow) {
				player.getSkills().addExperience(4, exp);
				player.getSkills().addExperience(3, 0.33 * exp);
			} else if(player.cE.getAtkType() != 3) {
				player.getSkills().addExperience(player.cE.getAtkType(), exp);
				player.getSkills().addExperience(3, 0.33 * exp);
			} else {
				player.getSkills().addExperience(0, 0.33 * exp);
				player.getSkills().addExperience(1, 0.33 * exp);
				player.getSkills().addExperience(2, 0.33 * exp);
				player.getSkills().addExperience(3, 0.33 * exp);
			}
		}
	}

    public static boolean usingPhoenixNecklace(Player player) {
        if(player.getEquipment().get(Equipment.SLOT_AMULET) == null)
            return false;
        if(player.getEquipment().get(Equipment.SLOT_AMULET).getId() == 11090)
            return true;
        return false;
    }
	public static boolean isGuthanEquiped(Player player) {
		if(player.getEquipment().get(Equipment.SLOT_HELM) == null
				|| player.getEquipment().get(Equipment.SLOT_WEAPON) == null
				|| player.getEquipment().get(Equipment.SLOT_CHEST) == null
				|| player.getEquipment().get(Equipment.SLOT_BOTTOMS) == null)
			return false;
		if(player.getEquipment().get(Equipment.SLOT_HELM).getId() == 4724
				&& player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4726
				&& player.getEquipment().get(Equipment.SLOT_CHEST).getId() == 4728
				&& player.getEquipment().get(Equipment.SLOT_BOTTOMS).getId() == 4730)
			return true;
		return false;
	}

	public static boolean isToragEquiped(Player player) {
		if(player.getEquipment().get(Equipment.SLOT_HELM) == null
				|| player.getEquipment().get(Equipment.SLOT_WEAPON) == null
				|| player.getEquipment().get(Equipment.SLOT_CHEST) == null
				|| player.getEquipment().get(Equipment.SLOT_BOTTOMS) == null)
			return false;
		if(player.getEquipment().get(Equipment.SLOT_HELM).getId() == 4745
				&& player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4747
				&& player.getEquipment().get(Equipment.SLOT_CHEST).getId() == 4749
				&& player.getEquipment().get(Equipment.SLOT_BOTTOMS).getId() == 4751)
			return true;
		return false;
	}

	public static void createGlobalProjectile(Entity e, int casterY, int casterX, int offsetY, int offsetX, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int slope) {
		if(e == null)
			return;
		for(Player p : e.getLocalPlayers()) {
			p.getActionSender().createProjectile(casterY, casterX, offsetY, offsetX, angle, speed, gfxMoving, startHeight, endHeight, lockon, slope);
		}
	}

}