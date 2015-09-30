package org.hyperion.rs2.sql;

import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.rs2.sql.event.impl.RewardTopVotersEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class PlayersSQLConnection extends MySQLConnection {

	public PlayersSQLConnection(Configuration config) {
		super("PlayersMySQL", config.getString("playersurl"), config.getString("playersuser"), config.getString("playerspass"), 5000, 500, 0);
	}


	private LinkedList<String> list = new LinkedList<String>();

	private void checkString(String query) {
		list.add(query);
		if(list.size() >= 1000) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("./data/playersql.log", true));
				for(String s: list) {
					out.write(s);
					out.newLine();
				}
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			list.clear();
		}
	}

	public ResultSet query(String s) throws SQLException {
		/*if(s.toLowerCase().contains("insert into"))
			checkString(s);*/
		return super.query(s);
	}

	public static void main(String... args) throws IOException, SQLException {
		PlayersSQLConnection conn = new PlayersSQLConnection(Server.getConfig());
		conn.init();
		conn.startupQueries();
		//Code reading from .sql file and inserting in SQLite
		/* 
		BufferedReader in = new BufferedReader(new FileReader("C:/Users/Saosin Hax/Desktop/SQLite/dump.sql"));
		String line;
		String insertLine = "";
		System.out.println(DB_FILE_NAME);
		LinkedList<String> queries = new LinkedList<String>();
		while((line = in.readLine()) != null) {
			
			line = line.replaceAll("`", "");
			line = line.replaceAll("ENGINE=MyISAM DEFAULT CHARSET=latin1", "");
			line = line.replaceAll("IF NOT EXISTS", "");
			if(line.startsWith("--"))
				continue;
			if(line.startsWith("INSERT")) {
				insertLine = line;
			} else if(line.startsWith("CREATE")) {
				StringBuilder sb = new StringBuilder(line);
				while(true) {
					line = in.readLine();
					line = line.replaceAll("`", "");
					line = line.replaceAll("ENGINE=MyISAM DEFAULT CHARSET=latin1", "");
					line = line.replaceAll("IF NOT EXISTS", "");
					
					sb.append(line);
					if(line.endsWith(";")) {
						//System.out.println(sb.toString());
						queries.add(sb.toString());
						break;
					}
				}
			} else {
				if(line.length() < 1)
					continue;
				String query = insertLine + line;
				if(line.endsWith(","))
					query = query.substring(0, query.length() - 1);
				//queries.add(query);
			}
		}
		in.close();
		long start = System.currentTimeMillis();
		for(String query: queries) {
			conn.query(query);
		}
		long delta = System.currentTimeMillis() - start;
		*/
	}

	public boolean startupQueries() {

		return true;
		/*try {
			long start = System.currentTimeMillis();
			query("CREATE TABLE IF NOT EXISTS `player_data` (  `name` varchar(12) NOT NULL,  `pass` varchar(30) NOT NULL,  `ip` varchar(30) NOT NULL,  `rights` int(11) NOT NULL,  `status` int(11) NOT NULL,  `createdstr` varchar(50) NOT NULL,  `location` varchar(30) NOT NULL,  `elo` int(11) NOT NULL,  `diced` int(11) NOT NULL,  `createdlong` bigint(20) NOT NULL,  `spec` int(11) NOT NULL,  `atktype` int(11) NOT NULL,  `magicbook` int(11) NOT NULL,  `xplock` tinyint(1) NOT NULL,  `trivia` tinyint(1) NOT NULL,  `altar` int(11) NOT NULL,  `clan` varchar(12) NOT NULL,  `donatorsbought` int(11) NOT NULL,  `donatorpoints` int(11) NOT NULL,  `pkpoints` int(11) NOT NULL,  `votepoints` int(11) NOT NULL,  `skull` int(11) NOT NULL,  `ep` int(11) NOT NULL,  `armakc` int(11) NOT NULL,  `bandkc` int(11) NOT NULL,  `zammykc` int(11) NOT NULL,  `sarakc` int(11) NOT NULL,  `slayertask` int(11) NOT NULL,  `taskamount` int(11) NOT NULL,  `killcount` int(11) NOT NULL,  `deathcount` int(11) NOT NULL,  `cleaned` tinyint(1) NOT NULL,  `fightcaveswave` int(11) NOT NULL)");
			query("CREATE TABLE IF NOT EXISTS `player_bank` (  `id` int(11) NOT NULL,  `amount` int(11) NOT NULL,  `slot` int(11) NOT NULL,  `username` varchar(12) NOT NULL)");
			query("CREATE TABLE IF NOT EXISTS `player_equipment` (  `id` int(11) NOT NULL,  `amount` int(11) NOT NULL,  `slot` int(11) NOT NULL,  `username` varchar(12) NOT NULL)");
			query("CREATE TABLE IF NOT EXISTS `player_friends` (  `friend` bigint(20) NOT NULL,  `slot` int(11) NOT NULL,  `username` varchar(12) NOT NULL)");
			query("CREATE TABLE IF NOT EXISTS `player_inventory` (  `id` int(11) NOT NULL,  `amount` int(11) NOT NULL,  `slot` int(11) NOT NULL,  `username` varchar(12) NOT NULL)");
			query("CREATE TABLE IF NOT EXISTS `player_look` (  `slot` int(11) NOT NULL,  `value` int(11) NOT NULL,  `username` varchar(12) NOT NULL)");
			query("CREATE TABLE IF NOT EXISTS `player_skills` (  `skill` int(11) NOT NULL,  `level` int(11) NOT NULL,  `exp` int(11) NOT NULL,  `username` varchar(12) NOT NULL)");
			//query("INSERT INTO `player_bank` (`id`,`amount`,`slot`,`username`) VALUES (0,0,0,'graham'),(0,0,1,'graham'),(0,0,2,'graham');");
			long delta = System.currentTimeMillis() - start;
			//System.out.println("Delta: " + delta + " ms  .");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}*/
	}

	@Override
	public boolean init() {
		establishConnection();
		this.start();
		return true;
	}
}
