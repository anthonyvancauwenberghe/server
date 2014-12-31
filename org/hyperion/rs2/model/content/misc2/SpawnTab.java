package org.hyperion.rs2.model.content.misc2;

import org.hyperion.Server;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.minigame.FightPits;

import java.io.FileNotFoundException;

public class SpawnTab implements ContentTemplate {

	public static final int[][] BARRAGERUNES = {
			{555, 1000}, // Death
			{560, 1000}, // Blood
			{565, 1000} // Water
	};

	public static void addBarrageRunes(Player player) {
		if(! Server.SPAWN)
			return;
		for(int i = 0; i < BARRAGERUNES.length; i++) {
			ContentEntity.addItem(player, BARRAGERUNES[i][0],
					BARRAGERUNES[i][1]);
		}
	}

	public static final int[][] MELEESET = {
			{4151, 1},
			{7462, 1},
			{11732, 1},
			{6585, 1},
			{4728, 1},
			{4730, 1},
			{6524, 1},
			{10828, 1}
	};

	public static void addMeleeSet(Player player) {
		if(! Server.SPAWN)
			return;
		for(int i = 0; i < MELEESET.length; i++) {
			ContentEntity.addItem(player, MELEESET[i][0], MELEESET[i][1]);
		}
	}

	public static final int[][] MAGESET = {
			{4708, 1},
			{4712, 1},
			{4714, 1},
			{4675, 1},
			{6889, 1},
			{6920, 1},
			{1052, 1},
			{1708, 1},
	};

	public static void addMageSet(Player player) {
		if(! Server.SPAWN)
			return;
		for(int i = 0; i < MAGESET.length; i++) {
			ContentEntity.addItem(player, MAGESET[i][0], MAGESET[i][1]);
		}
	}

	public static final int[][] RANGESET = {

			{2581, 1},
			{2577, 1},
			{15126, 1},
			{4736, 1},
			{4738, 1},
			{10499, 1},
			{9244, 100},
			{9185, 1},
			{7462, 1}
	};

	public static void addRangeSet(Player player) {
		if(! Server.SPAWN)
			return;
		for(int i = 0; i < RANGESET.length; i++) {
			ContentEntity.addItem(player, RANGESET[i][0], RANGESET[i][1]);
		}
	}

	public static final int[][] HYBRIDSET = {
			{4675, 1},
			{4151, 1},
			{6585, 1},
			{7462, 1},
			{6920, 1},
			{4712, 1},
			{4714, 1},
			{4728, 1},
			{4730, 1},
			{1052, 1},
			{10828, 1},
			{1215, 1},
	};

	public static void addHybridSet(Player player) {
		if(! Server.SPAWN)
			return;
		for(int i = 0; i < HYBRIDSET.length; i++) {
			ContentEntity.addItem(player, HYBRIDSET[i][0], HYBRIDSET[i][1]);
		}
	}

	public static final int[][] VEANGEANCE_RUNES = {
			{557, 1000},
			{9075, 1000},
			{560, 1000},
	};

	public static void addVengRunes(Player player) {
		if(! Server.SPAWN)
			return;
		for(int i = 0; i < VEANGEANCE_RUNES.length; i++) {
			ContentEntity.addItem(player, VEANGEANCE_RUNES[i][0], VEANGEANCE_RUNES[i][1]);
		}
	}

	public static void addSharks(Player player) {
		if(! Server.SPAWN)
			return;
		ContentEntity.addItem(player, 386, 1000);
	}

	public static final int[][] SUPERSETS = {
			{2440, 1},
			{2442, 1},
			{2436, 1},
	};

	public static void addSuperSets(Player player) {
		if(! Server.SPAWN)
			return;
		for(int i = 0; i < SUPERSETS.length; i++) {
			ContentEntity.addItem(player, SUPERSETS[i][0], SUPERSETS[i][1]);
		}
	}

	public static void addSuperRestores(Player player) {
		if(! Server.SPAWN) return;
		ContentEntity.addItem(player, 3025, 100);
	}

	public static void addRangingPotions(Player player) {
		if(! Server.SPAWN) return;
		ContentEntity.addItem(player, 2445, 100);
	}

	private static int[] ActionButtonIds = {
			 29167, 29168, 29169, 29170, 29171, 29172
	};

	@Override
	public boolean clickObject(Player player, int type, int a, int b, int c,
	                           int d) {
		if(!Server.SPAWN)
			return false;
        if(player.hardMode())
            return false;
		if(type == 0) {
			if(player.wildernessLevel > 0) {
				player.getActionSender().sendMessage("You cannot spawn Items in the wilderness.");
				return false;
			}
			if(FightPits.inGame(player))
				return false;
			switch(a) {
				case 29162:
					addMeleeSet(player);
					break;
				case 29163:
					addMageSet(player);
					break;
				case 29164:
					addRangeSet(player);
					break;
				case 29165:
					addHybridSet(player);
					break;
				case 29167:
					addSharks(player);
					break;
				case 29168:
					addSuperSets(player);
					break;
				case 29169:
					addSuperRestores(player);
					break;
				case 29170:
					addRangingPotions(player);
					break;
				case 29171:
					addBarrageRunes(player);
					break;
				case 29172:
					addVengRunes(player);
					break;
			}
		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {

	}

	@Override
	public int[] getValues(int type) {
		if(type == 0)
			return ActionButtonIds;
		return null;
	}

}