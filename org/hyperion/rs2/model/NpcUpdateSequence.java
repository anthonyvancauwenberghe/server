package org.hyperion.rs2.model;

import org.hyperion.rs2.model.combat.Combat;

/**
 * Created by Gilles on 12/02/2016.
 */
public class NpcUpdateSequence implements UpdateSequence<NPC> {
    @Override
    public void executePreUpdate(NPC npc) {
	    /*
		 * If the map region changed set the last known region.
		 */
        if(npc != null && npc.isMapRegionChanging()) {
            npc.setLastKnownRegion(npc.getLocation());
        }

		/*
		 * Process the next movement in the NPC's walking queue.
		 */
        if(npc != null && npc.getWalkingQueue() != null)
            npc.getWalkingQueue().processNextMovement();

        if(npc != null) {
            if(npc.cE.getOpponent() != null) {
                System.out.println("Processing cb for " + npc.getDefinition().getName());
                if (!Combat.processCombat(npc.cE)) {
                    System.out.println("Done processing cb for " + npc.getDefinition().getName());
                    Combat.resetAttack(npc.cE);
                    System.out.println("Done resetting cb for " + npc.getDefinition().getName());
                }
            } else if (npc.isDead()) {
                NPC.randomWalk(npc);
            }
        }
    }

    @Override
    public void executeUpdate(NPC npc) {
        throw new UnsupportedOperationException("NPC's cannot be updated.");
    }

    @Override
    public void executePostUpdate(NPC npc) {
        if(npc.getUpdateFlags().get(UpdateFlags.UpdateFlag.HIT_3)) {
            npc.getUpdateFlags().reset();
            npc.getDamage().setHit1(npc.getDamage().getHit3());
            npc.getUpdateFlags().flag(UpdateFlags.UpdateFlag.HIT);
        } else {
            npc.getUpdateFlags().reset();
        }
        if(npc.cE != null)
            npc.cE.isDoingAtk = false;
        npc.setTeleporting(false);
        npc.reset();
    }
}
