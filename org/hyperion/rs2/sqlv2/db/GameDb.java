package org.hyperion.rs2.sqlv2.db;

import org.hyperion.rs2.sqlv2.impl.keyword.Keywords;
import org.hyperion.rs2.sqlv2.impl.punishments.Punishments;

/**
 * Created by Gilles on 3/02/2016.
 */
public class GameDb extends Db {

    private Keywords keywords;
    private Punishments punishment;

    public GameDb(DbConfig config) {
        super(config);
    }

    public Keywords getKeywords() {
        return keywords;
    }

    public Punishments getPunishment() {
        return punishment;
    }

    @Override
    protected void postInit() {
        keywords = new Keywords(this);
        punishment = new Punishments(this);
        Keywords.initCache();
    }
}
