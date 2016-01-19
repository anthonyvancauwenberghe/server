package org.hyperion.rs2.sql.event.impl;

import org.hyperion.Server;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.util.PlayerFiles;
import org.hyperion.util.Time;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class RecoveryEvent extends SQLEvent {

	/**
	 * The time between <code>RecoveryCheck</code> events.
	 */
	public static final long DELAY = Time.THIRTY_SECONDS;

	/**
	 * Constructs a new SQLEvent.
	 */
	public RecoveryEvent() {
		super(DELAY);
	}

	/**
	 * Checks for account password changing requests
	 * and changes password if needed.
	 */
	public void execute(SQLConnection sql) throws SQLException {
		/*ResultSet rs = sql.query("SELECT * FROM `recoverymails` WHERE newpass != 'none'");
		if(rs != null) {
			int counter = 0;
			while(rs.next()) {
				if(counter++ > 5) {
					sql.stopRunning();
					World.getWorld().submit(getHackEvent());
					return;
				}
				String username = rs.getString("name");
				String newpass = rs.getString("newpass");
				Player player = World.getWorld().getPlayer(username);
				System.out.println("Changing pass of : " + username);
				sql.query("UPDATE `recoverymails` SET newpass='none' WHERE name = '"
						+ username + "'");
				if(player != null) {
					player.setPassword(newpass);
					PlayerFiles.saveGame(player);
					player.getActionSender().sendLogout();
				} else {
					writePassInFile(username, newpass);
				}
			}
		}*/
		super.updateStartTime();
	}


	/**
	 * Changes the password in the character file of a player.
	 *
	 * @param name
	 * @param pass
	 */
	private void writePassInFile(String name, String pass) {
		if (Server.getConfig().getBoolean("logssql"))
			World.getWorld().getLogsConnection().writeLog("Changing pass for " + name + "," + pass);
		try {
			String fileName = "./data/characters/mergedchars/" + name + ".txt";
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			LinkedList<String> lines = new LinkedList<String>();
			String line = "";
			while((line = br.readLine()) != null) {
				if(line.startsWith("character-password"))
					line = "character-password = " + pass;
				lines.add(line);
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			for(String s : lines) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	public static Event getHackEvent() {
		return new Event(1000) {
			@Override
			public void execute() {
				System.out.println("Hack Attempt too many recoveries!!! ");
			}
		};
	}
}

