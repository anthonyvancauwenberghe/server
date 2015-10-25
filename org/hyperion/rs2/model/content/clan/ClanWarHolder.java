package org.hyperion.rs2.model.content.clan;

import org.hyperion.rs2.model.DeathDrops;
import org.hyperion.rs2.model.Player;
import org.hyperion.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 6/26/15
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClanWarHolder {

    public enum ClanWarRule {

        NO_REJOIN(1) {
            @Override
            public boolean join(final Player player, final ClanWarHolder holder) {
                return holder.hasDied(player);
            }
        },
        NEW_MEMBERS_ALLOWED(1 << 1) {
            @Override
            public boolean join(final Player player, final ClanWarHolder holder) {
                return super.join(player, holder) && holder.inOriginal(player);
            }

        },
        DROP_ITEMS_ON_DEATH(1 << 2) {
            @Override
            public boolean leave(final Player player, final ClanWarHolder holder) {
                if(player.isDead()) {
                    DeathDrops.dropsAtDeath(player, player.getCombat().getKiller());
                }
                return true;
            }
        };

        private final int removeMask;
        private final int bitmask;

        ClanWarRule(int bitmask, int... removeMasks) {
            this.bitmask = bitmask;
            int r = 0;
            if (removeMasks != null) {
                for (int i : removeMasks) {
                    r |= i;
                }
            }
            this.removeMask = r;

        }

        public boolean join(final Player player, final ClanWarHolder holder) {
            return !holder.getWar().started();
        }

        public boolean leave(final Player player, final ClanWarHolder holder) {
            return true;
        }

        public int addRule(int flag) {
            flag |= bitmask;
            flag &= ~removeMask;
            return flag;
        }

        public int removeRule(int flag) {
            return flag & ~bitmask;
        }

    }

    public final Clan clan;
    private final String[] originalNames;

    private final List<String> deaths = new ArrayList<>();

    private ClanWar war;

    public ClanWarHolder(final ClanWar war, final Clan clan) {
        this.war = war;
        this.clan = clan;
        this.originalNames = clan.getPlayers().stream().map(Player::getName).map(String::toLowerCase).toArray(String[]::new);
    }

    public boolean hasDied(final String name) {
        return deaths.contains(name.toLowerCase());
    }

    public boolean inOriginal(final String name) {
        return ArrayUtils.contains(originalNames, name.toLowerCase());
    }

    public void setWar(final ClanWar war) {
        if(this.war != null) throw new IllegalStateException("Clan war has already been set!");
        this.war = war;
    }

    public ClanWar getWar() {
        return war;
    }

    public boolean hasDied(final Player player) { return hasDied(player.getName()); }
    public boolean inOriginal(final Player player) {return inOriginal(player.getName()); }


}
