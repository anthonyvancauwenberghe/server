package org.hyperion.rs2.task;

import org.hyperion.engine.GameEngine;

import java.util.LinkedList;
import java.util.List;

/**
 * A task which executes a group of tasks in a guaranteed sequence.
 *
 * @author Graham Edgecombe
 */
public class ConsecutiveTask implements Task {

	/**
	 * The tasks.
	 */
	private List<Task> tasks;

	/**
	 * Creates the consecutive task.
	 *
	 * @param tasks The child tasks to execute.
	 */
	public ConsecutiveTask(Task... tasks) {
		List<Task> taskList = new LinkedList<Task>();
		for(Task task : tasks) {
			taskList.add(task);
		}
		this.tasks = taskList;
	}

	public ConsecutiveTask(List<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public void execute(GameEngine context) {
		for(Task task : tasks) {
			task.execute(context);
		}
	}

}
