package org.hyperion.sql.impl.donation;

import org.hyperion.rs2.model.Player;
import org.hyperion.sql.DbHub;
import org.hyperion.sql.dao.SqlDaoManager;
import org.hyperion.sql.db.Db;

import java.util.List;

public class Donations extends SqlDaoManager<DonationDao> {

    public Donations(final Db db) {
        super(db, DonationDao.class);
    }

    public List<Donation> get(final String name, final boolean finished) {
        try{
            return dao.get(name, finished);
        } catch(Exception ex){
            if(DbHub.isConsoleDebug())
                ex.printStackTrace();
            return null;
        }
    }

    public List<Donation> get(final Player player, final boolean finished) {
        return get(player.getName().toLowerCase().replace("_", " "), finished);
    }

    public List<Donation> finished(final String name) {
        return get(name, true);
    }

    public List<Donation> finished(final Player player) {
        return get(player, true);
    }

    public List<Donation> unfinished(final String name) {
        return get(name, false);
    }

    public List<Donation> unfinished(final Player player) {
        return get(player, false);
    }

    public boolean finish(final Donation d) {
        try(final DonationDao dao = open()){
            return dao.finish(d.index()) == 1;
        }catch(Exception ex){
            if(DbHub.isConsoleDebug())
                ex.printStackTrace();
            return false;
        }
    }
}