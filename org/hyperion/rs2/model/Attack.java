package org.hyperion.rs2.model;

import org.hyperion.rs2.model.combat.CombatEntity;

public interface Attack {

    String getName();

    int[] npcIds();

    int handleAttack(NPC n, CombatEntity attack);

}