package org.hyperion.rs2.sqlv2.keyword;

import org.hyperion.rs2.sqlv2.DbHub;
import org.hyperion.rs2.sqlv2.dao.SqlDaoManager;
import org.hyperion.rs2.sqlv2.db.Db;
import org.hyperion.rs2.sqlv2.db.DbConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keywords extends SqlDaoManager<KeywordDao> {

    private static final Map<Object, Keyword> cache = new HashMap<>();

    public Keywords(final Db db) {
        super(db, KeywordDao.class);
    }

    public List<Keyword> all() {
        try{
            return dao.all();
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return null;
        }
    }

    public boolean insert(final String name, final int id) {
        try(final KeywordDao dao = open()){
            return dao.insert(name, id) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean insert(final Keyword k) {
        return insert(k.name(), k.id());
    }

    public boolean delete(final String name, final int id) {
        try(final KeywordDao dao = open()){
            return dao.delete(name, id) == 1;
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(final Keyword k) {
        return delete(k.name(), k.id());
    }

    public static Map<Object, Keyword> cache() {
        return cache;
    }

    public static boolean initCache() {
        if(!DbHub.initialized() || !DbHub.donationsDb().enabled())
            return false;
        final List<Keyword> all = DbHub.donationsDb().keywords().all();
        if(all == null)
            return false;
        all.forEach(Keywords::cache);
        return true;
    }

    public static boolean reloadCache() {
        cache.clear();
        return initCache();
    }

    public static int cacheSize() {
        return cache.size();
    }

    public static void cache(final Keyword k) {
        cache.put(k.name(), k);
        cache.put(k.id(), k);
    }

    public static void uncache(final Keyword k) {
        cache.remove(k.name());
        cache.remove(k.id());
    }

    public static Keyword cacheGet(final Object nameOrId) {
        return cache.get(nameOrId);
    }
}
