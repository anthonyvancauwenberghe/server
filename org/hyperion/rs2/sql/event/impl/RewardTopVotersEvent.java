package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLStartupEvent;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.sql.ResultSet;
import java.util.LinkedList;

public class RewardTopVotersEvent extends SQLStartupEvent {

	public static final int[] DONATOR_POINT_REWARDS = {
			50,
			35,
			30,
			25,
			20,
			15,
			10,
			8,
			6,
			5,
	};

	@Override
	public void run(SQLConnection sql) {
		try {
			ResultSet rs = sql.query("SELECT value FROM settings WHERE variable = 'last_top_rewards'");
			if(rs != null && rs.next()) {
				String value = rs.getString(1);
				rs.close();
				long time = Long.parseLong(value);
				if(System.currentTimeMillis() - time > Time.ONE_MONTH) {
					rs = sql.query("SELECT name FROM votes ORDER BY claimed DESC LIMIT 10");
					LinkedList<String> names = new LinkedList<String>();
					while(rs.next()) {
						names.add(rs.getString(1));
					}
					rs.close();
					rs = sql.query("SELECT COUNT(TOKEN_ID) AS count FROM donator");
					if(rs != null && rs.next()) {
						int rows = rs.getInt(1);
						rs.close();
						int r = Misc.random(100000);
						int index = 0;
						for(String name : names) {
							StringBuilder sb = new StringBuilder();
							sb.append("INSERT INTO donator(TOKEN_ID,name,amount,finished,row) VALUES(");
							sb.append("'voter" + r + "',");
							sb.append("'" + name + "',");
							sb.append(DONATOR_POINT_REWARDS[index++] + ",");
							sb.append("0,");
							sb.append(rows + index + 1 + ")");
							sql.query(sb.toString());
							//System.out.println(sb.toString());
						}
						sql.query("UPDATE votes SET claimed = 0");
						sql.query("UPDATE settings SET value= '" + System.currentTimeMillis() + "' WHERE variable = 'last_top_rewards'");
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
