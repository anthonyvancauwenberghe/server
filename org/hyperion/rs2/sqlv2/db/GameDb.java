package org.hyperion.rs2.sqlv2.db;

import org.hyperion.rs2.sqlv2.impl.keyword.Keywords;

/**
 * Created by Gilles on 3/02/2016.
 */
public class GameDb extends Db {

    private Keywords keywords;

    public GameDb(DbConfig config) {
        super(config);
    }

    public Keywords getKeywords() {
        return keywords;
    }

    @Override
    protected void postInit() {
        keywords = new Keywords(this);
        Keywords.initCache();
    }
}
