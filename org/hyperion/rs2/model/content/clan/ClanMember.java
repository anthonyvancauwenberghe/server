package org.hyperion.rs2.model.content.clan;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.util.IoBufferUtils;

public class ClanMember {
    private String name;
    private int rank;

    public ClanMember(final String name, final int rank) {
        this.setName(name);
        this.setRank(rank);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public void save(final IoBuffer buffer) {
        IoBufferUtils.putRS2String(buffer, name);
        buffer.put((byte) rank);
    }
}
