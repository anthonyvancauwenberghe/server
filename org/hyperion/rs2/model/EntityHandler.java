package org.hyperion.rs2.model;

import org.apache.mina.core.session.IoSession;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;

import java.util.Arrays;

/**
 * Created by Gilles on 11/02/2016.
 */
public class EntityHandler {


    /**
     * Handler registering of an Entity. Currently only used by Player and NPC
     * @param entity The entity to register
     */
    public static void register(Entity entity) {
        if(entity instanceof Player) {
            register((Player)entity);
        } if(entity instanceof NPC) {
            register((NPC)entity);
        }
    }

    private static void register(Player player) {
        /**
         * A small bit of code to activate the player their active punishments on login.
         */
        final PunishmentHolder holder = PunishmentManager.getInstance().get(player.getName());
        if (holder != null) {
            for (final Punishment p : holder.getPunishments()) {
                p.getCombination().getType().apply(player);
                p.send(player, false);
            }
        } else {
            for (final PunishmentHolder h : PunishmentManager.getInstance().getHolders()) {
                if (player.getName().equalsIgnoreCase(h.getVictimName()))
                    continue;
                for (final Punishment p : h.getPunishments()) {
                    if ((p.getCombination().getTarget() == Target.IP && p.getVictimIp().equals(player.getShortIP()))
                            || (p.getCombination().getTarget() == Target.MAC && p.getVictimMac() == player.getUID())
                            || (p.getCombination().getTarget() == Target.SPECIAL && Arrays.equals(p.getVictimSpecialUid(), player.specialUid))) {
                        p.getCombination().getType().apply(player);
                        p.send(player, false);
                    }
                }
            }
        }

        //TODO CONTINUE THIS TOMORROW

        IoSession session = player.getSession();
        if(session.isConnected() && !World.getLoginQueue().contains(player)) {
            World.getLoginQueue().add(player);
        }
    }

    private static void register(NPC npc) {
        World.getNpcs().add(npc);
    }

    /**
     * Handler deregistering of an Entity. Currently only used by Player and NPC
     * @param entity The entity to deregister
     */
    public static void deregister(Entity entity) {
        TaskManager.cancelTasks(entity);
        if(entity instanceof Player) {
            deregister((Player)entity);
        } if(entity instanceof NPC) {
            deregister((NPC)entity);
        }
    }

    private static void deregister(Player player) {
        World.getPlayers().remove(player);
    }

    private static void deregister(NPC npc) {
        World.getNpcs().remove(npc);
    }
}
