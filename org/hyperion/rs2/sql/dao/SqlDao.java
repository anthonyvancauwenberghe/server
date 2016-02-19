package org.hyperion.rs2.sql.dao;

import java.io.Closeable;

public interface SqlDao extends Closeable {

    void close();
}
