package org.hyperion.rs2.event.impl;


import org.hyperion.rs2.event.Event;


/**
 * Summ Follow
 */
public class SummoningEvent extends Event {

	/**
	 * The delay in milliseconds between consecutive execution.
	 */
	public static final int CYCLETIME = 600;


	/**
	 * Creates the event each 600 milliseconds.
	 */
	public SummoningEvent() {
		super(CYCLETIME);
	}

	public static int count = 0;

	@Override
	public void execute() {
		//SummoningMonsters.runEvent(count);
		count++;
	}

}
