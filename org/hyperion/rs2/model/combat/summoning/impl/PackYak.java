package org.hyperion.rs2.model.combat.summoning.impl;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.NPC;
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
		return 50;
	}

	@Override
	public boolean requiresOpponent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getScrollId() {
		return 12435;
	}

	@Override
	public boolean checkRequirements(Player p) {
		if(!p.getInventory().contains(getScrollId()))
			return false;
		return true;
	}

	@Override
	public void execute(Player player) {
		player.playGraphics(Graphic.create(1316, 0));
		player.playAnimation(Animation.create(7660));
        int amount = player.getInventory().remove(slot, Item.create(usedWith));
        player.getBank().add(Item.create(usedWith, amount));
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
