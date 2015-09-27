package org.hyperion.rs2.sql;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.hyperion.Configuration;

public class ImportantPlayerConnection extends MySQLConnection {

    public ImportantPlayerConnection(Configuration config) {
        super("ImportantSQL", config.getString("playersurl"), config.getString("playersuser"), config.getString("playerspass"), 5000, 500, 0);
    }

    @Override
    public boolean init() {
        establishConnection();
        this.start();
        return true;
    }

    private ArrayList<String> list = new ArrayList<String>(100);
	
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
