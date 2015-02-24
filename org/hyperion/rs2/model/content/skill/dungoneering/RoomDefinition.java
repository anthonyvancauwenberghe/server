package org.hyperion.rs2.model.content.skill.dungoneering;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.util.IoBufferUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/20/15
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoomDefinition {

    public static final List<RoomDefinition> ROOM_DEFINITIONS_LIST = new ArrayList<>();

    public final int x, y;
    public final List<Point> spawnLocations;

    public RoomDefinition(final int x, final int y, List<Point> spawnLocations) {
        this.x = x;
        this.y = y;
        this.spawnLocations = spawnLocations;
        ROOM_DEFINITIONS_LIST.add(this);
    }

    public final Room getRoom(final Dungeon dungeon) {
        return new Room(dungeon, this);
    }

    public String toString() {
        return String.format("LocX: %d LocY: %d Size: %d", x, y, spawnLocations.size());
    }

    public void save(final IoBuffer buffer) {
        buffer.putShort((short) x);
        buffer.putShort((short) y);

        buffer.put((byte)spawnLocations.size());

        for(final Point entries : spawnLocations) {
            buffer.putShort((short) entries.x);
            buffer.putShort((short) entries.y);
        }

    }

    public static void load() {
        try {
            File f = new File("./data/roomdef.bin");
            InputStream is = new FileInputStream(f);
            IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            while(true) {
                byte[] temp = new byte[1024];
                int read = is.read(temp, 0, temp.length);
                if(read == - 1) {
                    break;
                } else {
                    buf.put(temp, 0, read);
                }
            }
            buf.flip();
            int defs = 0;
            while(buf.hasRemaining()) {
                try {
                    int x = buf.getUnsignedShort();
                    int y = buf.getUnsignedShort();

                    int locs = buf.getUnsigned();

                    final List<Point> points = new ArrayList<>();
                    for(int i = 0; i < locs; i++) {
                        points.add(new Point(buf.getUnsignedShort(), buf.getUnsignedShort()));
                    }

                    System.out.println(new RoomDefinition(x, y, points));
                    defs++;
                } catch(Exception ex) {

                }
            }

            System.out.println("Loaded "+defs+" Room Definitions");
        }catch(final Exception ex) {

        }
    }


}
