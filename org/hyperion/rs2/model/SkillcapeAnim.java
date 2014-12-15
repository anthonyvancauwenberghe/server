package org.hyperion.rs2.model;

import org.hyperion.rs2.model.cluescroll.ClueScrollManager;

public class SkillcapeAnim {

	public static int skillIdForCape(int cape) {
		switch(cape) {
			case 9747:
			case 9748:
				return 0;
	        /*
		     * Defense cape.
		     */
			case 9753:
			case 9754:
				return 1;
		    /*
		     * Strength cape.
		     */
			case 9750:
			case 9751:
				return 2;
		    /*
		     * Hitpoints cape.
		     */
			case 9768:
			case 9769:
				return 3;
		    /*
		     * Ranging cape.
		     */
			case 9756:
			case 9757:
				return 4;
		    /*
		     * Prayer cape.
		     */
			case 9759:
			case 9760:
				return 5;
		    /*
		     * Magic cape.
		     */
			case 9762:
			case 9763:
				return 6;
		    /*
		     * Cooking cape.
		     */
			case 9801:
			case 9802:
				return 7;
		    /*
		     * Woodcutting cape.
		     */
			case 9807:
			case 9808:
				return 8;
		    /*
		     * Fletching cape.
		     */
			case 9783:
			case 9784:
				return 9;
		    /*
		     * Fishing cape.
		     */
			case 9798:
			case 9799:
				return 10;
		    /*
		     * Firemaking cape.
		     */
			case 9804:
			case 9805:
				return 11;
		    /*
		     * Crafting cape.
		     */
			case 9780:
			case 9781:
				return 12;
		    /*
		     * Smithing cape.
		     */
			case 9795:
			case 9796:
				return 13;
		    /*
		     * Mining cape.
		     */
			case 9792:
			case 9793:
				return 14;
		    /*
		     * Herblore cape.
		     */
			case 9774:
			case 9775:
				return 15;
		    /*
		     * Agility cape.
		     */
			case 9771:
			case 9772:
				return 16;
		    /*
		     * Thieving cape.
		     */
			case 9777:
			case 9778:
				return 17;
		    /*
		     * Slayer cape.
		     */
			case 9786:
			case 9787:
				return 18;

		    /*
		     * Farming cape.
		     */
			case 9810:
			case 9811:
				return 19;
		    /*
		     * Runecraft cape.
		     */
			case 9765:
			case 9766:
				return 20;
		    /*
		     * Hunter's cape
		     */
			case 9948:
			case 9949:
				return 21;
		    /*
		     * Construct. cape.
		     */
			case 9789:
			case 9790:
				return 22;
		    /*
		     * Summoning cape.
		     */
			case 12169:
			case 12170:
				return 23;
		    /*
		     * Quest cape.
		     */
			case 9813:
				return 24;
		}
		return - 1;
	}

	public static void skillcapeEmote(final Player player) {
		int skill = - 1, skillcapeAnimation = - 1, skillcapeGraphic = - 1;
		String skillcapeName = "";
		int cape = 0;
		if(player.getEquipment().get(1) == null)
			cape = - 1;
		else
			cape = player.getEquipment().get(1).getId();
		if(cape != - 1) {
			int skillId = skillIdForCape(cape);
			switch(skillId) {
		    /*
		     * Attack cape.
		     */
				case 0:
					skill = 0;
					skillcapeAnimation = 4959;
					skillcapeGraphic = 823;
					skillcapeName = "Attack";
					break;
		    /*
		     * Defense cape.
		     */
				case 1:
					skill = 1;
					skillcapeAnimation = 4961;
					skillcapeGraphic = 824;
					skillcapeName = "Defense";
					break;
		    /*
		     * Strength cape.
		     */
				case 2:
					skill = 2;
					skillcapeAnimation = 4981;
					skillcapeGraphic = 828;
					skillcapeName = "Strength";
					break;
		    /*
		     * Hitpoints cape.
		     */
				case 3:
					skill = 3;
					skillcapeAnimation = 4971;
					skillcapeGraphic = 833;
					skillcapeName = "Hitpoints";
					break;
		    /*
		     * Ranging cape.
		     */
				case 4:
					skill = 4;
					skillcapeAnimation = 4973;
					skillcapeGraphic = 832;
					skillcapeName = "Ranging";
					break;
		    /*
		     * Prayer cape.
		     */
				case 5:
					skill = 5;
					skillcapeAnimation = 4979;
					skillcapeGraphic = 829;
					skillcapeName = "Prayer";
					break;
		    /*
		     * Magic cape.
		     */
				case 6:
					skill = 6;
					skillcapeAnimation = 4939;
					skillcapeGraphic = 813;
					skillcapeName = "Magic";
					break;
		    /*
		     * Cooking cape.
		     */
				case 7:
					skill = 7;
					skillcapeAnimation = 4955;
					skillcapeGraphic = 821;
					skillcapeName = "Cooking";
					break;
		    /*
		     * Woodcutting cape.
		     */
				case 8:
					skill = 8;
					skillcapeAnimation = 4957;
					skillcapeGraphic = 822;
					skillcapeName = "Woodcutting";
					break;
		    /*
		     * Fletching cape.
		     */
				case 9:
					skill = 9;
					skillcapeAnimation = 4937;
					skillcapeGraphic = 812;
					skillcapeName = "Fletching";
					break;
		    /*
		     * Fishing cape.
		     */
				case 10:
					skill = 10;
					skillcapeAnimation = 4951;
					skillcapeGraphic = 819;
					skillcapeName = "Fishing";
					break;
		    /*
		     * Firemaking cape.
		     */
				case 11:
					skill = 11;
					skillcapeAnimation = 4975;
					skillcapeGraphic = 831;
					skillcapeName = "Firemaking";
					break;
		    /*
		     * Crafting cape.
		     */
				case 12:
					skill = 12;
					skillcapeAnimation = 4949;
					skillcapeGraphic = 818;
					skillcapeName = "Crafting";
					break;
		    /*
		     * Smithing cape.
		     */
				case 13:
					skill = 13;
					skillcapeAnimation = 4943;
					skillcapeGraphic = 815;
					skillcapeName = "Smithing";
					break;
		    /*
		     * Mining cape.
		     */
				case 14:
					skill = 14;
					skillcapeAnimation = 4941;
					skillcapeGraphic = 814;
					skillcapeName = "Mining";
					break;
		    /*
		     * Herblore cape.
		     */
				case 15:
					skill = 15;
					skillcapeAnimation = 4969;
					skillcapeGraphic = 835;
					skillcapeName = "Herblore";
					break;
		    /*
		     * Agility cape.
		     */
				case 16:
					skill = 16;
					skillcapeAnimation = 4977;
					skillcapeGraphic = 830;
					skillcapeName = "Agility";
					break;
		    /*
		     * Thieving cape.
		     */
				case 17:
					skill = 17;
					skillcapeAnimation = 4965;
					skillcapeGraphic = 826;
					skillcapeName = "Thieving";
					break;
		    /*
		     * Slayer cape.
		     */
				case 18:
					skill = 18;
					skillcapeAnimation = 4967;
					skillcapeGraphic = 827;
					skillcapeName = "Slayer";
					break;

		    /*
		     * Farming cape.
		     */
				case 19:
					skill = 19;
					skillcapeAnimation = 4963;
					skillcapeGraphic = 825;
					skillcapeName = "Farming";
					break;
		    /*
		     * Runecraft cape.
		     */
				case 20:
					skill = 20;
					skillcapeAnimation = 4947;
					skillcapeGraphic = 817;
					skillcapeName = "Runecrafting";
					break;
		    /*
		     * Hunter's cape
		     */
				case 21:
					skill = 21;
					skillcapeAnimation = 5158;
					skillcapeGraphic = 907;
					skillcapeName = "Hunter";
					break;
		    /*
		     * Construct. cape.
		     */
				case 22:
					skill = 22;
					skillcapeAnimation = 4953;
					skillcapeGraphic = 820;
					skillcapeName = "Construction";
					break;
		    /*
		     * Summoning cape.
		     */
				case 23:
					skill = 23;
					skillcapeAnimation = 8525;
					skillcapeGraphic = 1515;
					skillcapeName = "Summmoning";
					break;
		    /*
		     * Quest cape.
		     */
				case 24:
					skillcapeAnimation = 4945;
					skillcapeGraphic = 816;
					skillcapeName = "Quest";
					break;
				default:
					player.getActionSender().sendMessage(
							"You need to be wearing a skillcape to do the skillcape emote.");
					break;
			}
			if(skill == - 1 || player.getSkills().getLevelForExp(skill) >= 99) {
				player.playAnimation(Animation.create(skillcapeAnimation));
				player.playGraphics(Graphic.create(skillcapeGraphic));
                //ClueScrollManager.trigger(player, skillcapeAnimation);
			} else {
				player.getActionSender().sendMessage(
						"You need to be level 99 " + skillcapeName
								+ " to do the "
								+ skillcapeName + " emote.");
			}
		} else {
			player.getActionSender().sendMessage(
					"You need to be wearing a skillcape to do the skillcape emote.");
		}
	}
}
