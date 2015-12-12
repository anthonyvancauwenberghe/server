package org.hyperion.rs2.model.combat;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentEntity;

public class Curses {

    private static final String[] DRAIN_NAMES = {"Attack", "Ranged", "Magic", "Defense", "Strength", "Energy",
            "Special Energy"};

    /**
     * @param player, whoever is DOING the leech.
     */
    public static void applyLeeches(final Player player) {
        if(!player.getPrayers().isLeeching())
            return;
        if(!(System.currentTimeMillis() - player.LastTimeLeeched > 5000 * (1 + player.getPrayers().activeLeeches() / 2))){
            return;
        }
        if(player.cE.getOpponent() == null){
            return;
        }
        final int leechId = player.getPrayers().pollLeech();
        final CombatEntity victim = player.cE.getOpponent();
        updateLeech(player);
        World.getWorld().submit(new Event(800, "checked") {
            public void execute() {
                ContentEntity.startAnimation(player, 12575);
                if(victim == null || player == null){
                    this.stop();
                    return;
                }
                final int offsetY = (player.cE.getAbsX() - victim.getAbsX()) * -1;
                final int offsetX = (player.cE.getAbsY() - victim.getAbsY()) * -1;
                //find our lockon target
                final int hitId = player.cE.getSlotId(player.cE.getEntity());

                final int speed = 70;
                final int time = 24;
                final int slope = 0;
                player.getActionSender().createGlobalProjectile(player.cE.getAbsY(), player.cE.getAbsX(), offsetY, offsetX, 50, speed, getGfxIdForPrayerProjectile(leechId), 20, 20, hitId, time, slope);
                this.stop();
            }
        });
        Player pseudoPlayer = null;
        if(player.cE.getOpponent() != null && player.cE.getOpponent().getEntity() instanceof Player)
            pseudoPlayer = player.cE.getOpponent().getPlayer();
        final Player opponentPlayer = pseudoPlayer;
        World.getWorld().submit(new Event(1800, "checked") {
            public void execute() {
                if(opponentPlayer != null){
                    opponentPlayer.getCombat().doGfx(getGfxIdForPrayer(leechId));
                    if(leechId == 46){
                        if(opponentPlayer.duelAttackable < 1){
                            opponentPlayer.getSpecBar().decrease(15);
                            opponentPlayer.getSpecBar().sendSpecBar();
                            opponentPlayer.getSpecBar().sendSpecAmount();
                        }
                    }else{
                                /*int drainSkill = getSkillIdForLeechId(index);
                                if(drainSkill != -1 && drainSkill != 42) {
									if(opponentPlayer.getSkills().getLevel(drainSkill) >= opponentPlayer.getSkills().getLevelForExperience(drainSkill)) {
										opponentPlayer.getSkills().setLevel(drainSkill, (int)(opponentPlayer.getSkills().getLevel(drainSkill) * 0.85));
									}
								}*/
                    }
                    opponentPlayer.getActionSender().sendMessage("Your " + DRAIN_NAMES[leechId - 40] + " has been leeched by " + player.getSafeDisplayName() + " !");
                    player.getActionSender().sendMessage("You have leeched " + opponentPlayer.getSafeDisplayName() + "'s " + DRAIN_NAMES[leechId - 40] + " !");
                }
                this.stop();
            }
        });
    }

    public static void updateLeech(final Player player) {
        player.LastTimeLeeched = System.currentTimeMillis();
    }

    private static int getGfxIdForPrayer(int i) {
        i -= 40;
        switch(i){
            case 0://attack
                return 2253;
            case 3://range
                return 2238;
            case 4://magic
                return 2242;
            case 6://special
                return 2258;
            case 1://def
                return 2250;
            case 2://str
                return 2246;
        }
        return 0;
    }

    private static int getGfxIdForPrayerProjectile(final int i) {
        switch(i){
            case 40:
                return 2252;
            case 41:
                return 2236;
            case 42:
                return 2240;
            case 43:
                return 2248;
            case 44:
                return 2248;
            case 45:
                return 2256;
        }
        return 0;
    }

    private static int getSkillIdForLeechId(final int leechId) {
        switch(leechId){
            case 40:
                return 0;
            case 41:
                return 4;
            case 42:
                return 6;
            case 43:
                return 1;
            case 44:
                return 2;
        }
        return -1;
    }

}
