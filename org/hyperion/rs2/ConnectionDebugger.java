package org.hyperion.rs2;

import debug.Debugger;
import org.hyperion.Server;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ConnectionDebugger extends Debugger {

	public static final int MAX_LOGS_SIZE = 1000;

	public static final File LOG_FILE = new File("./logs/connections.log");

	private List<String> logs = new LinkedList<String>();


	public ConnectionDebugger() {
		super(LOG_FILE);
		setEnabled(Server.getConfig().getBoolean("connectiondebugger"));
	}

	@Override
	public void log(String message) {
		if(! isEnabled())
			return;
		if(logs.size() >= MAX_LOGS_SIZE)
			logs.remove(0);
		logs.add(message);
	}

	@Override
	public List<String> getLogs() {
		return logs;
	}

}
