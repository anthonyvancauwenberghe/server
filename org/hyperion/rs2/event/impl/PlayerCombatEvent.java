package org.hyperion.rs2.event.impl;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hyperion.map.pathfinding.Path;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.content.ClickId;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.saving.PlayerSaving;

public class PlayerCombatEvent extends Event {
	
	public PlayerCombatEvent() {
		super(600);
	}
	
	public static long lastTimeDid = System.currentTimeMillis();
	
	
	public synchronized static LinkedList<Player> cloneEntityList() {
		LinkedList<Player> newList = new LinkedList<Player>();
		for(Player p : World.getWorld().getPlayers()) {
			newList.add(p);
		}
		Collections.shuffle(newList);
		return newList;
	}
	
	@Override
	public void execute() {
		cleanList();
		//move all combat events to this event, and npcs seperate, doesn't glitch both
        final long startTime = System.currentTimeMillis();
		List<Player> clonedList = cloneEntityList();
		synchronized(clonedList) {
			//handle all combat first players first
			for(Player player : clonedList) {
				try {
					if(player == null || player.cE == null)
						continue;
					//following for players
					if(player.isFollowing != null) {
						//System.out.println("Following");
						player.cE.face(player.isFollowing.cE.getAbsX()
		                        //+ player.isFollowing.cE.getOffsetX()
                                ,
                                player
								.isFollowing.cE.getAbsY()
								//+ player.cE.getOpponent().getOffsetY()
                        );

						player.setInteractingEntity(player.isFollowing);
						int dis = player.getLocation().distance(player.isFollowing.getLocation());
						if(dis <= 20 && dis > 1) {
							try {
								//Combat.follow(player.cE, player.isFollowing.cE);
								//int startx = player.getLocation().getX();
								//int starty = player.getLocation().getY();

								int toX = player.isFollowing.getLocation().getX();
								int toY = player.isFollowing.getLocation().getY();
								//System.out.println("X : " + startx + " Y : " + starty);
								if(player.isFollowing.getWalkingQueue().getPublicPoint() != null) {
									toX = player.isFollowing.getWalkingQueue().getPublicPoint().getX();
									toY = player.isFollowing.getWalkingQueue().getPublicPoint().getY();
								}
								int baseX = player.getLocation().getX() - 25;
								int baseY = player.getLocation().getY() - 25;
								player.getWalkingQueue().reset();
								player.getWalkingQueue().setRunningQueue(true);
								Path p = World.getWorld().pathTest.getPath(player.getLocation().getX(), player.getLocation().getY(), toX, toY);
								if(p != null) {
									for(int i = 1; i < p.getLength(); i++) {
										//player.getActionSender().sendMessage((baseX+p.getX(i))+"	"+(baseY+p.getY(i)));
										if((baseX + p.getX(i)) != toX || (baseY + p.getY(i)) != toY)
											player.getWalkingQueue().addStep((baseX + p.getX(i)), (baseY + p.getY(i)));
									}
									player.getWalkingQueue().finish();
								} else {
									//System.out.println("Derp");
								}
							} catch(Exception e) {
                                e.printStackTrace();
							}
						}
					}
				} catch(Exception e) {
                    e.printStackTrace();
				} finally {
                    if(!stakeReset(player) && player.cE.getOpponent() != null) {
						if(! Combat.processCombat(player.cE))
							Combat.resetAttack(player.cE);
					}

					player.getWalkingQueue().walkingCheck();
				}
			}
            //final long deltaTime = System.currentTimeMillis() - startTime;
            //if(deltaTime > 50)
              //  System.err.println("[PLAYER COMBAT EVENT]: took: "+(deltaTime) + "ms");
        }
	}
	
	public synchronized static void cleanList() {
		
	}

    public static boolean stakeReset(final Player player) {
        final Player opp = player.getTrader();
        if(opp != null && !opp.isDead() && !player.isDead() && !opp.getSession().isConnected() && !player.getSession().isConnected() && player.duelAttackable > 0 && opp.duelAttackable > 0) {
            PlayerSaving.getSaving().saveLog("./logs/accounts/" + opp.getName(), (new Date()) + " Duel TIE against "+player.getName());
            PlayerSaving.getSaving().saveLog("./logs/accounts/" + player.getName(), (new Date()) + " Duel TIE against " + opp.getName());
            Container.transfer(player.getDuel(), player.getInventory());//jet is a smartie
            Container.transfer(opp.getDuel(), opp.getInventory());
            opp.setTeleportTarget(Location.create(3360 + Combat.random(17), 3274 + Combat.random(3), 0), false);
            player.setTeleportTarget(Location.create(3360 + Combat.random(17), 3274 + Combat.random(3), 0), false);
            return true;
        }
        return false;
    }

}
