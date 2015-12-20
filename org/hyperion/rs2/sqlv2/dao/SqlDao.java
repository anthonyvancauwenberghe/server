package org.hyperion.rs2.sqlv2.dao;

import java.io.Closeable;

public interface SqlDao extends Closeable {

    void close();
}
