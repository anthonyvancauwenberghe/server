package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.achievements.AchievementHandler;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.EloRating;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ClickId;
import org.hyperion.rs2.model.content.bounty.BountyPerkHandler;
import org.hyperion.rs2.model.content.minigame.Bork;
import org.hyperion.rs2.model.content.minigame.LastManStanding;
import org.hyperion.rs2.model.content.minigame.barrowsffa.BarrowsFFA;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.pvptasks.TaskHandler;
import org.hyperion.rs2.model.content.skill.dungoneering.DungeoneeringManager;
import org.hyperion.rs2.model.content.specialareas.NIGGERUZ;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.rs2.model.content.specialareas.impl.PurePk;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;


public class PlayerDeathEvent extends Event {

    public static final Map<String, Queue<String>> kills = new HashMap<String, Queue<String>>();

    public static boolean isRecentKill(final Player killer, final Player player) {
        if(killer.equals(player))
            return true;
        if(kills.containsKey(killer.getShortIP())) {
            final Queue<String> recent = kills.get(killer.getShortIP());
            if(recent.size() == 3)
                recent.poll();
            if(recent.contains(player.getShortIP()))
                return true;
            recent.offer(player.getShortIP());
        } else {
            final Queue<String> queue = new ArrayBlockingQueue<>(3);
            queue.add(player.getShortIP());
            kills.put(killer.getShortIP(), queue);
        }
        return false;
    }

    public static int pkpToTransfer(final Player victim) {
        int base = 8;
        base += (int)(victim.wildernessLevel/1.5D);

        base += (int)(Math.pow(victim.getKillCount(), 0.765) - Math.pow(victim.getDeathCount(), 0.67));

        if(base > 150)
            base = 150;
        if(base < 8)
            base = 8;
        return base;
    }

    private static void handlePkpTransfer(final Player killer, final Player player, int original) {
        int toTransfer = pkpToTransfer(player);
        final PlayerPoints kP = player.getPoints();
        if(kP.getPkPoints() < toTransfer) {
            int deltaRemove = (toTransfer - kP.getPkPoints() + 10)/9;
            int remove = player.getBank().remove(Item.create(5020, deltaRemove));
            kP.increasePkPoints((remove * 10), false);
        }

        if(kP.getPkPoints() < toTransfer)
            toTransfer = kP.getPkPoints();
        if(!player.isNewlyCreated()) {
            kP.setPkPoints(kP.getPkPoints() - toTransfer);
            player.sendPkMessage("You lose " + toTransfer +" Pk points for this death");
        }
        toTransfer *= 0.9D;
        toTransfer = killer.getPoints().pkpBonus(toTransfer);
        killer.getPoints().increasePkPoints((int)(toTransfer) + original, false);

        killer.sendPkMessage("You have received " + (toTransfer + original) +" PK points for this kill");


    }
	
	private static final String[] KILLER_MESSAGES = new String[] {
		"You have wiped the floor with %s.",
		"%s regrets the day that he met you.",
		"You rock, clearly %s does not.",
		"You have sent %s to his grave.",
		"All the kings horses and men could never put %s back together again...",
		"%s falls before your might.",
		"With a crushing blow %s's life is met with an untimely end.",
		"You have ended %s's life abruptly.",
		"The mysteries of life can no longer be discovered by %s.",
		"The sword is obviously mightier than %s.",
		"The death of %s is a burden you must bear.",
		"%s must of dissapointed the gods...",
         "I think %s said square root, not square up - either way, he died."
	};
	
	private static final String[] DEATH_MESSAGES = new String[] {
		"Oh dear! You are dead",
		"Death is a harsh mistress",
		"Life is fragile, you had to learn it the hard way",
		"Life is part of a cycle, the cycle just ended.",
		"The darkness of the afterlife awaits you...",
		"You're stupid... and dead"
	};


	private Player player;

	public static Location DEATH_LOCATION() {
		return Location.create(3096, 3471, 0);
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
		player.getCombat().setOpponent(null);
		for(int i = 0; i < Skills.SKILL_COUNT - 3; i++) {
			player.getSkills().setLevel(i, player.getSkills().getLevelForExp(i));
		}
        if(System.currentTimeMillis() - player.getExtraData().getLong("lastdeath") > 120000) {
		    player.getSpecBar().setAmount(SpecialBar.FULL);
            player.getExtraData().put("lastdeath", System.currentTimeMillis());
        } else {
            player.sendMessage("You don't restore special energy as you have died too quickly");
        }
		player.specOn = false;
		player.teleBlockTimer = System.currentTimeMillis();
		player.getActionSender().resetFollow();
        if(player.getRunePouch().size() > 0)
            player.getRunePouch().clear();
		player.getSpecBar().sendSpecAmount();
		player.getSpecBar().sendSpecBar();
		//CombatEntility ddd = entity.cE.getKiller();

		player.cE.setPoisoned(false);

		player.cE.setFreezeTimer(0);
		Player killer = player.cE.getKiller();
        if((player.duelAttackable > 0 || (killer != null && killer.duelAttackable > 0)) ||
				(Duel.inDuelLocation(killer) || Duel.inDuelLocation(player)) || player.hasDuelTimer()) {    //If dying in duel arena
			Duel.finishFullyDuel(player);
        } else if (player.getDungeoneering().inDungeon()) {
			DungeoneeringManager.handleDying(player);
        } else if(LastManStanding.inLMSArea(player.cE.getAbsX(), player.cE.getAbsY())) {
            LastManStanding.getLastManStanding().deathCheck(player, killer);
		} else if (Bork.doDeath(player)) {
        } else if(World.getWorld().getContentManager().handlePacket(6, player, ClickId.ATTACKABLE)) {
			if(World.getWorld().getContentManager().handlePacket(6, player, ClickId.FIGHT_PITS_DEATH))
			if(killer != null) //in fight pits death, reward player
				killer.getInventory().add(Item.create(391, 1));
		} else if(World.getWorld().getContentManager().handlePacket(6, player, 32000, - 1, - 1, - 1)) {
			World.getWorld().getContentManager().handlePacket(6, player, 32001, - 1, - 1, - 1);
		} else if(player.fightCavesWave > 0 && !player.getLocation().inPvPArea()) { //If dying in fight caves
			player.fightCavesWave = 0;
			player.getActionSender().showInterfaceWalkable(- 1);
			player.setTeleportTarget(Location.create(2439, 5171, 0), false);
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
                            if(killer.getLocation().getZ() != PurePk.HEIGHT) {
						        killer.getPoints().updateEloRating(player.getPoints().getEloRating(), EloRating.WIN);
						        player.getPoints().updateEloRating(oldKillerRating, EloRating.LOSE);
                            }


						}

                        try {
                            if(killer.getPvPTask() != null)
                                TaskHandler.checkTask(killer, player);
                        } catch(Exception e) {
                            System.err.println("PvP tasks error!");
                            e.printStackTrace();
                        }
						killer.getBountyHunter().handleBHKill(player);
                        if(isRecentKill(killer, player)) {
							killer.sendPkMessage("You have recently killed this player and do not receive PK points.");
                            if(killer.getGameMode() <= player.getGameMode())
                                handlePkpTransfer(killer, player, 0);
                        } else {
							if(player.getKillCount() >= 10) {
								killer.increaseKillStreak();
							}
							AchievementHandler.progressAchievement(player, "Kill");
                            killer.addLastKill(player.getName());
                            int pkpIncrease = (int)Math.pow(player.getKillCount(), 0.4);
                            if(pkpIncrease > 40)
                                pkpIncrease = 40;

							int pointsToAdd = ((int)((player.wildernessLevel/4 + player.getBounty())) + pkpIncrease);

                            for(SpecialArea area: SpecialAreaHolder.getAreas()) {
                                if(area.inEvent() && area.inArea(player))
                                    pointsToAdd *= 5;
                            }
							if(player.getKillStreak() >= 6) {
                                AchievementHandler.progressAchievement(player, "Killstreak");
								for(Player p : World.getWorld().getPlayers())
									if(p != null)
                                		p.sendPkMessage(killer.getSafeDisplayName() + " has just ended " + player.getSafeDisplayName() + "'s rampage of " + player.getKillStreak() + " kills.");
							}
							handlePkpTransfer(killer, player, pointsToAdd > 0 ? pointsToAdd : 5);
                            if(Rank.hasAbility(killer, Rank.SUPER_DONATOR))
                                killer.getSpecBar().increment(SpecialBar.FULL/5);
                            if(Rank.hasAbility(killer, Rank.DONATOR))
                                killer.getSpecBar().increment(SpecialBar.CYCLE_INCREMENT);
                            killer.getSpecBar().sendSpecBar();
							killer.getSpecBar().sendSpecAmount();

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
            boolean inSpecial = false;
            for(SpecialArea area : SpecialAreaHolder.getAreas()) {
                if(area.inArea(player) && area instanceof NIGGERUZ) {
                    inSpecial = true;
                    player.setTeleportTarget(area.getDefaultLocation(), false);
                }
            }
            if(!inSpecial)
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
		player.getActionSender().resetFollow();
		player.cE.morrigansLeft = 0;
	}


}