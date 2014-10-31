package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.duel.DuelRule.DuelRules;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.net.Packet;

/**
 * A packet which handles walking requests.
 *
 * @author Graham Edgecombe
 */
public class WalkingPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		if(player.inGame) {
			player.getActionSender().sendMessage("You cannot move in this game.");
			return;
		}
		if(player.getAgility().isRunning()) {
			player.getActionSender().sendMessage("You're a bit busy to be walking around!");
			return;
		}
		if(player.inAction) {
			player.inAction = false;
			ContentEntity.startAnimation(player, - 1);
		}
		if(player.duelRule[DuelRules.MOVEMENT.ordinal()] && player.duelWith2 != null) {
			player.getActionSender().sendMessage("You cannot move in this duel.");
			return;
		}
		int size = packet.getLength();
		if(packet.getOpcode() == 248) {
			size -= 14;
		}
		if(packet.getOpcode() != 99)
			player.isFollowing = null;
		player.cE.deleteSpellAttack();
		if(player.isDead())
			return;
		if(! player.cE.canMove()) {
			if(player.isFollowing != null && packet.getOpcode() != 99)
				player.getActionSender().sendMessage("You are currently frozen.");
			return;
		}
		if(player.cE.getAttackers().size() > 1 && System.currentTimeMillis() - player.cE.lastHit > 10000)
			player.cE.getAttackers().clear();
		if(player.isFollowing == null && packet.getOpcode() != 99) {
			Combat.resetAttack(player.cE);
		}
		player.getInterfaceState().resetInterfaces();
		if(player.isBusy()) {
			player.playAnimation(Animation.create(- 1, 0));
			player.setBusy(false);
		}
		player.getWalkingQueue().reset();
		player.getActionQueue().clearNonWalkableActions();
		player.resetInteractingEntity();

		final int steps = (size - 5) / 2;
		if(steps < 0)
			return;
		final int[][] path = new int[steps][2];

		final int firstX = packet.getLEShortA();
		for(int i = 0; i < steps; i++) {
			path[i][0] = packet.getByte();
			path[i][1] = packet.getByte();
		}
		final int firstY = packet.getLEShort();
		final boolean runSteps = packet.getByteC() == 1;
	    /*if((steps-1) < 0)
			return;*/
		/*int toX = path[(steps-1)][0]+firstX;
        int toY = path[(steps-1)][1]+firstY;
        int baseX = player.getLocation().getX()-PathTest.maxRegionSize;
        int baseY = player.getLocation().getY()-PathTest.maxRegionSize;
        player.getWalkingQueue().setRunningQueue(runSteps);
        Path p = World.getWorld().pathTest.getPath(player.getLocation().getX(), player.getLocation().getY(), toX, toY);
        if(p == null)
        	return;
        for(int i = 0; i < p.getLength(); i++){
        	//player.getActionSender().sendMessage((baseX+p.getX(i))+"	"+(baseY+p.getY(i)));
        	player.getWalkingQueue().addStep((baseX+p.getX(i)),(baseY+p.getY(i)));
        }
        player.getWalkingQueue().finish();*/
		
		/*int toX = path[(steps-1)][0]+firstX;
        int toY = path[(steps-1)][1]+firstY;
        int xLength = toX - player.getLocation().getX();
        int yLength = toY - player.getLocation().getY();
        if(xLength < 0)
        	xLength *= -1;
        if(yLength < 0)
        	yLength *= -1;
        org.hyperion.map.Region.p = player;
        org.hyperion.map.Region.findRoute(toX, toY, false, xLength, yLength);*/

		if(player.debug) {
			int toX = path[(steps-1)][0]+firstX;
	        int toY = path[(steps-1)][1]+firstY;
	        player.setTeleportTarget(Location.create(toX,  toY, player.getLocation().getZ()));
	        player.getWalkingQueue().finish();
	        return;
		}
		player.getWalkingQueue().setRunningQueue(runSteps);
		player.getWalkingQueue().addStep(firstX, firstY);

		for(int i = 0; i < steps; i++) {
			path[i][0] += firstX;
			path[i][1] += firstY;
			player.getWalkingQueue().addStep(path[i][0], path[i][1]);
		}
		player.getWalkingQueue().finish();

	}

}
