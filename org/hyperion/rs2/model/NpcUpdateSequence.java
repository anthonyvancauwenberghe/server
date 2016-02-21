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
        System.out.println("1");
        if(npc != null && npc.isMapRegionChanging()) {
            System.out.println("2");
            npc.setLastKnownRegion(npc.getLocation());
            System.out.println("3");
        }

		/*
		 * Process the next movement in the NPC's walking queue.
		 */
        System.out.println("4");
        if(npc != null && npc.getWalkingQueue() != null)
            npc.getWalkingQueue().processNextMovement();
        System.out.println("5");

        if(npc != null) {
            System.out.println("6");
            if (npc.cE.getOpponent() != null) {
                System.out.println("7");
                if (!Combat.processCombat(npc.cE))
                    Combat.resetAttack(npc.cE);
                System.out.println("8");
            } else if (npc.isDead()) {
                System.out.println("9");
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
        System.out.println("10");
        if(npc.getUpdateFlags().get(UpdateFlags.UpdateFlag.HIT_3)) {
            System.out.println("11");
            npc.getUpdateFlags().reset();
            System.out.println("12");
            npc.getDamage().setHit1(npc.getDamage().getHit3());
            System.out.println("13");
            npc.getUpdateFlags().flag(UpdateFlags.UpdateFlag.HIT);
            System.out.println("14");
        } else {
            System.out.println("15");
            npc.getUpdateFlags().reset();
            System.out.println("16");
        }
        System.out.println("17");
        if(npc.cE != null)
            npc.cE.isDoingAtk = false;
        System.out.println("18");
        npc.setTeleporting(false);
        System.out.println("19");
        npc.reset();
        System.out.println("20");
    }
}
