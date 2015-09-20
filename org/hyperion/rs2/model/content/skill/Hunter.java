package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;


/**
 * @author Vegas/Linus/Flux/Jolt/KFC/Tinderbox/Jack Daniels <- Same Person
 */
public class Hunter implements ContentTemplate {

	public static final int[] IMP_IDS = {1028, 6055, 1029, 6056, 1030, 6057,
			1031, 6058, 1032, 6059, 1033, 6060, 1034, 6061, 1035, 6062, 6063,
			7845, 7846, 6064, 7903, 7906};


	public static void addExp(Player p, int NpcId) {
		switch(NpcId) {
			case 1028:// baby impling
			case 6055:// Baby impling
				p.getSkills().addExperience(Skills.HUNTER, 1000);
				break;
			case 1029:// Young impling
			case 6056:// Young impling
				p.getSkills().addExperience(22, 1500);
				break;
			case 1030:// Gourmet impling
			case 6057:// Gourmet impling
				p.getSkills().addExperience(22, 2100);
				break;
			case 1031:// Earth impling
			case 6058:// Earth impling
				p.getSkills().addExperience(22, 2500);
				break;
			case 1032:// Essence impling
			case 6059:// Essence impling
				p.getSkills().addExperience(22, 3600);
				break;
			case 1033:// Eclectic impling
			case 6060:// Eclectic impling
				p.getSkills().addExperience(22, 7503);
				break;
			case 1034:// Nature impling
			case 6061:// Nature impling
				p.getSkills().addExperience(22, 15500);
				break;
			case 1035:// Magpie impling
			case 6062:// Magpie impling
				p.getSkills().addExperience(22, 26000);
				break;
			case 6063:// Ninja impling
				p.getSkills().addExperience(22, 56000);
				break;
			case 7845:// Pirate impling
			case 7846:// Pirate impling
				p.getSkills().addExperience(22, 61000);
				break;
			case 6064:// Dragon impling
				p.getSkills().addExperience(22, 85400);
				break;
			case 7903:// Kingly Imp
			case 7906:
				p.getSkills().addExperience(22, 104000);
				break;

		}
	}

	public static void addItem(Player p, int NpcId) {
		switch(NpcId) {
			case 1028:// baby impling
			case 6055:// Baby impling
				p.getInventory().add(new Item(11238));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11238, 1);
				break;
			case 1029:// Young impling
			case 6056:// Young impling
				p.getInventory().add(new Item(11240));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11240, 1);
				break;
			case 1030:// Gourmet impling
			case 6057:// Gourmet impling
				p.getInventory().add(new Item(11242));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11242, 1);
				break;
			case 1031:// Earth impling
			case 6058:// Earth impling
				p.getInventory().add(new Item(11244));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11244, 1);
				break;
			case 1032:// Essence impling
			case 6059:// Essence impling
				p.getInventory().add(new Item(11246));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11246, 1);
				break;
			case 1033:// Eclectic impling
			case 6060:// Eclectic impling
				p.getInventory().add(new Item(11248));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11248, 1);
				break;
			case 1034:// Nature impling
			case 6061:// Nature impling
				p.getInventory().add(new Item(11250));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11250, 1);
				break;
			case 1035:// Magpie impling
			case 6062:// Magpie impling
				p.getInventory().add(new Item(11252));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11252, 1);
				break;
			case 6063:// Ninja impling
				p.getInventory().add(new Item(11254));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11254, 1);
				break;
			case 7845:// Pirate impling
			case 7846:// Pirate impling
				p.getInventory().add(new Item(13337));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 13337, 1);
				break;
			case 6064:// Dragon impling
				p.getInventory().add(new Item(11256));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 11256, 1);
				break;
			case 7903:// Kingly Imp
			case 7906:
				p.getInventory().add(new Item(15517));
				p.getAchievementTracker().itemSkilled(Skills.HUNTER, 15517, 1);
				break;
		}
	}

	public static int getReq(int NpcId) {
		switch(NpcId) {
			case 1029:// Young impling
			case 6056:// Young impling
				return 22;
			case 1030:// Gourmet impling
			case 6057:// Gourmet impling
				return 28;
			case 1031:// Earth impling
			case 6058:// Earth impling
				return 36;
			case 1032:// Essence impling
			case 6059:// Essence impling
				return 42;
			case 1033:// Eclectic impling
			case 6060:// Eclectic impling
				return 50;
			case 1034:// Nature impling
			case 6061:// Nature impling
				return 58;
			case 1035:// Magpie impling
			case 6062:// Magpie impling
				return 65;
			case 6063:// Ninja impling
				return 74;
			case 7845:// Pirate impling
			case 7846:// Pirate impling
				return 76;
			case 6064:// Dragon impling
				return 83;
			case 7903://Kingly Imp
			case 7906:
				return 91;
		}
		return 0;
	}

	public void catchImp(final Player p, final int NpcId, final int xcoord, final int ycoord) {

		if(System.currentTimeMillis() - p.contentTimer < 1500) {
			return;
		}
		p.contentTimer = System.currentTimeMillis();
		if(p.getEquipment().get(Equipment.SLOT_WEAPON) == null || (p.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 11259
				&& p.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 10010)) {
			p.getActionSender().sendMessage("You need a net to catch imps!");
			return;
		}
		if(p.getInventory().freeSlots() == 0) {
			p.getActionSender().sendMessage("You need some free inventory slots to catch imps!");
			return;
		}
		switch(NpcId) {
			case 1028:// baby impling
			case 6055:// Baby impling

			case 1029:// Young impling
			case 6056:// Young impling

			case 1030:// Gourmet impling
			case 6057:// Gourmet impling

			case 1031:// Earth impling
			case 6058:// Earth impling

			case 1032:// Essence impling
			case 6059:// Essence impling

			case 1033:// Eclectic impling
			case 6060:// Eclectic impling

			case 1034:// Nature impling
			case 6061:// Nature impling

			case 1035:// Magpie impling
			case 6062:// Magpie impling

			case 6063:// Ninja impling

			case 7845:// Pirate impling
			case 7846:// Pirate impling

			case 6064:// Dragon impling

			case 7903://Kingly Imp
			case 7906:
				if(p.getSkills().getLevel(22) < getReq(NpcId)) {
					p.getActionSender().sendMessage("You need a hunter level of " + getReq(NpcId) + " to catch this impling.");
					return;
				}
				ContentEntity.startAnimation(p, 5209);
				p.cE.face(xcoord, ycoord);
				World.getWorld().submit(new Event(500) {
					public void execute() {
						boolean success = HunterNpcs.removeImp(NpcId, xcoord, ycoord);
						if(success) {
							p.getActionSender().sendMessage(
									"You catch the impling!");
							int caught = p.getExtraData().getInt("impscaught");
							caught++;
							p.getExtraData().put("impscaught", caught);
							p.getActionSender().sendMessage("You have now caught " + caught + " implings!");
							if(caught == 20) {
								p.getActionSender().sendMessage("You can now go to Santa to receive your reward!");
							}
							addItem(p, NpcId);
							addExp(p, NpcId);
						}
						this.stop();
					}
				});

		}
	}

	@Override
	public boolean clickObject(Player player, int type, int npcId, int xcoord, int ycoord,
	                           int d) {
		if(type == 10) {
			catchImp(player, npcId, xcoord, ycoord);
			return false;
		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {
		HunterNpcs.hunterStartup();
	}

	@Override
	public int[] getValues(int type) {
		if(type == 10)
			return IMP_IDS;
		return null;
	}

}