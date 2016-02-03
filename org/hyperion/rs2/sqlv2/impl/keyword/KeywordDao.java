package org.hyperion.rs2.sqlv2.impl.keyword;

import org.hyperion.rs2.sqlv2.dao.SqlDao;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(KeywordMapper.class)
public interface KeywordDao extends SqlDao {

    @SqlQuery("SELECT * FROM keywords")
    List<Keyword> all();

    @SqlUpdate("DELETE FROM keywords WHERE keyword = :name AND id = :id")
    int delete(@Bind("name") final String name, @Bind("id") final int id);

    @SqlUpdate("INSERT INTO keywords (keyword, id) VALUES (:name, :id)")
    int insert(@Bind("name") final String name, @Bind("id") final int id);
}
