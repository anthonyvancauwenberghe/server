package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.minigame.DangerousPK;

/**
 * An event which increases ActivityPoints, refreshes Quest Tab , refreshes
 * Skills.
 */
public class PlayerEvent1Second extends Event {

	/**
	 * The delay in milliseconds between consecutive execution.
	 */
	public static final int CYCLETIME = 1000;

	/**
	 * Creates the event each 30 seconds.
	 */
	public PlayerEvent1Second() {
		super(CYCLETIME);
	}

	@Override
	public void execute() {
		for(Player p : World.getWorld().getPlayers()) {

            if(System.currentTimeMillis() - p.getExtraData().getLong("lastwalk") > 13000 && p.getEquipment().getItemId(Equipment.SLOT_WEAPON) == 15426
                    && System.currentTimeMillis() - p.getExtraData().getLong("lastcanespin") > 6000) {
                p.playAnimation(Animation.create(12664));
                p.getExtraData().put("lastcanespin", System.currentTimeMillis());
            }

			if(! p.active)
				continue;

			if(p.getDrainRate() > 0) {
				// System.out.println("drain rate: "+getDrainRate()+" prayer level: "+getSkills().getLevel2(5));
				// Prayer.updateCurses(p);

				if(p.getSkills().getLevel(5) - p.getDrainRate() <= 0) {
					p.getActionSender().sendMessage(
							"You've run out of Prayer points.");
					p.getSkills().detractLevel(5, p.getSkills().getLevel(5));
					p.resetPrayers();
					p.getActionSender().sendSkill(5);
					return;
				}
				p.prayerDrain += p.getDrainRate();
				if(p.prayerDrain > 1 && !p.isDead()) {
					p.getSkills().detractLevel(5, (int) p.prayerDrain);
					p.prayerDrain = 0;
				}
				p.getActionSender().sendSkill(5);
			}


            for(int i = 0; i < Skills.SKILL_COUNT; i++) {
				p.skillRecoverTimer[i]++;
				if(p.skillRecoverTimer[i] == 60 && i != 5 && i != 23) {
					p.skillRecoverTimer[i] = 0;
					p.getSkills().normalizeLevel(i);
				}
			}

			p.decreaseSkullTimer();
			if(p.duelAttackable > 0)
				p.refreshDuelTimer();

			p.getExtraData().put("pmCount", 0);
		}
		//FFARandom.cycle();
	}

}
