package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;

public class BunyipEvent extends Event {
	
	private Player player;
	
	public BunyipEvent() {
		super(20000);
	}
	
	public BunyipEvent(Player p) {
		super(20000);
		player = p;	}

	@Override
	public void execute() {
        if(!isRunning() || player == null || player.getCombat() == null || player.getCombat().getFamiliar() == null || player.getCombat().getFamiliar().getDefinition() == null){
            this.stop();
            return;
        }
        this.stop();
		player.getCombat().getFamiliar().getCombat().doAnim(7741);
		player.getCombat().getFamiliar().getCombat().doGfx(1507);
		ContentEntity.increaseSkill(player, 3, 2);
	}

}
