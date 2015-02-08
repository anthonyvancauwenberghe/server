package org.hyperion.rs2.model.content.bounty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    public static final int BASE_POINTS = 5;
    private static final int DP_SPLIT = 850;

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


        private static final Map<Integer, Emblem> EMBLEM_MAP = Stream.of(values()).collect(Collectors.toMap(e -> e.id, Function.<Emblem>identity()));;

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

        public static Emblem forId(int id) {
            return EMBLEM_MAP.get(id);
        }

        private static List<Item> getEmblems(final Container inventory) {
            return Stream.of(inventory.toArray()).filter(Objects::nonNull).filter(item -> forId(item.getId()) != null).collect(Collectors.toList());
        }

        private static int getTotalVal(final Item[] items) {
            return Stream.of(items).filter(Objects::nonNull).filter(item -> forId(item.getId()) != null).mapToInt(item -> forId(item.getId()).reward).sum();
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
		return Math.abs(p.getCombat().getCombat() - player.getCombat().getCombat()) < 12 && player.getLocation().getZ() == p.getLocation().getZ() && player.getUID() != p.getUID();
	}
	
	public boolean wildLevelCheck(final Player opp) {
		final int oppLevel = Combat.getWildLevel(opp.cE.getAbsX(), opp.cE.getAbsY());
		final int playerLevel = Combat.getWildLevel(player.cE.getAbsX(), player.cE.getAbsY());
		return (oppLevel < 10 && playerLevel < 10) || (oppLevel >= 10 && playerLevel >= 10);
	}
	
	private boolean wealthCheck(final Player opp) {
		final int accValue = player.getAccountValue().getTotalValue();
		final int oppAccValue = opp.getAccountValue().getTotalValue();
		return (oppAccValue < DP_SPLIT && accValue < DP_SPLIT) || (accValue >= DP_SPLIT && oppAccValue >= DP_SPLIT);
	}
	
	public static boolean applicable(Player player) {
		return player.getLocation().inPvPArea() && !player.getLocation().inFunPk() && player.getBountyHunter().target == null && player.getPermExtraData().getBoolean("bhon");
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
        if(opp.getSkills().getCombatLevel() < 80 || player.getSkills().getCombatLevel() < 80)
            return;
        if(player.getLocation().getZ() > 0)
            return;
        incrementAndGet();  //2x
		player.sendMessage("@blu@You now have @red@"+incrementAndGet()+" @blu@bh points!");
		handleBHDrops(opp);
		for(Player p : new Player[]{player, opp}) {
			p.getBountyHunter().target = null;
			p.getActionSender().createArrow(10, -1);
			p.getQuestTab().sendAllInfo();
		}
        final List<Item> emblems = Emblem.getEmblems(opp.getInventory());
        for(final Item item : emblems) {
            player.getBank().add(Item.create(item.getId(), opp.getInventory().remove(item)));
            player.sendf("@red@%s@bla@ was added to your bank", item.getDefinition().getName());
        }
	}
	
	public void handleBHDrops(final Player opp) {
        GlobalItem gI = new GlobalItem(player, opp.getLocation().getX(),
                opp.getLocation().getY(), opp.getLocation().getZ(),
                Item.create(Emblem.BASE_ID, 1));
        World.getWorld().getGlobalItemManager().newDropItem(player, gI);
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

    public int emblemExchagePrice() {
        return Emblem.getTotalVal(player.getInventory().toArray());
    }

    public void exchangeEmblems() {
        final List<Item> toSubtract = Emblem.getEmblems(player.getInventory());
        final int toAdd = Emblem.getTotalVal(toSubtract.toArray(new Item[toSubtract.size()]));
        for(Item item : toSubtract) {
            player.getInventory().remove(item);
        }
        emblemPoints += toAdd;
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
