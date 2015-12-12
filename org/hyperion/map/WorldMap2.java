package org.hyperion.map;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class WorldMap2 {

    public static final int WIDTH = 12000;
    public static final int HEIGHT = 9900;

    private static final TiledMap map = new TiledMap(WIDTH, HEIGHT);
    public static Map<Integer, Object> thiefstalls = new HashMap<Integer, Object>();

    public static void loadWorldMap(final boolean b) {
        try{
            initialize();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    //public static boolean traversable(int x, int y, int direction) {
    public static boolean isWalkAble(final int height, final int x, final int y, final int toAbsX, final int toAbsY, final int check) {
        final int flag = map.getFlag(x, y);
        final int direction = direction(x, y, toAbsX, toAbsY);
        if(direction == 0 && (flag == 1 || flag == 4 || flag == 6 || flag == 7 || flag == 9 || flag == 11 || flag == 13 || flag == 14)){
            return false;
        }else if(direction == 4 && (flag == 1 || flag == 7 || flag == 15 || flag == 10 || flag == 11 || flag == 12 || flag == 14 || flag == 5)){
            return false;
        }else if(direction == 8 && (flag == 1 || flag == 2 || flag == 3 || flag == 4 || flag == 5 || flag == 6 || flag == 7 || flag == 12)){
            return false;
        }else if(direction == 12 && (flag == 1 || flag == 3 || flag == 6 || flag == 9 || flag == 10 || flag == 11 || flag == 12 || flag == 8)){
            return false;
        }else if(flag > 0 && flag < 15){
            return false;
        }
        return true;
    }

    public static int direction(final int srcX, final int srcY, final int destX, final int destY) {
        final int dx = destX - srcX;
        final int dy = destY - srcY;
        // a lot of cases that have to be considered here ... is there a more
        // sophisticated (and quick!) way?
        if(dx < 0){
            if(dy < 0){
                if(dx < dy)
                    return 11;
                else if(dx > dy)
                    return 9;
                else
                    return 10; // dx == dy
            }else if(dy > 0){
                if(-dx < dy)
                    return 15;
                else if(-dx > dy)
                    return 13;
                else
                    return 14; // -dx == dy
            }else{ // dy == 0
                return 12;
            }
        }else if(dx > 0){
            if(dy < 0){
                if(dx < -dy)
                    return 7;
                else if(dx > -dy)
                    return 5;
                else
                    return 6; // dx == -dy
            }else if(dy > 0){
                if(dx < dy)
                    return 1;
                else if(dx > dy)
                    return 3;
                else
                    return 2; // dx == dy
            }else{ // dy == 0
                return 4;
            }
        }else{ // dx == 0
            if(dy < 0){
                return 8;
            }else if(dy > 0){
                return 0;
            }else{ // dy == 0
                return -1; // src and dest are the same
            }
        }
    }

    public static void initialize() throws Exception {
        final long delta = System.currentTimeMillis();
        final RandomAccessFile raf = new RandomAccessFile("./data/tiledump.bin", "r");
        final FileChannel channel = raf.getChannel();
        final MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        final int length = buffer.getInt();
        for(int i = 0; i < length; i++){
            final int x = buffer.getShort();
            final int y = buffer.getShort();
            final byte flag = buffer.get();
            map.flag(x, y, flag);
        }
        System.out.println("Loaded tilemap in  " + (System.currentTimeMillis() - delta) + " ms.");
    }

    private static class TiledMap {

        private final byte[] plane;

        public TiledMap(final int width, final int height) {
            this.plane = new byte[width * 10000 + height];
        }

        public int getFlag(final int x, final int y) {
            return plane[x * 10000 + y];
        }

        public void flag(final int x, final int y, final byte flag) {
            this.plane[x * 10000 + y] = flag;
        }

    }

}
