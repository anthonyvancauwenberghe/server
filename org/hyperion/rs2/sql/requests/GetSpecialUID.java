package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 4/26/15
 * Time: 2:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetSpecialUID  extends SQLRequest {

    final int[] specialUid;
    final String name;

    public GetSpecialUID(final String name,final int[] specialUid) {
        super(QUERY_REQUEST);
        this.specialUid = specialUid;
        this.name = name;
    }

    @Override
    public void process(SQLConnection sql) throws SQLException {
        try {
            final ResultSet set = sql.query("SELECT suid FROM accountvalues WHERE name='"+name+"'");
            while(set.next()) {
                String[] strings = set.getString("suid").replace("[", "").replace("]", "").split(", ");
                for (int i = 0; i < strings.length; i++) {
                    specialUid[i] = Integer.parseInt(strings[i]);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
