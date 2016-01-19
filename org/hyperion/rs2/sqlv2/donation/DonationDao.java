package org.hyperion.rs2.sqlv2.donation;

import org.hyperion.rs2.sqlv2.dao.SqlDao;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(DonationMapper.class)
public interface DonationDao extends SqlDao {

    @SqlQuery("SELECT * FROM donations WHERE name = :name AND finished = :finished")
    List<Donation> get(@Bind("name") final String name, @Bind("finished") final boolean finished);

    @SqlUpdate("UPDATE donations SET finished = 1 WHERE index = :index")
    int finish(@Bind("index") final int index);
}
