package org.hyperion.rs2.sql;

import org.hyperion.Configuration;
import org.hyperion.Server;

import java.util.ArrayList;

public class ImportantPlayerConnection extends MySQLConnection {

	private final ArrayList<String> list = new ArrayList<String>(100);

	public ImportantPlayerConnection(final Configuration config) {
		super("ImportantSQL", config.getString("playersurl"), config.getString("playersuser"), config.getString("playerspass"), 5000, 500, 0);
    }

    @Override
    public boolean init() {
		if(!Server.getConfig().getBoolean("sql"))
			return false;
		establishConnection();
        this.start();
        return true;
    }

	/*public void offer(SQLRequest request) {
		list.add(request.toString());
		if(list.size() > 100) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("./data/impsql.log", true));
				for(String s: list) {
					out.write(s);
					out.newLine();
				}
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.clear();
		}
		super.offer(request);
	}*/


}
