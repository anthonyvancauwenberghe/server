package org.hyperion.rs2.model.content.skill.unfinished.agility;

import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.content.ContentEntity;

public class Obstacle {
	protected int animId, skillXp, level;


	public int[] getObjs() {
		return null;
	}

	public boolean checkLevel(Player player) {
		if(ContentEntity.returnSkillLevel(player, Skills.AGILITY) < level) {
			ContentEntity.sendMessage(player, "You need level " + level + " agility to overcome this obstacle.");
			return false;
		}
		return true;
	}

	public void overCome(Player player, int[] data) {

	}

	protected static int getObjectLength(int id) {
		if(GameObjectDefinition.forId(id).getSizeX() > GameObjectDefinition.forId(id).getSizeY())
			return GameObjectDefinition.forId(id).getSizeX();
		else
			return GameObjectDefinition.forId(id).getSizeY();
	}
}
