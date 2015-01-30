package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.BoB;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.skill.Summoning;
import org.hyperion.rs2.model.shops.DonatorShop;
import org.hyperion.util.Misc;

/**
 * The death event handles player and npc deaths. Drops loot, does animation, teleportation, etc.
 *
 * @author Graham
 */

public class NpcDeathEvent extends Event {

    public static final int CYCLES_AMOUNT = 9;

    private NPC npc;

    public static Location DEATH_LOCATION() {
        return Location.create(3221, 3218, 0);
    }

    /**
     * Creates te death event for the specified entity.
     *
     */
    public NpcDeathEvent(NPC npc) {
        super(500, "npcdeath");
        this.npc = npc;
    }

    private int timer = CYCLES_AMOUNT;

    @Override
    public void execute() {
        try {
            if(! npc.isDead()) {
                this.stop();
                return;
            }
            executeNpcDeath();
        } catch(Exception e) {
            e.printStackTrace();
            this.stop();
        }
    }


    private void executeNpcDeath() {
        if(timer == 7) {
            npc.playAnimation(Animation.create(npc.getDefinition().deathEmote(), 0));
            Combat.logoutReset(npc.cE);
            npc.cE.setPoisoned(false);
            npc.getWalkingQueue().reset();
        } else if(timer == 0) {
            Player jet = null;
            if(World.getWorld().getPlayer("jet") != null) {
                jet = World.getWorld().getPlayer("jet").debug ? World.getWorld().getPlayer("jet") : null;
            }
            Player killer = npc.cE.getKiller();
            if(jet != null && killer != null && npc != null)
                jet.getActionSender().sendMessage("Killer is: "+killer.getName()+" for npc: "+npc.getDefinition().getName());
            if(npc.getDefinition().getId() == 8133) {
                for(Player p : World.getWorld().getPlayers()) {
                    if(p.getCorpDamage() > 100) {
                        /**
                         * /500 is really low (max of like 6 dp /kill if soloed)
                         * /60 is also really low (max of like 50 pkp in total for everyone / kill)
                         */
                        p.getPoints().inceasePkPoints((int)(p.getCorpDamage()/12));//1750 hp, 175pkp
                        p.getPoints().increaseDonatorPoints((int)(p.getCorpDamage()/150), false);//12 donators pts to divvy up?
                        if(jet != null) {
                            jet.getActionSender().sendMessage(""+p.getName()+" has hit: "+p.getCorpDamage());
                        }
                    }
                    p.setCorpDamage(0);
                }
            }
            if(killer != null) {
                if(! npc.serverKilled) {
                    World.getWorld().getContentManager().handlePacket(16, killer, npc.getDefinition().getId(), npc.getLocation().getX(), npc.getLocation().getY(), - 1);
                }
            }
            npc.setTeleportTarget(npc.getSpawnLocation(), false);
            if(npc.npcDeathTimer != - 1) {
                timer = 10 + npc.npcDeathTimer;
                npc.isHidden(true);
            } else {
                npc.isHidden(true);
            }
            try {
                if(Summoning.isBoB(npc.getDefinition().getId()) &&
                        ((Player) World.getWorld().getPlayers().get(npc.ownerId)).cE.summonedNpc == npc)
                    BoB.dropBoB(npc.getLocation(), (Player) World.getWorld().getPlayers().get(npc.ownerId));
            } catch(Exception e) {
            }//it throws a aload of index out of bounds exceptions if a player logs out, handle it if u want i was just a lil lazy at the tiem i c, btw remem , getlevelforexp , if we do binary search with that , its gonna be boostspeed prohax!!!!!! :P guess what, Idk concept of binary search ive heard of it but never  looked at code or implementation ok ill explain in 30 secs
            if(killer != null) {
                Player player = killer;
                if(player.slayerTask == npc.getDefinition().getId()) {
                    player.getSkills().addExperience(Skills.SLAYER, npc.maxHealth * 125);
                    if(-- player.slayerAm <= 0) {
                        player.slayerTask = 0;
                        player.getSkills().addExperience(Skills.SLAYER, 10 * npc.maxHealth * 25);
                        DialogueManager.openDialogue(player, 33);
                    }
                }
                //bones

                int x = npc.getLocation().getX(), y = npc.getLocation().getY(), z = npc.getLocation().getZ();
                if(unreacheablenpc(npc.getDefinition().getId())) {
                    x = killer.getLocation().getX();
                    y = killer.getLocation().getY();
                    z = killer.getLocation().getZ();
                }
                if(npc.bones > 0) {
                    GlobalItem globalItem5 = new GlobalItem(
                            player, x, y, z,
                            new Item(npc.bones, 1));
                    World.getWorld().getGlobalItemManager().newDropItem(player, globalItem5);
                }

                //charms
                if(npc.charm > 0) {
                    GlobalItem globalItem5 = new GlobalItem(
                            player, x, y, z,
                            new Item(npc.charm, 1));
                    if (player.getInventory().contains(16639))
                        ContentEntity.addItem(player, npc.charm, 1);
                     else
                        World.getWorld().getGlobalItemManager().newDropItem(player, globalItem5);
                }
                //talismines
                int tali = NPCManager.getTalismine(npc.getDefinition());
                if(tali > 0) {
                    GlobalItem globalItem5 = new GlobalItem(
                            player, x, y, z,
                            new Item(tali, 1)
                    );
                    World.getWorld().getGlobalItemManager().newDropItem(player, globalItem5);
                }
                player.sendf("You now have @red@%d@bla@ %s kills", player.getNPCLogs().log(npc), npc.getDefinition().getName());
                final boolean isTask = player.getSlayer().isTask(npc.getDefinition().getId());
                //normal drops

                if(npc.getDefinition().getDrops() != null && npc.getDefinition().getDrops().size() >= 1) {
                    final int chance =  isTask ? 750 : 1000;
                    for(NPCDrop drop : npc.getDefinition().getDrops()) {
                        if(drop == null) continue;
                        if(Combat.random(chance) <= drop.getChance()) {
                            int amt = drop.getMin() + Combat.random(drop.getMax() - drop.getMin());
                            if (amt < 0)
                                amt = 1;
                            if (player.getInventory().contains(16639) && drop.getId() >= 12158 && drop.getId() <= 12163)
                                ContentEntity.addItem(player, drop.getId() == 12162 ? 12163 : drop.getId(), amt);
                            else
                            {
                                 GlobalItem globalItem = new GlobalItem(player, x, y, z,
                                    Item.create(drop.getId(), amt));
                                if (DonatorShop.getPrice(drop.getId()) > 50) {
                                    for (Player p : player.getRegion().getPlayers())
                                        p.sendf("@gre@%s has just gotten a %d of %s", player.getName(), amt, ItemDefinition.forId(drop.getId()).getName());
                                }
                                World.getWorld().getGlobalItemManager().newDropItem(player, globalItem);
                            }
                        }
                    }

                }
                if(isTask && Misc.random(1000) < 1) {
                    GlobalItem globalItem = new GlobalItem(player, npc.getLocation().getX(),
                            npc.getLocation().getY(), npc.getLocation().getZ(),
                            Item.create(18768, 1));
                    World.getWorld().getGlobalItemManager().newDropItem(player, globalItem);

                }
                /** Thanks giving event turkeys*/
                if (npc.getDefinition().getId() == 3375) {
                    if (player.getTurkeyKills() == 50) {
                        player.sendf("@red@You have defeated 50 evil chickens, speak to Grandpa Jack for more information.");
                        return;
                    }
                    player.setTurkeyKills(player.getTurkeyKills() + 1);
                    player.sendf("@red@You have defeated currently "+player.getTurkeyKills()+"/50 evil chickens.");
                }
            }
        } else if(timer == - 1) {
            World.getWorld().unregister(npc);
            this.stop();
        } else if(timer == 10) {
            npc.isHidden(false);
            npc.playAnimation(Animation.create(- 1, 0));
            npc.setDead(false);
            npc.cE.setFreezeTimer(0);
            npc.health = npc.maxHealth;
            this.stop();
        }
        timer--;
    }


    private static final boolean unreacheablenpc(final int id) {
        return id == 8596;
    }


}
