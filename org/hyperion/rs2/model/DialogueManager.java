package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.rs2.model.Animation.FacialAnimation;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.bounty.BountyPerkHandler;
import org.hyperion.rs2.model.content.bounty.BountyPerks.Perk;
import org.hyperion.rs2.model.content.minigame.Barrows3;
import org.hyperion.rs2.model.content.minigame.DangerousPK;
import org.hyperion.rs2.model.content.minigame.DangerousPK.ArmourClass;
import org.hyperion.rs2.model.content.minigame.RangingGuild;
import org.hyperion.rs2.model.content.minigame.ZombieMinigame;
import org.hyperion.rs2.model.content.misc.Starter;
import org.hyperion.rs2.model.content.misc.TGEvent;
import org.hyperion.rs2.model.content.misc2.Dicing;
import org.hyperion.rs2.model.content.misc2.SkillCapeShops;
import org.hyperion.rs2.model.content.pvptasks.PvPTask;
import org.hyperion.rs2.model.content.skill.HunterLooting;
import org.hyperion.rs2.model.sets.SetData;
import org.hyperion.rs2.model.sets.SetUtility;
import org.hyperion.rs2.net.ActionSender.DialogueType;
import org.hyperion.rs2.packet.ModerationOverride;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.util.Misc;


public class DialogueManager {

	public static void openDialogue(Player player, int dialogueId) {
		if(dialogueId == - 1 && player.tutIsland == 10) {
			player.getActionSender().removeAllInterfaces();
			return;
		}
		for(int i = 0; i < 5; i++) {
			player.getInterfaceState().setNextDialogueId(i, - 1);
		}
		player.getInterfaceState().setOpenDialogueId(dialogueId);
		if(player.getInteractingEntity() instanceof Player)
			return;
		NPC npc = (NPC) player.getInteractingEntity();
		if(World.getWorld().getContentManager().handlePacket(20, player, dialogueId, 0, 0, 0))
			return;
		switch(dialogueId) {
			case 0:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Good day. How may I help you?");
				player.getInterfaceState().setNextDialogueId(0, 1);
				break;
			case 1:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"I'd like to access my bank account, please.",
						"I'd like to set/change my PIN please.");
				player.getInterfaceState().setNextDialogueId(0, 2);
				player.getInterfaceState().setNextDialogueId(1, 3);
				//player.getInterfaceState().setNextDialogueId(2, 4);
				break;
			case 2:
				player.getActionSender().removeChatboxInterface();
				Bank.open(player, false);
				break;
			case 3:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"I'd like to set my PIN please.");
				player.getInterfaceState().setNextDialogueId(0, 4);
				break;
			case 4:
				player.getActionSender().removeAllInterfaces();
				Bank.open(player, true);
				break;
			case 5:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Bandit Camp",
						"Soul's Bane Dungeon.",
						"Master Area",
						"More"
				);
				player.getInterfaceState().setNextDialogueId(0, 10);
				player.getInterfaceState().setNextDialogueId(1, 11);
				player.getInterfaceState().setNextDialogueId(2, 12);
				player.getInterfaceState().setNextDialogueId(3, 9);
				break;
			case 10:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(800);
				Magic.teleport(player, 3172, 2980, 0, false);
				break;
			case 11:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(1400);
				Magic.teleport(player, 3300, 9825, 0, false);
				break;
			case 12:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(2000);
				Magic.teleport(player, 2717, 9803, 0, false);
				break;
			case 6:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(2000);
				Magic.teleport(player, 3007, 9550, 0, false);
				break;
			case 7:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(2000);
				Magic.teleport(player, 2703, 9564, 0, false);
				break;
			case 8:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(2000);
				Magic.teleport(player, 2884, 9798, 0, false);
				break;
			case 9://more
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Asgarnian Ice Dungeon",
						"Brimhaven Dungeon.",
						"Taverly Dungeon",
						"Next"
				);
				player.getInterfaceState().setNextDialogueId(0, 6);
				player.getInterfaceState().setNextDialogueId(1, 7);
				player.getInterfaceState().setNextDialogueId(2, 8);
				player.getInterfaceState().setNextDialogueId(3, 34);
				break;
			case 13:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Duel Arena",
						"Barrows Minigame",
						"GodWars",
						"Thzaar"
				);
				player.getInterfaceState().setNextDialogueId(0, 14);
				player.getInterfaceState().setNextDialogueId(1, 15);
				player.getInterfaceState().setNextDialogueId(2, 16);
				player.getInterfaceState().setNextDialogueId(3, 17);
				break;
			case 14:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(1000);
				player.getActionSender().sendMessage("hideru cool");
				Magic.teleport(player, 3371, 3274, 0, false);
				break;
			case 15:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(1000);
				Magic.teleport(player, 3564, 3288, 0, false);
				break;
			case 16:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(1000);
				Magic.teleport(player, 2881, 5310, 2, false);
				break;
			case 17:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(1000);
				Magic.teleport(player, 2480, 5175, 0, false);
				break;
			case 18:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hello, welcome to " + Server.NAME + ", Please ensure you are active", "on the forums to keep updated with whats new.");
				player.getInterfaceState().setNextDialogueId(0, 19);
				break;
			case 19:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"To get to the main cities you may use your spellbook,", "however in order to train or visit minigames", "you must use your glory and ring of dueling.");
				player.getInterfaceState().setNextDialogueId(0, 20);
				break;
			case 20:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"You may begin your adventure by visiting barberian village", "and start training.");
				player.getInterfaceState().setNextDialogueId(0, 21);
				break;
			case 21:
	        /*player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"You can visit the highscores at: www.highscores.Jolt-Online.com");*/
				break;
			case 22:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"You already have a Slayer Task!", "Is this one too difficult?");
				player.getInterfaceState().setNextDialogueId(0, 23);
				break;
			case 23:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yes I need a new one.",
						"No I just forgot what it was."
				);
				player.getInterfaceState().setNextDialogueId(0, 24);
				player.getInterfaceState().setNextDialogueId(1, 26);
				break;
			case 24:
				if(player.slayerCooldown <= 0) {
					player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
							"Haha Your incomptence mocks me,",
							"So to regain some respect heres a new task.");
					player.getInterfaceState().setNextDialogueId(0, 25);
				} else {
					player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
							"Keep trying at your current task,",
							"I'm sure you can do it!");
				}
				break;
			case 25:
				player.slayerTask = 0;
				break;
			case 26:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Your current task is to kill " + player.slayerAm,
						NPCDefinition.forId(player.slayerTask).getName() + "'s.");
				player.getInterfaceState().setNextDialogueId(0, 27);
				break;
			case 27:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"Thanks for your help.");
				break;
			case 28:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hi, How may I help you?");
				player.getInterfaceState().setNextDialogueId(0, 29);
				break;
			case 29:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"I'm looking for a master in the slayer skill.",
						"Do you know any around here?");
				player.getInterfaceState().setNextDialogueId(0, 30);
				break;
			case 30:
				if(npc != null){
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "Yes I do, Infact you've already found him.",
                            "I'm the current grandmaster in slayer in all of glenior.");
                    player.getInterfaceState().setNextDialogueId(0, 31);
                }
				break;
			case 31:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Could you give me a task to improve my slayer abilities?",
						"No sorry I think I have the wrong person."
				);
				player.getInterfaceState().setNextDialogueId(0, 32);
				player.getInterfaceState().setNextDialogueId(1, 27);
				break;
			case 32:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Sure no problem, Now lets find a task,",
						"worthy of a warrior like yourself.");
				player.getInterfaceState().setNextDialogueId(0, 25);
				break;
			case 33:
				player.getActionSender().sendDialogue("Slayer Master", DialogueType.NPC, 1599, FacialAnimation.DEFAULT,
						"Congratulations on completing your slayer task!",
						"Come see me whenever you would like a new one.");
				break;
			case 34://more
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Rock Crabs",
						"Greater Demons.",
						"Back"
				);
				player.getInterfaceState().setNextDialogueId(0, 35);
				player.getInterfaceState().setNextDialogueId(1, 36);
				player.getInterfaceState().setNextDialogueId(2, 5);
				break;
			case 35:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2709, 3718, 0, false);
				break;
			case 36:
				player.getActionSender().removeAllInterfaces();
				player.removeAsTax(800);
				Magic.teleport(player, 2633, 9483, 2, false);
				break;
			case 37:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hello " + player.getName() + ", I see that you are new here.");
				player.getInterfaceState().setNextDialogueId(0, 38);
				break;
			case 38:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"Yes, I am can you please tell me how can I get to the main land?");
				player.getInterfaceState().setNextDialogueId(0, 39);
				break;
			case 39:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Not so fast young one, ", "you have to know the basics before i let you leave this place");
				player.getInterfaceState().setNextDialogueId(0, 40);
				break;
			case 40:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"Sure, just tell me what i have to do and i'll get it done.");
				player.getInterfaceState().setNextDialogueId(0, 41);
				break;
			case 41:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Right, i have a small task for you....");
				player.getInterfaceState().setNextDialogueId(0, 64);
				break;
			case 64:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"I need you to find my guide book", "Its around here somewhere.");
				player.getInterfaceState().setNextDialogueId(0, 42);
				break;
			case 42:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Can you do this for me?");
				player.getInterfaceState().setNextDialogueId(0, 43);
				break;
			case 43:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"Sure, i'm on it.");
				player.tutIsland = 2;
				break;
			case 44:
				if(ContentEntity.isItemInBag(player, 1856)) {
					dialogueId = 45;
					player.tutIsland = 3;
				} else
					player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
							"Can't you find the book!?,", "well you cant leave till it's found...");
				break;
			case 45:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Well done " + player.getName() + ", now please", "read the guide to understand basic infomation about " + Server.NAME);
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 46:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"Ok, thanks can i leave now?");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 47:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Not just yet, the main land is a dangerous place,", "you need to learn basic combat before i let you leave", "so you can defend yourself");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 48:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"ahh, fine tell me what i have to do now..");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 49:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Here take these combat items");
				ContentEntity.addItem(player, 1153, 1);
				ContentEntity.addItem(player, 1115, 1);
				ContentEntity.addItem(player, 1067, 1);
				ContentEntity.addItem(player, 1191, 1);
				ContentEntity.addItem(player, 1323, 1);
				player.tutIsland = 4;
				player.tutSubIsland = 0;
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 50:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Go next door and kill the chickens,", "once you kill them bring me the chicken");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 51:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"Right.... will do it right away");
				player.tutSubIsland = 0;
				player.tutIsland = 5;
				break;
			case 52:
				if(ContentEntity.isItemInBag(player, 2138)) {
					dialogueId = 53;
					player.tutIsland = 6;
				} else {
					player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
							"Too tough for you?", "sorry you can't leave till this is done");
				}
				break;
			case 53:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Ah, great job", "you may now leave to the main land");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 54:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"Alright cool! but before i leave can i get some food");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 55:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"I'm kind of hungery");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 56:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"well sure, take this axe,", "tinderbox and cook the meat you got");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				ContentEntity.addItem(player, 1351, 1);
				ContentEntity.addItem(player, 590, 1);
				player.tutSubIsland = 0;
				player.tutIsland = 7;
				break;
			case 57:
				if(! ContentEntity.isItemInBag(player, 2140)) {
					player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
							"I'm still hungry, I better find some food soon.");
				} else {
					player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
							"I feel much better now that I have eaten");
					player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				}
				break;
			case 58:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Good, you may now leave,", "if you need help later on use the guide book");
				player.getInterfaceState().setNextDialogueId(0, dialogueId + 1);
				break;
			case 59:
				player.tutIsland = 10;
				Magic.teleport(player, 3105, 3420, 0, false);
				if(! player.oldFag) {
					player.oldFag = true;
					ContentEntity.addItem(player, 995, 35000);
					ContentEntity.addItem(player, 326, 50);
					ContentEntity.addItem(player, 316, 50);
					ContentEntity.addItem(player, 558, 100);
					ContentEntity.addItem(player, 556, 100);
					ContentEntity.addItem(player, 555, 100);
					ContentEntity.addItem(player, 554, 100);
					ContentEntity.addItem(player, 557, 100);
					ContentEntity.addItem(player, 841, 1);
					ContentEntity.addItem(player, 882, 100);
					ContentEntity.addItem(player, 1712, 1);
					ContentEntity.addItem(player, 2560, 1);
				}
				player.getActionSender().openQuestInterface(Server.NAME + " Guide Book", new String[]{
						"Congratulations on Completeing the begineers",
						"Tutorial you can now read your guide book",
						"To learn how to get started.",
						"on " + Server.NAME + ".com",
				});
				player.getSkills().reset();
				player.getActionSender().sendSkills();
				break;
			case 60://1,3,4,6
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"I need to talk to the guide.");
				break;
			case 61://2
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"I need to find the book to give it to the guide.");
				break;
			case 62://5
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"I need to kill some chickens", "To get some meat for the guide.");
				break;
			case 63://7
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, - 1, FacialAnimation.DEFAULT,
						"I need to use my axe to chop the trees next door,", "make a fire and cook the raw chicken i have");
				break;
			case 65:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"I found a Secret Crypt, I'm going in!",
						"This secret crypt looks scary, ill come back later."
				);
				player.getInterfaceState().setNextDialogueId(0, 66);
				player.getInterfaceState().setNextDialogueId(1, - 1);
				break;
			case 66:
				Barrows3.confirmCoffinTeleport(player);
				player.getActionSender().removeAllInterfaces();
				break;
			case 67:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hi. How may I help you?");
				player.getInterfaceState().setNextDialogueId(0, 68);
				break;
			case 68:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Teleport me to Tzhaar!",
						"Nah, ill come back later."
				);
				player.getInterfaceState().setNextDialogueId(0, 69);
				player.getInterfaceState().setNextDialogueId(1, - 1);
				break;
			case 69:
				Magic.teleport(player, 2439, 5171, 0, false);
				player.getActionSender().removeAllInterfaces();
				break;
			/*

 Guide: "Good, you may now leave, if you need help later on use the guide book"
 the Guide then performs a animation and a gfx appears (tele another person gfx) and the players ends up in the main land
 the coords for that will be on barb village bridge to varrock

	
			 */

			case 70:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Experiments",
						"Taverly Dungeon",
						"Slayer Tower",
						"Brimhaven Dungeon",
						"Hill Giants"
				);
				player.getInterfaceState().setNextDialogueId(0, 71);
				player.getInterfaceState().setNextDialogueId(1, 72);
				player.getInterfaceState().setNextDialogueId(2, 73);
				player.getInterfaceState().setNextDialogueId(3, 74);
				player.getInterfaceState().setNextDialogueId(4, 75);
				break;
			case 71:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3570 - Misc.random(1), 9953 - Misc.random(1), 0, false);
				break;
			case 72:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2884, 9798, 0, false);
				break;
			case 73:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3428, 3535, 0, false);
				break;
			case 74:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2703, 9564, 0, false);
				break;
			case 75:
				player.getActionSender().removeAllInterfaces();
				//Magic.teleport(player,2709,3718,0);
				break;
			case 76:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Barrows",
						"Tzhaar Caves",
						"Duel Arena",
						"Warriors Guild",
						"Ranging Guild"
				);
				player.getInterfaceState().setNextDialogueId(0, 77);
				player.getInterfaceState().setNextDialogueId(1, 78);
				player.getInterfaceState().setNextDialogueId(2, 79);
				player.getInterfaceState().setNextDialogueId(3, 80);
				player.getInterfaceState().setNextDialogueId(4, 87);
				break;
			case 77:
				player.getActionSender().removeAllInterfaces();
				player.getActionSender().sendMessage("hideru cool");
				Magic.teleport(player, 3564, 3288, 0, false);
				break;
			case 78:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2480, 5175, 0, false);
				break;
			case 79:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3375, 3274, 0, false);
				break;
			case 80:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2875, 3546, 0, false);
				break;
			case 81:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Godwars", "King Black Dragon (Wild)", "Dagannoth Kings", "Chaos Elemental (Wild)", "Corporal Beast"
				);
				player.getInterfaceState().setNextDialogueId(0, 82);
				player.getInterfaceState().setNextDialogueId(1, 83);
				player.getInterfaceState().setNextDialogueId(2, 84);
				player.getInterfaceState().setNextDialogueId(3, 85);
				player.getInterfaceState().setNextDialogueId(4, 86);
				break;
			case 82:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2882, 5310, 2, false);
				break;
			case 83:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3007, 3849, 0, false);
				break;
			case 84:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 1910, 4367, 0, false);
				break;
			case 85:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3295, 3921, 0, false);
				break;
			case 86:
				player.getActionSender().removeAllInterfaces();
				//Magic.teleport(player,3242,9364,0);
				break;
			case 87:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2652, 3439, 0, false);
				break;
			case 88:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Lumbridge", "Varrock", "Edgeville", "Falador", "Camelot"
				);
				player.getInterfaceState().setNextDialogueId(0, 89);
				player.getInterfaceState().setNextDialogueId(1, 90);
				player.getInterfaceState().setNextDialogueId(2, 91);
				player.getInterfaceState().setNextDialogueId(3, 92);
				player.getInterfaceState().setNextDialogueId(4, 99);
				break;
			case 89:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3222, 3218, 0, false);
				break;
			case 90:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3210, 3424, 0, false);
				break;
			case 91:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3084, 3484, 0, false);
				break;
			case 92:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2964, 3372, 0, false);
				break;
			case 99:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2757, 3478, 0, false);
				break;

			case 93:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Mage Bank", "Edgeville Dragons", "Mid Wilderness", "Chaos Altar", "Fun Pk (Safe)"
				);
				player.getInterfaceState().setNextDialogueId(0, 94);
				player.getInterfaceState().setNextDialogueId(1, 95);
				player.getInterfaceState().setNextDialogueId(2, 96);
				player.getInterfaceState().setNextDialogueId(3, 97);
				player.getInterfaceState().setNextDialogueId(4, 106);
				break;
			case 94:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2539, 4716, 0, false);
				break;
			case 95:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2983, 3596, 0, false);
				break;
			case 96:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2982, 3733, 0, false);
				break;
			case 97:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3237, 3639, 0, false);
				break;
			case 98:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3300, 2795, 0, false);
				break;
			case 100:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Mining", "Smithing", "Fishing/Cooking", "Agility", "Farming/Woodcutting"
				);
				player.getInterfaceState().setNextDialogueId(0, 101);
				player.getInterfaceState().setNextDialogueId(1, 102);
				player.getInterfaceState().setNextDialogueId(2, 103);
				player.getInterfaceState().setNextDialogueId(3, 104);
				player.getInterfaceState().setNextDialogueId(4, 105);
				break;
			case 101:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3046, 9779, 0, false);
				break;
			case 102:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3079, 9502, 0, false);
				break;
			case 103:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2597, 3408, 0, false);
				break;
			case 104:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2724, 3484, 0, false);
				break;
			case 105:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2812, 3463, 0, false);
				break;
			case 106:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Multi Arena (Safe)", "Singles (Safe)"
				);
				player.getInterfaceState().setNextDialogueId(0, 107);
				player.getInterfaceState().setNextDialogueId(1, 108);
				break;
			case 107:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 2528 + Combat.random(6), 3303 + Combat.random(5), 0, false);
				break;
			case 108:
				player.getActionSender().removeAllInterfaces();
				Magic.teleport(player, 3290 + Combat.random(6), 3025 + Combat.random(5), 0, false);
				break;
			case 109:

				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Ranged Pure", "Hybrid Pure", "Berserker Pure", "Main", "Skiller"
				);
				player.getInterfaceState().setNextDialogueId(0, 110);
				player.getInterfaceState().setNextDialogueId(1, 111);
				player.getInterfaceState().setNextDialogueId(2, 112);
				player.getInterfaceState().setNextDialogueId(3, 113);
				player.getInterfaceState().setNextDialogueId(4, 114);
				break;
			case 110:
				Starter.giveRangedPure(player);
				player.getActionSender().removeAllInterfaces();
				player.getActionSender().showInterface(3559);
				player.receivedStarter = true;
				break;
			case 111:
				player.getActionSender().removeAllInterfaces();
				Starter.giveHybridPure(player);
				player.getActionSender().showInterface(3559);
				player.receivedStarter = true;
				break;
			case 112:
				player.getActionSender().removeAllInterfaces();
				Starter.giveBerserker(player);
				player.getActionSender().showInterface(3559);
				player.receivedStarter = true;
				break;
			case 113:
				player.getActionSender().removeAllInterfaces();
				Starter.giveMain(player);
				player.getActionSender().showInterface(3559);
				player.receivedStarter = true;
				break;
			case 114:
				player.getActionSender().removeAllInterfaces();
				Starter.giveSkiller(player);
				player.getActionSender().showInterface(3559);
				player.receivedStarter = true;
				break;
			case 115:

				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Flux Account", "DeviousPK Account"
				);
				player.getInterfaceState().setNextDialogueId(0, 116);
				player.getInterfaceState().setNextDialogueId(1, 117);
				break;
			case 116:
				break;
			case 117:
				break;
			case 118:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hi, Do you want ", "to exchange your implings", "for rewards?");
				player.getInterfaceState().setNextDialogueId(0, 119);
				break;
			case 119:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yea sure", "Nop"
				);
				player.getInterfaceState().setNextDialogueId(0, 120);
				player.getInterfaceState().setNextDialogueId(1, - 1);
				break;
			case 120:
				for(Item i : player.getInventory().toArray()) {
					if(i != null)
						HunterLooting.giveLoot(player, i.getId());
				}
				player.getActionSender().removeAllInterfaces();
				break;
			case 121:
				player.getActionSender().sendDialogue("Select a Spellbook", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Modern Spellbook", "Ancient Spellbook"
				);
				player.getInterfaceState().setNextDialogueId(0, 122);
				player.getInterfaceState().setNextDialogueId(1, 123);
				break;
			case 122:
				player.getSpellBook().changeSpellBook(SpellBook.REGULAR_SPELLBOOK);
				player.getActionSender().sendSidebarInterface(6, 1151);
				player.getActionSender().removeAllInterfaces();
				break;
			case 123:
				player.getSpellBook().changeSpellBook(SpellBook.ANCIENT_SPELLBOOK);
				player.getActionSender().sendSidebarInterface(6, 12855);
				player.getActionSender().removeAllInterfaces();
				break;
			case 124:
				player.getActionSender().sendDialogue("Talk About:", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Ranging Minigame", "Ranging SkillCape"
				);
				player.getInterfaceState().setNextDialogueId(0, 125);
				player.getInterfaceState().setNextDialogueId(1, 128);//TODO
				break;
			case 125:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hi, Do you want ", "to play a game?", "You might win cool awards!");
				player.getInterfaceState().setNextDialogueId(0, 126);
				break;
			case 126:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"All you have to do ", "is fire these targets!", "To enter you must pay 5000 gp");
				player.getInterfaceState().setNextDialogueId(0, 127);
				break;
			case 127:
				player.getActionSender().sendDialogue("Do you want to play?", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yes sure", "Sorry, maybe later"
				);
				player.getInterfaceState().setNextDialogueId(0, 129);
				player.getInterfaceState().setNextDialogueId(1, - 1);//TODO
				break;
			case 128:
				SkillCapeShops.openSkillCapeShop(player, 3000 + Misc.random(1));
				break;
			case 129:
				RangingGuild.buyShots(player);
				player.getActionSender().removeAllInterfaces();
				break;
			case 130:
				if(npc != null)
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hey, I'm a dice game hoster ", "if I throw 55 or higher I'll double your item!", "if I throw less than 55, you'll lose your item");
				player.getInterfaceState().setNextDialogueId(0, 131);
				break;
			case 131:
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"To play , simply give", "the item to me", "-use item with gambler-");
				player.getInterfaceState().setNextDialogueId(0, -1);
				break;
			case 132:
				player.getActionSender().sendDialogue("Santa Claus", DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Hello Adventurer", "I have a challenge for you..");
				player.getInterfaceState().setNextDialogueId(0, 133);
				break;
			case 133:
				player.getActionSender().sendDialogue("Santa Claus", DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"I need you to catch", "20 implings", "to help the world out!");
				player.getInterfaceState().setNextDialogueId(0, 134);
				break;
			case 134:
				player.getActionSender().sendDialogue("Will you do this?", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yes I will", "Sorry, maybe later"
				);
				player.getInterfaceState().setNextDialogueId(0, - 1);//TODO
				break;
			case 135:
				player.getActionSender().sendDialogue("Santa Claus", DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Enjoy your surprise!");
				player.getInterfaceState().setNextDialogueId(0, - 1);//TODO
				break;
			case 136:
				player.getActionSender().sendDialogue("Moderation Options", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Kick",
						"Jail",
						"Mute"
				);
				player.getInterfaceState().setNextDialogueId(0, 137);
				player.getInterfaceState().setNextDialogueId(1, 138);
				player.getInterfaceState().setNextDialogueId(2, 139);
				break;
			case 137:
				if(ModerationOverride.canModerate(player))
					ModerationOverride.kickPlayer(player.getModeration());
				player.getActionSender().removeAllInterfaces();
				PushMessage.pushGlobalMessage(player.getSafeDisplayName() + " has just kicked " + player.getModeration().getSafeDisplayName() + ".");
				player.setModeration(null);
				break;
			case 138:
				if(ModerationOverride.canModerate(player))
					ModerationOverride.jailPlayer(player.getModeration());
				player.getActionSender().removeAllInterfaces();
				PushMessage.pushGlobalMessage(player.getSafeDisplayName() + " has just jailed " + player.getModeration().getSafeDisplayName() + ".");
				player.setModeration(null);
				break;
			case 139:
				if(ModerationOverride.canModerate(player))
					ModerationOverride.mutePlayer(player.getModeration());
				player.getActionSender().removeAllInterfaces();
				PushMessage.pushGlobalMessage(player.getSafeDisplayName() + " has just muted " + player.getModeration().getSafeDisplayName() + ".");
				player.setModeration(null);
				break;
			case 140:
				player.getActionSender().sendDialogue("Sir", DialogueType.NPC, npc.getDefinition().getId() , FacialAnimation.DEFAULT, 
						"Hello I'm the master of PvP Tasks!", "Would you like a task?");
				player.getInterfaceState().setNextDialogueId(0, 141);//TODO
				break;
			case 141:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yes!",
						"No..."
				);
				player.getInterfaceState().setNextDialogueId(0, 142);
				player.getInterfaceState().setNextDialogueId(1, -1);
				break;
			case 142:
				if(player.getPvPTask() == null || player.getPvPTaskAmount() <= 0) {
					player.setPvPTask(null);
					player.setPvPTaskAmount(0);
					player.setPvPTask(PvPTask.toTask(Combat.random(2) + 1));
					player.setPvPTaskAmount(Combat.random(10) + 10);
					//DialogueManager.openDialogue(player, 139);
					player.getActionSender().sendDialogue("Sir", DialogueType.NPC, npc.getDefinition().getId() , FacialAnimation.DEFAULT,
							"I've assigned you "+player.getPvPTaskAmount()+" "+PvPTask.toString(player.getPvPTask())+"s to kill!");
				} else {
					player.getActionSender().sendDialogue("Sir", DialogueType.NPC, npc.getDefinition().getId() , FacialAnimation.DEFAULT, 
							"You still have "+player.getPvPTaskAmount()+" "+PvPTask.toString(player.getPvPTask())+"s to kill!");
				}
				player.getInterfaceState().setNextDialogueId(0, - 1); //exit
				break;
			case 143:
				player.getActionSender().sendDialogue("Are you sure?", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yes! I want to empty my inventory!",
						"No..."
				);
				player.getInterfaceState().setNextDialogueId(0, 144);
				player.getInterfaceState().setNextDialogueId(1, -1);
				break;
			case 144:
				player.getInventory().clear();
				player.getActionSender().removeChatboxInterface();
				break;
			case 145:
				player.getActionSender().sendDialogue("You protect 0 items in here!", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yes! I'm a brave warrior!",
						"No..."
				);
				player.getInterfaceState().setNextDialogueId(0, 146);
				player.getInterfaceState().setNextDialogueId(1, 147);
				//player.getInterfaceState().setNextDialogueId(2, 148);
				break;
			case 146:
				player.getActionSender().removeChatboxInterface();
				DangerousPK.toWaitArea(player);
				break;
			case 147:
				player.getActionSender().removeChatboxInterface();
				break;
			case 148:
				player.pickedClass = ArmourClass.RANGE;
				DangerousPK.toWaitArea(player);
				break;
				
			case 149:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Information",
						"View bank"
				);
				player.getInterfaceState().setNextDialogueId(0, 130);
				player.getInterfaceState().setNextDialogueId(1, 150);
				break;
			case 150:
				if(Rank.hasAbility(player, Rank.DEVELOPER)) {
					player.getActionSender().sendInterfaceInventory(5292, 5063);
					player.getActionSender().sendUpdateItems(5382, Dicing.getGambledItems());
				} else {
					player.getActionSender().sendMessage("You are not a high enough rank to view this");
					player.getActionSender().removeAllInterfaces();
				}
				break;
			case 151:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Hybridding Area",
						"Oldschool Pk Area",
						"Fun Pk Area"
				);
				player.getInterfaceState().setNextDialogueId(0, 152);
				player.getInterfaceState().setNextDialogueId(1, 153);
				player.getInterfaceState().setNextDialogueId(2, 154);
				break;
			case 152:
				Magic.goTo13s(player);
				player.getActionSender().removeChatboxInterface();
				break;
			case 153:
				OSPK.enter(player);
				player.getActionSender().removeChatboxInterface();
				break;
			case 154:
                Magic.teleport(player, Location.create(2594, 3156, 0), false);
				player.getActionSender().removeChatboxInterface();
				break;
			case 155:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Set my levels please!",
						"I'd like to keep my current levels"
				);
				player.getInterfaceState().setNextDialogueId(0, 156);
				player.getInterfaceState().setNextDialogueId(1, 157);
				break;
			case 156:
				//i know i should declare it outside as prviate static final but 2 lazy
				final int[][] skillData = {
						{Skills.ATTACK, 60},
						{Skills.DEFENCE, 1},
						{Skills.PRAYER, 52},
						{Skills.RANGED, 99},
						{Skills.MAGIC, 99}
				};
				if(!player.canSpawnSet())
					return;
				for(int i = 0; i < skillData.length; i++) {
					player.getSkills().setLevel(skillData[i][0], skillData[i][1]);
					player.getSkills().setExperience(skillData[i][0], player.getSkills().getXPForLevel(skillData[i][1]));
				}
				SetUtility.getInstantSet(player, SetData.getPureSet());
				SetUtility.addSetOfItems(player, SetData.getPureItems());
				player.getActionSender().removeChatboxInterface();
				break;
			case 157:
				if(!player.canSpawnSet())
					return;
				if(player.getSkills().getLevels()[Skills.ATTACK] >= 60 && player.getSkills().getLevels()[Skills.RANGED] >= 70) {
					SetUtility.getInstantSet(player, SetData.getPureSet());
					SetUtility.addSetOfItems(player, SetData.getPureItems());
				} else {
					player.sendMessage("You need 60 attack and 70 ranged to spawn this instant set!");
				}
				break;
			case 158:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"PvM Area",
						"Skilling Area"
				);
				//
				//
				player.getInterfaceState().setNextDialogueId(0, 159);
				player.getInterfaceState().setNextDialogueId(1, 160);
				break;
			case 159:
				Magic.teleport(player, Location.create(2373, 4972, 0), false, false);
				player.getActionSender().removeChatboxInterface();
				break;
			case 160:
				Magic.teleport(player, Location.create(3793, 2851, 0), false);
				player.getActionSender().removeChatboxInterface();
				break;
			case 161:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Set my levels please!",
						"I'd like to keep my current levels"
				);
				player.getInterfaceState().setNextDialogueId(0, 162);
				player.getInterfaceState().setNextDialogueId(1, 163);
				break;
			case 162:
				//i know i should declare it outside as prviate static final but 2 lazy
				final int[][] skillDataZ = {
						{Skills.ATTACK, 75},
						{Skills.DEFENCE, 45},
						{Skills.PRAYER, 95},
				};
				if(!player.canSpawnSet())
					return;
				for(int i = 0; i < skillDataZ.length; i++) {
					player.getSkills().setLevel(skillDataZ[i][0], skillDataZ[i][1]);
					player.getSkills().setExperience(skillDataZ[i][0], player.getSkills().getXPForLevel(skillDataZ[i][1]));
				}
				SetUtility.getInstantSet(player, SetData.getZerkSet());
				SetUtility.addSetOfItems(player, SetData.getZerkItems());
				player.getActionSender().removeChatboxInterface();
				break;
			case 163:
				if(!player.canSpawnSet())
					return;
				if(player.getSkills().getLevels()[Skills.ATTACK] >= 70 && player.getSkills().getLevels()[Skills.DEFENCE] >= 45) {
					SetUtility.getInstantSet(player, SetData.getZerkSet());
					SetUtility.addSetOfItems(player, SetData.getZerkItems());
				} else {
					player.sendMessage("You need 70 attack and 45 defense to spawn this instant set!");
				}
				break;
			case ZombieMinigame.START_DIALOG:
				break;
			case 165:
				player.getActionSender().sendDialogue("Upgrade ("+player.getBHPerks().calcNextPerkCost()+" BHPts)", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Upgrade my perks!",
						"What are perks?"
				);
				player.getInterfaceState().setNextDialogueId(0, 169);
				player.getInterfaceState().setNextDialogueId(1, 170);

				break;
			case 166:
				BountyPerkHandler.upgrade(player, Perk.SPEC_RESTORE);
				break;
			case 167:
				BountyPerkHandler.upgrade(player, Perk.VENG_REDUCTION);
				break;
			case 168:
				BountyPerkHandler.upgrade(player, Perk.PRAY_LEECH);
				break;
			case 169:
				player.getActionSender().sendDialogue("Upgrade ("+player.getBHPerks().calcNextPerkCost()+" BHPts)", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Special Restore",
						"Veng Time Reduction",
						"Prayer Leeching"
				);
				
				player.getInterfaceState().setNextDialogueId(0, 166);
				player.getInterfaceState().setNextDialogueId(1, 167);
				player.getInterfaceState().setNextDialogueId(2, 168);
				break;
			case 170:
				player.getActionSender().removeChatboxInterface();
				player.sendMessage("Special Perk: @blu@Increase special after a kill", "@red@(I) 10% (II) 20% (III) 40%",
						"Veng Reduction Perk: @blu@Reduce time for next vengeance", "@red@(I) 4seconds (II)8seconds (II)16seconds",
						"Prayer Leech Perk: @blu@Leech your opponent's prayer (stacks with soulsplit & smite)");
				break;
			case 171:
				player.getActionSender().sendDialogue("RISK!", DialogueType.NPC, -1, FacialAnimation.DEFAULT,
						"Your opponent is in deep or multi wilderness...", "Are you sure you want to teleport to him/her?");
				player.getInterfaceState().setNextDialogueId(0, 172);//TODO
				break;
			case 172:
				player.getActionSender().sendDialogue("Upgrade ("+player.getBHPerks().calcNextPerkCost()+" BHPts)", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
						"Yes",
						"No"
				);
				player.getInterfaceState().setNextDialogueId(0, 173);
				player.getInterfaceState().setNextDialogueId(1, -1);
				break;
			case 173:
				final Player opp = player.getBountyHunter().getTarget();
				if(opp != null) {
					final int x = opp.getLocation().getX();
					final int y = opp.getLocation().getY();
					Magic.teleport(player, opp.getLocation().getX(), opp.getLocation().getY(), opp.getLocation().getZ(), false);
				}
				break;
            case 174:
                player.getActionSender().sendDialogue("Select an option", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
                        "I would like to have a new assignment.",
                        "Remove current slayer task (20 pts).",
                        "I would like to view the slayer store.",
                        "I would like to reset my task progress (lose total task streak)."
                );

                player.getInterfaceState().setNextDialogueId(0, 175);
                player.getInterfaceState().setNextDialogueId(1, 176);
                player.getInterfaceState().setNextDialogueId(2, 177);
                player.getInterfaceState().setNextDialogueId(3, 178);
                break;
            case 175:
                final String toDisplay;
                if(player.getSlayer().assignTask(player.getSkills().getRealLevels()[Skills.SLAYER]))
                    toDisplay = String.format("You have %d %s to kill!", player.getSlayer().getTaskAmount(), player.getSlayer().getTask());
                else
                     toDisplay = String.format("You still have %d %s to kill", player.getSlayer().getTaskAmount(), player.getSlayer().getTask() );
                player.getActionSender().sendDialogue("Slayer Master", DialogueType.NPC, npc.getDefinition().getId() , FacialAnimation.DEFAULT,
                       toDisplay);

                break;
            case 176:
                if(player.getSlayer().resetTask())
                    player.sendMessage("Your task has been successfully reset!");
                else
                    player.sendMessage("You need more slayer points to reset your task");
                player.getActionSender().removeChatboxInterface();
                break;
            case 177:
                ShopManager.open(player, 77);
                break;
            case 178:
                player.getActionSender().sendDialogue("Are you sure?", DialogueType.OPTION, - 1, FacialAnimation.DEFAULT,
                        "Yes i'm sure. I want to reset my task progress!",
                        "Nevermind."
                );
                player.getInterfaceState().setNextDialogueId(0, 200);
                player.getInterfaceState().setNextDialogueId(1, 201);
                break;
            case 200:
                player.getSlayer().removeTask();
                player.getActionSender().sendMessage("You now have 0 total tasks and your task has been reset!");
                player.getActionSender().removeChatboxInterface();
                break;
            case 201:
                player.getActionSender().removeChatboxInterface();
                break;
            /** Thanks giving event dialogues*/
            case 179: //jack D
                player.getInterfaceState().setNextDialogueId(0, -1);
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Good day " + player.getName() + "! I'm kind of in trouble.",
                        "In order to save thanks-giving I need your help to",
                        "defeat 50 evil chickens, once you've done that", "please come back to me and that speak to me again.");
                break;
            case 180: //jack D
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "You have killed "+player.getTurkeyKills()+" out of "+player.getTurkeyKills()+" evil chickens.",
                        "Speak with me once you have killed 50 of them,",
                        "we must save the thanks-giving after all!");
                player.getInterfaceState().setNextDialogueId(0, 6000);//
                break;
            case 181: //jack D
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "So you managed to kill all the evil chickens, "+player.getName()+"!",
                        "Now the thanks-giving is all safe thanks to you!");
                player.getInterfaceState().setNextDialogueId(0, 182);//
                break;
            case 182: //jack D
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Please accept my reward as a gift,", "it's the least I can do!");
                player.getInterfaceState().setNextDialogueId(0, 183);//
                break;
            case 183:
                player.sendMessage("DeviousPK wishes you happy thanks-giving!");
                player.getActionSender().removeChatboxInterface();
                player.sendMessage("@red@You have received x1 Web cloak to your bank account!");
                player.getBank().add(new Item(15352, 1));
                break;

            case 184: //gala
                if (player.hasFinishedTG()) {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "You have already saved the thanks-giving, "+player.getName()+"!");
                    player.getInterfaceState().setNextDialogueId(0, -1);
                    return;
                }
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "My friend called Grandpa Jack is in need of help.",
                        "I will take you to him for more instructions.");
                player.getInterfaceState().setNextDialogueId(0, 185);
                break;
            case 185: //gala
                TGEvent.teleport(player);
                player.getActionSender().removeChatboxInterface();
                break;
            case 6000:
                player.getActionSender().removeChatboxInterface();
                break;
			default:
				player.getActionSender().removeChatboxInterface();
				break;
		}
	}

	public static void advanceDialogue(Player player, int index) {
		int dialogueId = player.getInterfaceState().getNextDialogueId(index);
		if(dialogueId == - 1) {
			player.getActionSender().removeChatboxInterface();
			return;
		}
		openDialogue(player, dialogueId);
	}

}
