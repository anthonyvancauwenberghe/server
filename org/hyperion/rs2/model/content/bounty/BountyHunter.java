package org.hyperion.rs2.model.content.bounty;

import java.util.ArrayList;
import java.util.List;

import org.hyperion.rs2.model.GlobalItem;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.pvp.PvPArmourStorage;
import org.hyperion.rs2.model.content.bounty.rewards.BHDrop;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.util.Misc;

public class BountyHunter {


    public static final int BASE_POINTS = 50_000;
    private static final int DP_SPLIT = 850;
    private static final List<BHDrop> list;

    private enum Emblem {
        TIER_1(1),
        TIER_2(2),
        TIER_3(4),
        TIER_4(8),
        TIER_5(15),
        TIER_6(25),
        TIER_7(35),
        TIER_8(50),
        TIER_9(70),
        TIER_10(100);


        private static final int BASE_ID = 13195;

        private final int reward;
        private final int id;
        private Emblem(final int multiplier) {
            this.reward = multiplier * BASE_POINTS;
            this.id = ordinal() + BASE_ID;
        }

        private Emblem upgrade() {
            return ordinal() == values().length - 1 ? this : values()[ordinal() + 1];
        }

        public static Emblem getBest(final Container inventory) {
            for(int i = values().length - 1; i >= 0; i--) {
                if(inventory.contains(values()[i].id))
                    return values()[i];
            }
            return null;
        }
    }
	
	static {
		list = new ArrayList<>();
        list.add(BHDrop.create(13898, 5, true));
        list.add(BHDrop.create(13886, 3, true));
		list.add(BHDrop.create(13892, 4, true));
		
		for(int id : PvPArmourStorage.getArmours()) {
			list.add(BHDrop.create(id, 1, false));
		}
	}

    private int bhPoints = 0;
    private int emblemPoints = 0;
	private final Player player;
	private Player target;
    private boolean enabled = true;

	public BountyHunter(Player player) {
		this.player = player;
	}
	
	public void findTarget() {
		for(final Player p : World.getWorld().getPlayers()) {
			if(p.isHidden() || !applicable(p) || this.player.equals(p) || !levelCheck(p) || !wealthCheck(p) || !wildLevelCheck(p)) continue;
			assignTarget(p);
			break;
		}
	}
	
	public void assignTarget(Player p) {
		this.target = p;
		p.getBountyHunter().target = player;
		player.getActionSender().createArrow(target);
		p.getActionSender().createArrow(player);
		p.getQuestTab().sendBHTarget();
		player.getQuestTab().sendBHTarget();
	}
	
	public boolean levelCheck(Player p) {
		return Math.abs(p.getCombat().getCombat() - player.getCombat().getCombat()) < 12 && player.getLocation().getZ() == p.getLocation().getZ();
	}
	
	public boolean wildLevelCheck(final Player opp) {
		final int oppLevel = Combat.getWildLevel(opp.cE.getAbsX(), opp.cE.getAbsY());
		final int playerLevel = Combat.getWildLevel(player.cE.getAbsX(), player.cE.getAbsY());
		return (oppLevel < 10 && playerLevel < 10) || (oppLevel >= 10 && oppLevel >= 10);
	}
	
	private boolean wealthCheck(final Player opp) {
		final int accValue = player.getAccountValue().getTotalValue();
		final int oppAccValue = opp.getAccountValue().getTotalValue();
		return (oppAccValue < DP_SPLIT && accValue < DP_SPLIT) || (accValue >= DP_SPLIT && oppAccValue >= DP_SPLIT);
	}
	
	public static boolean applicable(Player player) {
		return player.getLocation().inPvPArea() && !player.getLocation().inFunPk() && player.getBountyHunter().target == null && player.getBountyHunter().enabled;
	}
	
	public static void fireLogout(final Player player) {
		final Player targ = player.getBountyHunter().getTarget();
		if(targ != null) {
			targ.getBountyHunter().setTarget(null);
			targ.getActionSender().removeArrow();
			targ.getQuestTab().sendBHTarget();
		}
	}
	
	public void handleBHKill(final Player opp) {
		if(!opp.equals(target)) return;
		player.sendMessage("@blu@You now have @red@"+incrementAndGet()+" @blu@bh points!");
		handleBHDrops(opp);
		for(Player p : new Player[]{player, opp}) {
			p.getBountyHunter().target = null;
			p.getActionSender().createArrow(10, -1);
			p.getQuestTab().sendAllInfo();
		}		
	}
	
	public void handleBHDrops(final Player opp) {
        GlobalItem gI = new GlobalItem(player, opp.getLocation().getX(),
                opp.getLocation().getY(), opp.getLocation().getZ(),
                Item.create(Emblem.BASE_ID, 1));
        World.getWorld().getGlobalItemManager().newDropItem(player, gI);
		for(final BHDrop drop : list) {
			if(Misc.random(drop.isRare() ? 5000 : 250) < drop.getChance()) {
					GlobalItem globalItem = new GlobalItem(player, opp.getLocation().getX(), 
							opp.getLocation().getY(), opp.getLocation().getZ(),
							Item.create(drop.getId(), 1));
					World.getWorld().getGlobalItemManager().newDropItem(player, globalItem);
					break;
			}
		}

        upgradeEmblem();
	}

    private void upgradeEmblem() {
        final Container inventory = player.getInventory();
        final Emblem best = Emblem.getBest(inventory);
        if(best != null) {
            final int slot = inventory.getSlotById(best.id);
            inventory.remove(slot, Item.create(best.id));
            inventory.add(Item.create(best.upgrade().id), slot);
        }
    }

    public boolean switchEnabled() {
        return enabled = !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getKills() {
        return bhPoints;
    }

    public int incrementAndGet() {
        return ++bhPoints;
    }

    public void setKills(final int kills) {
        this.bhPoints = kills;
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public int getEmblemPoints() {
        return emblemPoints;
    }

    public int setEmblemPoints(final int points) {
        return emblemPoints = points;
    }
	
}
