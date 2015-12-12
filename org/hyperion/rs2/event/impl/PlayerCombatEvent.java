package org.hyperion.rs2.event.impl;

import org.hyperion.map.pathfinding.Path;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.saving.PlayerSaving;

import java.util.Date;

public class PlayerCombatEvent extends Event {

    public static long lastTimeDid = System.currentTimeMillis();

    public PlayerCombatEvent() {
        super(600, "playercb");
    }

    public synchronized static Player[] cloneEntityList() {
        return World.getWorld().getPlayers().stream().toArray(Player[]::new);
    }

    public synchronized static void cleanList() {

    }

    public static boolean stakeReset(final Player player) {
        final Player opp = player.getTrader();
        if(opp != null && !opp.isDead() && !player.isDead() && !opp.getSession().isConnected() && !player.getSession().isConnected() && player.duelAttackable > 0 && opp.duelAttackable > 0){
            PlayerSaving.getSaving().saveLog("./logs/accounts/" + opp.getName(), (new Date()) + " Duel TIE against " + player.getName());
            PlayerSaving.getSaving().saveLog("./logs/accounts/" + player.getName(), (new Date()) + " Duel TIE against " + opp.getName());
            Container.transfer(player.getDuel(), player.getInventory());//jet is a smartie
            Container.transfer(opp.getDuel(), opp.getInventory());
            opp.setTeleportTarget(Location.create(3360 + Combat.random(17), 3274 + Combat.random(3), 0), false);
            player.setTeleportTarget(Location.create(3360 + Combat.random(17), 3274 + Combat.random(3), 0), false);
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        //move all combat events to this event, and npcs seperate, doesn't glitch both
        final long startTime = System.currentTimeMillis();
        final Player[] players = cloneEntityList();
        //handle all combat first players first
        final int size = players.length;
        final int startPoint = Combat.random(size - 1);
        for(int pid = startPoint; pid < (size + startPoint); pid++){
            final Player player = players[pid % size];
            try{
                if(player == null || player.cE == null){
                    System.err.println("ERROR SEVERE: NULL PLAYER");
                    continue;
                }
                //following for players
                if(player.isFollowing != null){
                    //System.out.println("Following");

                    final int dis = player.getLocation().distance(player.isFollowing.getLocation());
                    if(dis <= 20 && dis > 1){
                        try{
                            int toX = player.isFollowing.getLocation().getX();
                            int toY = player.isFollowing.getLocation().getY();
                            //System.out.println("X : " + startx + " Y : " + starty);
                            if(player.isFollowing.getWalkingQueue().getPublicPoint() != null){
                                toX = player.isFollowing.getWalkingQueue().getPublicPoint().getX();
                                toY = player.isFollowing.getWalkingQueue().getPublicPoint().getY();
                            }
                            final int baseX = player.getLocation().getX() - 25;
                            final int baseY = player.getLocation().getY() - 25;
                            player.getWalkingQueue().reset();
                            player.getWalkingQueue().setRunningQueue(true);
                            final Path p = World.getWorld().pathTest.getPath(player.getLocation().getX(), player.getLocation().getY(), toX, toY);
                            if(p != null){
                                for(int i = 1; i < p.getLength(); i++){
                                    if((baseX + p.getX(i)) != toX || (baseY + p.getY(i)) != toY)
                                        player.getWalkingQueue().addStep((baseX + p.getX(i)), (baseY + p.getY(i)));
                                }
                                player.getWalkingQueue().finish();
                            }
                        }catch(final Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }catch(final Exception e){
                e.printStackTrace();
            }finally{
                if(!stakeReset(player) && player.cE.getOpponent() != null){
                    if(!Combat.processCombat(player.cE))
                        Combat.resetAttack(player.cE);
                }

                player.getWalkingQueue().walkingCheck();
            }
        }
    }

}
