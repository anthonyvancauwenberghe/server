package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.DeathDrops;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.SpecialBar;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.EloRating;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ClickId;
import org.hyperion.rs2.model.content.bounty.BountyPerkHandler;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.pvptasks.TaskHandler;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;


public class PlayerDeathEvent extends Event {
	
	private static final String[] KILLER_MESSAGES = new String[] {
		"You have wiped the floor with %s",
		"%s regrets the day that he met you.",
		"You rock, clearly %s does not",
		"You have sent %s to his grave",
		"All the kings horses and men could never put %s back together again...",
		"%s falls before your might.",
		"With a crushing blow %s's life is met with an untimely end",
		"You have ended %s's life abruptly",
		"The mysteries of life can no longer be discovered by %s",
		"The sword is obviously mightier than %s",
		"The death of %s is a burden you must bear",
		"%s must of dissapointed the gods..."
	};
	
	private static final String[] DEATH_MESSAGES = new String[] {
		"Oh dear! You are dead",
		"Death is a harsh mistress",
		"Life is fragile, you had to learn it the hard way",
		"Life is part of a cycle, yours is over",
		"The darkness of the afterlife awaits you...",
		"You're stupid... and dead"
	};


	private Player player;

	public static Location DEATH_LOCATION() {
		return Location.create(3221, 3218, 0);
	}

	/**
	 * Executes the death event for the dying player.
	 *
	 * @param player
	 */
	public PlayerDeathEvent(Player player) {
		super(500, "playerdeath");
		this.player = player;
		player.setDead(true);
	}

	private int timer = 0;

	@Override
	public void execute() {
		try {
			if(! player.isActive() || player.isHidden()) {
				this.stop();
				return;
			}
			if(Jail.inJail(player)) {
				player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevelForExp(Skills.HITPOINTS));
				this.stop();
				return;
			}
			switch(timer) {
				case 2:
					startDeath();
                    PlayerSaving.getSaving().save(player);
					break;
				case 9:
                    resetPlayer();
                    PlayerSaving.getSaving().save(player);
					break;
				case 11:
					player.playAnimation(Animation.create(- 1, 0));
					player.setDead(false);
                    PlayerSaving.getSaving().save(player);
					this.stop();
					break;
			}
			timer++;
		} catch(Exception e) {
			e.printStackTrace();
			this.stop();
		}
	}


	private void resetPlayer() {
		player.playAnimation(Animation.create(- 1, 0));
		for(int i = 0; i < Skills.SKILL_COUNT - 3; i++) {
			player.getSkills().setLevel(i, player.getSkills().getLevelForExp(i));
		}
		player.getSpecBar().setAmount(SpecialBar.FULL);
		player.specOn = false;
		player.teleBlockTimer = System.currentTimeMillis();
		player.getActionSender().resetFollow();
		player.getSpecBar().sendSpecAmount();
		player.getSpecBar().sendSpecBar();
		//CombatEntility ddd = entity.cE.getKiller();

		player.cE.setPoisoned(false);

		player.cE.setFreezeTimer(0);
		Player killer = player.cE.getKiller();
		if((player.duelAttackable > 0 || (killer != null && killer.duelAttackable > 0)) ||
				(Duel.inDuelLocation(killer) || Duel.inDuelLocation(player)) || player.hasDuelTimer())    //If dying in duel arena
			Duel.finishFullyDuel(player);
		else if(World.getWorld().getContentManager().handlePacket(6, player, ClickId.ATTACKABLE)) {
			if(World.getWorld().getContentManager().handlePacket(6, player, ClickId.FIGHT_PITS_DEATH))
			if(killer != null) //in fight pits death, reward player
				killer.getInventory().add(Item.create(391, 2));
		} else if(World.getWorld().getContentManager().handlePacket(6, player, 32000, - 1, - 1, - 1)) {
			World.getWorld().getContentManager().handlePacket(6, player, 32001, - 1, - 1, - 1);
		} else if(player.fightCavesWave > 0 && !player.getLocation().inPvPArea()) { //If dying in fight caves
			player.fightCavesWave = 0;
			player.getActionSender().showInterfaceWalkable(- 1);
			player.setTeleportTarget(Location.create(2439, 5171, 0), true);
			player.getActionSender().sendMessage("Too bad, you didn't complete fight caves!");
;
		} else {
			if(! player.getLocation().inFunPk()) {
				if(killer != null) {
					//blood lust system
					World.getWorld().getContentManager().handlePacket(6, player, 38000, killer.getClientIndex(), - 1, - 1);
                    World.getWorld().getBountyHandler().handle(killer, player.getName());
					/**
					 * Increasing stupid points and stuff.
					 */
					killer.sendMessage(sendKillMessage(player.getSafeDisplayName()));
					BountyPerkHandler.handleSpecialPerk(killer);
					if(true || killer.getLocation().inPvPArea()) {
						boolean isDev = false;
						if(Rank.getPrimaryRank(killer).ordinal() >= Rank.DEVELOPER.ordinal() 
								|| Rank.getPrimaryRank(player).ordinal() >= Rank.DEVELOPER.ordinal())
							isDev = true;
						if(!isDev) {
						    killer.increaseKillCount();
						    int oldKillerRating = killer.getPoints().getEloRating();
						    killer.getPoints().updateEloRating(player.getPoints().getEloRating(), EloRating.WIN);
						    player.getPoints().updateEloRating(oldKillerRating, EloRating.LOSE);
						}
                        if(killer.killedRecently(player.getName()) || killer.getShortIP().equalsIgnoreCase(player.getShortIP())) {
                            killer.getActionSender().sendMessage("You don't receive Pk Points/KillStreak for killing the same enemy twice.");
                        } else {
							try {
								if(killer.getPvPTask() != null)
									TaskHandler.checkTask(killer, player);
							} catch(Exception e) {
								System.err.println("PvP tasks error!");
								e.printStackTrace();
							}
							if(player.getKillCount() >= 10) {
								killer.increaseKillStreak();
							}
                            killer.getBountyHunter().handleBHKill(player);
                            killer.addLastKill(player.getName());
							int pointsToAdd = player.wildernessLevel / 4 + player.getBounty();
							if(player.getKillStreak() >= 6) {
								ActionSender.yellMessage("@blu@" + killer.getName() + " has just ended " + player.getSafeDisplayName() + "'s rampage of " + player.getKillStreak() + " kills.");
							}
							killer.getPoints().inceasePkPoints(pointsToAdd > 0 ? pointsToAdd : 5);
                            if(Rank.hasAbility(killer, Rank.SUPER_DONATOR))
                                killer.getSpecBar().increment(SpecialBar.FULL/5);
                            if(Rank.hasAbility(killer, Rank.DONATOR))
                                killer.getSpecBar().increment(SpecialBar.CYCLE_INCREMENT);
                            killer.getSpecBar().sendSpecBar();

                        }
						if(!isDev) {
						player.increaseDeathCount();
						player.resetKillStreak();
						player.resetBounty();
						}

					}
					//DeathDrops.dropAllItems(player, killer);
					DeathDrops.dropsAtDeath(player, killer);
				} else {
					DeathDrops.dropsAtDeath(player, player);
				}
			}
			player.setTeleportTarget(DEATH_LOCATION(), false);
			player.getActionSender().sendMessage(getDeathMessage());
		}
		player.setSkulled(false);
		player.resetPrayers();
	}
	
	private static String sendKillMessage(String name) {
		name = TextUtils.titleCase(name);
		return getMiscMessage(KILLER_MESSAGES, name);
	}
	
	private static String getDeathMessage() {
		return getMiscMessage(DEATH_MESSAGES, null);
	}
	
	private static String getMiscMessage(final String[] array, final String name) {
		final int rand = Misc.random(array.length - 1);
		return array[rand].replaceAll("%s", name);
	}

	private void startDeath() {
		player.playAnimation(Animation.create(0x900, 0));
		Combat.logoutReset(player.cE);
		player.cE.setPoisoned(false);
		player.getWalkingQueue().reset();
		player.isFollowing = null;
		player.cE.morrigansLeft = 0;		
	}


}