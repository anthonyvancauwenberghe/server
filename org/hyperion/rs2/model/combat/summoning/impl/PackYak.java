package org.hyperion.rs2.model.combat.summoning.impl;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.BoB;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.summoning.AbstractSummoningSpecial;

public final class PackYak extends AbstractSummoningSpecial {	
	private final int usedWith, slot;
	
	public PackYak(int usedWith, int slot) {
		this.usedWith = usedWith;
		this.slot = slot;
	}
	@Override
	public int requiredSpecial() {
		return 0;
	}

	@Override
	public boolean requiresOpponent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getScrollId() { return -1; }

	@Override
	public boolean checkRequirements(Player p) {
        this.execute(p);
		return true;
	}

	@Override
	public void execute(Player player) {

	}

	@Override
	public void executeOpponent(Entity p) throws NullPointerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeFamiliar(NPC n) {
		//npc.getCombat().doAnim();
		//npc.playGraphics(Graphic.create());
	}

}
