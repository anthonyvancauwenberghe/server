package org.hyperion.rs2.packet;


import org.hyperion.map.WorldMap;
import org.hyperion.rs2.action.impl.MiningAction;
import org.hyperion.rs2.action.impl.MiningAction.Node;
import org.hyperion.rs2.action.impl.ProspectingAction;
import org.hyperion.rs2.action.impl.WoodcuttingAction;
import org.hyperion.rs2.action.impl.WoodcuttingAction.Tree;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.content.DoorManager;

import java.io.IOException;

public class ObjectClickHandler {

	public static void clickObject(Player p, int id, int x, int y, int type) {
		//System.out.println("Id " + id);
        final GameObjectDefinition def = GameObjectDefinition.forId(id);
        int offX = def != null ? 1 + def.getSizeX() : 3;
        int offY = def != null ? 1 + def.getSizeY() : 3;
		if(!canClick(offX, offY, x, y, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ())) {
			p.getActionSender().sendMessage("You are too far away from the object to interact with it!");
			return;
		}
		if(Rank.hasAbility(p, Rank.ADMINISTRATOR) && p.debug)
			p.getActionSender().sendMessage("Clicked object: "+id);
		if(World.getWorld().getContentManager().handlePacket(5 + type, p, id, x, y, - 1))
			return;
		if(type == 1) {
			objectClickOne(p, id, x, y);
		} else if(type == 2) {
			objectClickTwo(p, id, x, y);
		}
	}

	public static void objectClickOne(final Player player, final int id, final int x, final int y) {
		if(id > GameObjectDefinition.MAX_DEFINITIONS || id < 0)
			return;
		Location loc = Location.create(x, y, player.getLocation().getZ());
		if(DoorManager.handleDoor(player, loc, id))
			return;
		// woodcutting
		Tree tree = Tree.forId(id);
		if(tree != null && player.getLocation().isWithinInteractionDistance(loc)) {
			player.getActionQueue().addAction(new WoodcuttingAction(player, loc, tree));
		}
		// mining
		Node node = Node.forId(id);
		if(node != null && player.getLocation().isWithinInteractionDistance(loc)) {
			player.getActionQueue().addAction(new MiningAction(player, loc, node));
		}
		switch(id) {
            case 2471:
                if(player.isInCombat()) {
                    player.getActionSender().sendMessage("You cannot enter while in combat!");
                    break;
                }
                int absY = 3605;
                absY += player.cE.getAbsY() > absY ? -1 : 1;
                player.setTeleportTarget(Location.create(player.cE.getAbsX(), absY, player.cE.getAbsZ()));
                break;
		case 2470:
			DialogueManager.openDialogue(player, 151);
			break;
			case 14831:
			case 14830:
			case 14832:
			case 14829:
			case 14828:
			case 14826:
			case 14827:
				World.getWorld().getWilderness().useObelisk(player, x, y);
				break;
			case 5110:
				World.getWorld().submit(new Event(100) {
					@Override
					public void execute() {
						if(player.getLocation().getX() == 2649 && player.getLocation().getY() == 9562)
							player.setTeleportTarget(Location.create(2647, 9557, 0));
						if(player.getLocation().getX() == 2647 && player.getLocation().getY() == 9557)
							player.setTeleportTarget(Location.create(2649, 9562, 0));
						this.stop();
					}
				});
				break;
            case 1766:
                player.playAnimation(Animation.create(828));  //ladder climb anim
                World.getWorld().submit(new Event(600) {
                    @Override
                    public void execute() throws IOException {
                        player.setTeleportTarget(Location.create(3017, 3850, 0));
                        this.stop();
                    }
                });
                break;
			case 2213:
			case 2214:
			case 3045:
			case 5276:
			case 6084:
			case 10517:
			case 11338:
			case 11758:
			case 12798:
			case 12799:
			case 12800:
			case 3193:
			case 12801:
				DialogueManager.openDialogue(player, 0);
				break;
			case 3537:
			case 12554:
			case 1738:
				player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY(), 2));
				break;
			case 9398:
				//Bank.openDepositBox(player);
				break;
			case 10230://dag ladder
				player.setTeleportTarget(Location.create(2900, 4449, 0));
				break;
			case 1733:
				player.setTeleportTarget(Location.create((player.getLocation().getX() - 3), (player.getLocation().getY() + 6400), 0));
				break;
            case 1734:
                player.setTeleportTarget(Location.create((player.getLocation().getX() + 3), (player.getLocation().getY() - 6400), 0));
                break;
			case 26384:
				if(player.godWarsKillCount[0] < 40) {
					player.getActionSender().sendMessage("You need to slay 40 Bandos monsters to pass.");
					return;
				}
				player.godWarsKillCount[0] = 0;
				if(player.getLocation().getY() != 5333 || player.getLocation().getX() < 2850 || player.getLocation().getX() > 2851)
					break;
				player.face(Location.create(player.getLocation().getX() <= 2850 ? (player.getLocation().getX() + 1) : (player.getLocation().getX() - 1), y, 2));
				player.playAnimation(Animation.create(7002));
				World.getWorld().submit(new Event(1100) {
					@Override
					public void execute() {
						player.getActionSender().sendReplaceObject(x, y, id, 1, 0);
						player.getWalkingQueue().reset();
						player.getWalkingQueue().addStep(player.getLocation().getX(), player.getLocation().getY());
						player.getWalkingQueue().addStep(player.getLocation().getX() <= 2850 ? (player.getLocation().getX() + 1) : (player.getLocation().getX() - 1), player.getLocation().getY());
						player.getWalkingQueue().finish();
						this.stop();
					}
				});
				World.getWorld().submit(new Event(2100) {
					@Override
					public void execute() {
						player.getActionSender().sendReplaceObject(x, y, id, 0, 0);
						this.stop();
					}
				});
				break;
			case 7353:
				Magic.teleport(player, 3429, 3538, 0, false);
				break;
			case 26303:
				if(player.godWarsKillCount[3] >= 40) {
					player.godWarsKillCount[3] = 0;
					if(player.getLocation().getY() <= 5269)
						player.setTeleportTarget(Location.create(2872, (player.getLocation().getY() + 10), 2));
					else
						player.setTeleportTarget(Location.create(2872, (player.getLocation().getY() - 10), 2));
				} else {
					player.getActionSender().sendMessage("You need to slay 40 Armdayl monsters to pass.");
					return;
				}
	            /*player.getWalkingQueue().reset();
				final int a = player.getAppearance().getStandAnim();
				final int b = player.getAppearance().getWalkAnim();
				final int c = player.getAppearance().getRunAnim();
				player.getAppearance().setAnimations(6067,6067,6067);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

				
				World.getWorld().submit(new Event(600){
					@Override
					public void execute(){
						player.forceWalkX1 = player.getLocation().getX();
						player.forceWalkX2 = player.getLocation().getX();
						player.forceWalkY1 = player.getLocation().getY();
						if(player.getLocation().getY() <= 5269)
							player.forceWalkY2 = (player.getLocation().getY()+10);
						else
							player.forceWalkY2 = (player.getLocation().getY()-10);
						player.forceSpeed1 = 50;
						player.forceSpeed2 = 100;
						if(player.getLocation().getY() <= 5269)
							player.forceDirection = 0;
						else
							player.forceDirection = 2;
						player.getUpdateFlags().flag(UpdateFlag.WALK);
						this.stop();
					}
				});
				World.getWorld().submit(new Event(1250){
					@Override
					public void execute(){
						player.getAppearance().setAnimations(a,b,c);
						player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
						if(player.getLocation().getY() <= 5269){
							player.setTeleportTarget(Location.create(2872,5279,2));
							player.setLocation(Location.create(2872,5279,2));
						}else{
							player.setLocation(Location.create(2872,5269,2));
							player.setTeleportTarget(Location.create(2872,5269,2));
						}
						this.stop();
					}
				});*/
				break;
		}
	}

	public static void objectClickTwo(Player player, int id, int x, int y) {
		Location loc = Location.create(x, y, player.getLocation().getZ());
		Node node = Node.forId(id);
		if(node != null && player.getLocation().isWithinInteractionDistance(loc)) {
			player.getActionQueue().addAction(new ProspectingAction(player, loc, node));
			return;
		}
		switch(id) {
			case 2213:
			case 2214:
			case 3045:
			case 5276:
			case 6084:
			case 10517:
			case 11338:
			case 11758:
			case 12798:
			case 12799:
			case 12800:
			case 3193:
			case 12801:
				Bank.open(player, false);
				break;
		}


	}


    public static boolean canClick(int offsetX, int offsetY, int toLocX, int toLocY, int fromLocX, int fromLocY, int height) {
        int deltaX = Math.abs(toLocX - fromLocX);
        int deltaY = Math.abs(toLocY - fromLocY);
        return  ((deltaX <= offsetX && deltaY <= offsetY) || (deltaX <= offsetY && deltaY <= offsetX));
    }

}