package org.hyperion.rs2.action.impl;

import org.hyperion.rs2.action.impl.MiningAction.Node;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Position;

public class ProspectingAction extends InspectAction {

	/**
	 * The node type.
	 */
	private Node node;

	/**
	 * The delay.
	 */
	private static final int DELAY = 3000;

	/**
	 * Constructor.
	 *
	 * @param player
	 * @param position
	 * @param node
	 */
	public ProspectingAction(Player player, Position position, Node node) {
		super(player, position);
		this.node = node;
	}

	@Override
	public long getInspectDelay() {
		return DELAY;
	}

	@Override
	public void init() {
		final Player player = getPlayer();
		player.getActionSender().sendMessage("You examine the rock for ores...");
	}

	@Override
	public void giveRewards(Player player) {
		player.getActionSender().sendMessage("This rock contains "
				+ ItemDefinition.forId(node.getOreId()).getName().toLowerCase().replaceAll("ore", "").trim() + ".");
	}

}