package org.hyperion.rs2.sqlv2.impl.keyword;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KeywordMapper implements ResultSetMapper<Keyword> {

    @Override
    public Keyword map(final int i, final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new Keyword(rs.getString("keyword"), rs.getInt("id"));
    }
}
