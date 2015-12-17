package org.hyperion.rs2.sql.requests;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Highscores;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HighscoresRequest extends SQLRequest {

	/**
	 * The highscores table name.
	 */
	public static final String HIGHSCORES_TABLE = Server.getConfig().getString("hstable");

	private Highscores highscores;

	public HighscoresRequest(Highscores highscores) {
		super(SQLRequest.QUERY_REQUEST);
		this.highscores = highscores;
	}

	@Override
	public void process(SQLConnection sql) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM " + HIGHSCORES_TABLE + " WHERE Name = '" + highscores.getName() + "'");
		if(rs != null && rs.next()) {
			sql.query(highscores.getUpdateQuery());
		} else {
			sql.query(highscores.getInsertQuery());
		}
	}


	public static void main(String... args) {
		int[] exps = new int[Highscores.SKILLS_COLUMN_NAMES.length];
		exps[10] = 1337;
		Highscores highscores = new Highscores(null, "graham", 100, exps, 100, 100);
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + HIGHSCORES_TABLE + " (");
		for(String skill : Highscores.SKILLS_COLUMN_NAMES) {
			sb.append(skill).append(",");
		}
		sb.append("Total,Overall,elo,Name) VALUES (");
		for(int exp : highscores.getExps()) {
			sb.append(exp).append(",");
		}
		sb.append(highscores.getTotalLevel() + "," + highscores.getOverallExp() + "," + highscores.getElo() + ",'" + highscores.getName() + "')");
		System.out.println(sb.toString());
	}

	static {
		CommandHandler.submit(new Command("saveallhighscores", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage("Saving highscores");
				for(Player p : World.getWorld().getPlayers()) {
					if(p.getHighscores().needsUpdate()) {
						if (Server.getConfig().getBoolean("logssql"))
							World.getWorld().getLogsConnection().offer(new HighscoresRequest(p.getHighscores()));
					}
				}
				return true;
			}
		});
	}
}
