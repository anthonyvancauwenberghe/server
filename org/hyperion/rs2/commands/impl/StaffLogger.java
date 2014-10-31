package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.saving.PlayerSaving;

import java.io.File;
import java.util.Date;

/**
 * @author Arsen Maxyutov
 */
public class StaffLogger {

	public static final File LOG_FILE = new File("./logs/stafflog.log");

	public static StaffLogger getLogger() {
		return singleton;
	}

	private static StaffLogger singleton = new StaffLogger();

	public void log(String line) {

		PlayerSaving.getSaving().saveLog(LOG_FILE, new Date() + "\t" + line);
	}
}
