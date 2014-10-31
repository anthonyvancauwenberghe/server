package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arsen Maxyutov.
 */
public class MailRequest extends SQLRequest {

	/**
	 * Constructs a new mail request for the specified player.
	 *
	 * @param player
	 */
	public MailRequest(Player player) {
		super(SQLRequest.MAIL_REQUEST);
		super.setPlayer(player);
	}

	/**
	 * Processes the ::setmail request.
	 */
	@Override
	public void process(SQLConnection sql) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM `recoverymails` WHERE name = '"
				+ player.getName().toLowerCase() + "'");
		if(rs == null) {
			setMail(sql);
		} else {
			int counter = 0;
			while(rs.next()) {
				counter++;
			}
			if(counter > 0)
				player.getActionSender().sendMessage(
						"Your e-mail had already been set earlier.");
			else {
				setMail(sql);
			}
		}
	}


	/**
	 * Does the actual query and confirms it to the player.
	 *
	 * @param player
	 * @throws SQLException
	 */
	private void setMail(SQLConnection sql) throws SQLException {
		// System.out.println(player.getMail());
		sql.query("INSERT INTO `recoverymails`(`name`, `mail`) VALUES ('"
				+ player.getName().toLowerCase() + "','" + player.getMail()
				+ "')");
		player.getActionSender().sendMessage(
				"Your e-mail has been succesfully set!");
	}
}
