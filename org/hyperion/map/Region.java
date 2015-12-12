package org.hyperion.map;

import org.hyperion.rs2.model.GameObjectDefinition;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;


public class Region {

    private static Region[] regions;
    private final int id;
    private final int[][][] clips = new int[4][][];
    private boolean members = false;

    public Region(final int id, final boolean members) {
        this.id = id;
        this.members = members;
    }

    private static void addClipping(final int x, final int y, final int height, final int shift) {
        final int regionX = x >> 3;
        final int regionY = y >> 3;
        final int regionId = ((regionX / 8) << 8) + (regionY / 8);
        for(final Region r : regions){
            if(r.id() == regionId){
                r.addClip(x, y, height, shift);
                break;
            }
        }
    }

    public static boolean isMembers(final int x, final int y, final int height) {
        if(x >= 3272 && x <= 3320 && y >= 2752 && y <= 2809)
            return false;
        if(x >= 2640 && x <= 2677 && y >= 2638 && y <= 2679)
            return false;
        final int regionX = x >> 3;
        final int regionY = y >> 3;
        final int regionId = ((regionX / 8) << 8) + (regionY / 8);
        for(final Region r : regions){
            if(r.id() == regionId){
                return r.members();
            }
        }
        return false;
    }

    private static void addClippingForVariableObject(final int x, final int y, final int height, final int type, final int direction, final boolean flag) {
        if(type == 0){
            if(direction == 0){
                addClipping(x, y, height, 128);
                addClipping(x - 1, y, height, 8);
            }else if(direction == 1){
                addClipping(x, y, height, 2);
                addClipping(x, y + 1, height, 32);
            }else if(direction == 2){
                addClipping(x, y, height, 8);
                addClipping(x + 1, y, height, 128);
            }else if(direction == 3){
                addClipping(x, y, height, 32);
                addClipping(x, y - 1, height, 2);
            }
        }else if(type == 1 || type == 3){
            if(direction == 0){
                addClipping(x, y, height, 1);
                addClipping(x - 1, y, height, 16);
            }else if(direction == 1){
                addClipping(x, y, height, 4);
                addClipping(x + 1, y + 1, height, 64);
            }else if(direction == 2){
                addClipping(x, y, height, 16);
                addClipping(x + 1, y - 1, height, 1);
            }else if(direction == 3){
                addClipping(x, y, height, 64);
                addClipping(x - 1, y - 1, height, 4);
            }
        }else if(type == 2){
            if(direction == 0){
                addClipping(x, y, height, 130);
                addClipping(x - 1, y, height, 8);
                addClipping(x, y + 1, height, 32);
            }else if(direction == 1){
                addClipping(x, y, height, 10);
                addClipping(x, y + 1, height, 32);
                addClipping(x + 1, y, height, 128);
            }else if(direction == 2){
                addClipping(x, y, height, 40);
                addClipping(x + 1, y, height, 128);
                addClipping(x, y - 1, height, 2);
            }else if(direction == 3){
                addClipping(x, y, height, 160);
                addClipping(x, y - 1, height, 2);
                addClipping(x - 1, y, height, 8);
            }
        }
        if(flag){
            if(type == 0){
                if(direction == 0){
                    addClipping(x, y, height, 65536);
                    addClipping(x - 1, y, height, 4096);
                }else if(direction == 1){
                    addClipping(x, y, height, 1024);
                    addClipping(x, y + 1, height, 16384);
                }else if(direction == 2){
                    addClipping(x, y, height, 4096);
                    addClipping(x + 1, y, height, 65536);
                }else if(direction == 3){
                    addClipping(x, y, height, 16384);
                    addClipping(x, y - 1, height, 1024);
                }
            }
            if(type == 1 || type == 3){
                if(direction == 0){
                    addClipping(x, y, height, 512);
                    addClipping(x - 1, y + 1, height, 8192);
                }else if(direction == 1){
                    addClipping(x, y, height, 2048);
                    addClipping(x + 1, y + 1, height, 32768);
                }else if(direction == 2){
                    addClipping(x, y, height, 8192);
                    addClipping(x + 1, y + 1, height, 512);
                }else if(direction == 3){
                    addClipping(x, y, height, 32768);
                    addClipping(x - 1, y - 1, height, 2048);
                }
            }else if(type == 2){
                if(direction == 0){
                    addClipping(x, y, height, 66560);
                    addClipping(x - 1, y, height, 4096);
                    addClipping(x, y + 1, height, 16384);
                }else if(direction == 1){
                    addClipping(x, y, height, 5120);
                    addClipping(x, y + 1, height, 16384);
                    addClipping(x + 1, y, height, 65536);
                }else if(direction == 2){
                    addClipping(x, y, height, 20480);
                    addClipping(x + 1, y, height, 65536);
                    addClipping(x, y - 1, height, 1024);
                }else if(direction == 3){
                    addClipping(x, y, height, 81920);
                    addClipping(x, y - 1, height, 1024);
                    addClipping(x - 1, y, height, 4096);
                }
            }
        }
    }

    private static void addClippingForSolidObject(final int x, final int y, final int height, final int xLength, final int yLength, final boolean flag) {
        int clipping = 256;
        if(flag){
            clipping += 0x20000;
        }
        for(int i = x; i < x + xLength; i++){
            for(int i2 = y; i2 < y + yLength; i2++){
                addClipping(i, i2, height, clipping);
            }
        }
    }

    private static void addObject(final int objectId, final int x, final int y, final int height, final int type, final int direction) {
        final GameObjectDefinition def = GameObjectDefinition.forId(objectId);
        /*if (def == null) {
            return;
        }
        int xLength;
        int yLength;
        if (direction != 1 && direction != 3) {
            xLength = def.getSizeX();
            yLength = def.getSizeY();
        } else {
            xLength = def.getSizeX();
            yLength = def.getSizeY();
        }
        if (type == 22) {
            if (def.hasActions() && def.aBoolean767()) {
                addClipping(x, y, height, 0x200000);
            }
        } else if (type >= 9) {
			if(def.aBoolean767())
			{
                addClippingForSolidObject(x, y, height, xLength, yLength, def.solid());
			}
        } else if (type >= 0 && type <= 3) {
			if(def.aBoolean767())
			{
                addClippingForVariableObject(x, y, height, type, direction, def.solid());
			}
        }*/
    }

    private static void loadMaps(final int regionId, final ByteStream str1, final ByteStream str2) {
        final int absX = (regionId >> 8) * 64;
        final int absY = (regionId & 0xff) * 64;
        final int[][][] someArray = new int[4][64][64];
        for(int i = 0; i < 4; i++){
            for(int i2 = 0; i2 < 64; i2++){
                for(int i3 = 0; i3 < 64; i3++){
                    while(true){
                        final int v = str2.getUByte();
                        if(v == 0){
                            break;
                        }else if(v == 1){
                            str2.skip(1);
                            break;
                        }else if(v <= 49){
                            str2.skip(1);
                        }else if(v <= 81){
                            someArray[i][i2][i3] = v - 49;
                        }
                    }
                }
            }
        }
        for(int i = 0; i < 4; i++){
            for(int i2 = 0; i2 < 64; i2++){
                for(int i3 = 0; i3 < 64; i3++){
                    if((someArray[i][i2][i3] & 1) == 1){
                        int height = i;
                        if((someArray[1][i2][i3] & 2) == 2){
                            height--;
                        }
                        if(height >= 0 && height <= 3){
                            addClipping(absX + i2, absY + i3, height, 0x200000);
                        }
                    }
                }
            }
        }
        int objectId = -1;
        int incr;
        while((incr = str1.getUSmart()) != 0){
            objectId += incr;
            int location = 0;
            int incr2;
            while((incr2 = str1.getUSmart()) != 0){
                location += incr2 - 1;
                final int localX = (location >> 6 & 0x3f);
                final int localY = (location & 0x3f);
                int height = location >> 12;
                final int objectData = str1.getUByte();
                final int type = objectData >> 2;
                final int direction = objectData & 0x3;
                if(localX < 0 || localX >= 64 || localY < 0 || localY >= 64){
                    continue;
                }
                if((someArray[1][localX][localY] & 2) == 2){
                    height--;
                }
                if(height >= 0 && height <= 3){
                    addObject(objectId, absX + localX, absY + localY, height, type, direction);
                }
            }
        }
    }

    public static int getClipping(final int x, final int y, int height) {
        if(height > 3)
            height = 0;
        final int regionX = x >> 3;
        final int regionY = y >> 3;
        final int regionId = ((regionX / 8) << 8) + (regionY / 8);
        for(final Region r : regions){
            if(r.id() == regionId){
                return r.getClip(x, y, height);
            }
        }
        return 0;
    }

    public static boolean getClipping(final int x, final int y, int height, final int moveTypeX, final int moveTypeY) {
        if(moveTypeX == 0 && moveTypeY == 0)
            return true;
        try{
            if(height > 3)
                height = 0;
            final int checkX = (x + moveTypeX);
            final int checkY = (y + moveTypeY);
            if(moveTypeX == -1 && moveTypeY == 0)
                return (getClipping(x, y, height) & 0x1280108) == 0;
            else if(moveTypeX == 1 && moveTypeY == 0)
                return (getClipping(x, y, height) & 0x1280180) == 0;
            else if(moveTypeX == 0 && moveTypeY == -1)
                return (getClipping(x, y, height) & 0x1280102) == 0;
            else if(moveTypeX == 0 && moveTypeY == 1)
                return (getClipping(x, y, height) & 0x1280120) == 0;
            else if(moveTypeX == -1 && moveTypeY == -1)
                return ((getClipping(x, y, height) & 0x128010e) == 0 && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0 && (getClipping(checkX - 1, checkY, height) & 0x1280102) == 0);
            else if(moveTypeX == 1 && moveTypeY == -1)
                return ((getClipping(x, y, height) & 0x1280183) == 0 && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0 && (getClipping(checkX, checkY - 1, height) & 0x1280102) == 0);
            else if(moveTypeX == -1 && moveTypeY == 1)
                return ((getClipping(x, y, height) & 0x1280138) == 0 && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0 && (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
            else if(moveTypeX == 1 && moveTypeY == 1)
                return ((getClipping(x, y, height) & 0x12801e0) == 0 && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0 && (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
            else{
                System.out.println("[FATAL ERROR]: At getClipping: " + x + ", " + y + ", " + height + ", " + moveTypeX + ", " + moveTypeY);
                return false;
            }
        }catch(final Exception e){
            return true;
        }
    }

    public static void load() {
        try{
            final File f = new File("./data/slash/map_index");
            final byte[] buffer = new byte[(int) f.length()];
            final DataInputStream dis = new DataInputStream(new FileInputStream(f));
            dis.readFully(buffer);
            dis.close();
            final ByteStream in = new ByteStream(buffer);
            final int size = in.length() / 7;
            regions = new Region[size];
            final int[] regionIds = new int[size];
            final int[] mapGroundFileIds = new int[size];
            final int[] mapObjectsFileIds = new int[size];
            final boolean[] isMembers = new boolean[size];
            for(int i = 0; i < size; i++){
                regionIds[i] = in.getUShort();
                mapGroundFileIds[i] = in.getUShort();
                mapObjectsFileIds[i] = in.getUShort();
                isMembers[i] = in.getUByte() == 0;
            }
            for(int i = 0; i < size; i++){
                regions[i] = new Region(regionIds[i], isMembers[i]);
            }
            for(int i = 0; i < size; i++){
                final byte[] file1 = getBuffer(new File("./data/slash/map/" + mapObjectsFileIds[i] + ".dat"));
                final byte[] file2 = getBuffer(new File("./data/slash/map/" + mapGroundFileIds[i] + ".dat"));
                if(file1 == null || file2 == null){
                    continue;
                }
                try{
                    loadMaps(regionIds[i], new ByteStream(file1), new ByteStream(file2));
                }catch(final Exception e){
                    System.out.println("Error loading map region: " + regionIds[i]);
                }
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] getBuffer(final File f) throws Exception {
        if(!f.exists())
            return null;
        byte[] buffer = new byte[(int) f.length()];
        final DataInputStream dis = new DataInputStream(new FileInputStream(f));
        dis.readFully(buffer);
        dis.close();
        final byte[] gzipInputBuffer = new byte[999999];
        int bufferlength = 0;
        final GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
        do {
            if(bufferlength == gzipInputBuffer.length){
                System.out.println("Error inflating data.\nGZIP buffer overflow.");
                break;
            }
            final int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
            if(readByte == -1)
                break;
            bufferlength += readByte;
        }while(true);
        final byte[] inflated = new byte[bufferlength];
        System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
        buffer = inflated;
        if(buffer.length < 10)
            return null;
        return buffer;
    }

    private void addClip(final int x, final int y, final int height, final int shift) {
        final int regionAbsX = (id >> 8) * 64;
        final int regionAbsY = (id & 0xff) * 64;
        if(clips[height] == null){
            clips[height] = new int[64][64];
        }
        clips[height][x - regionAbsX][y - regionAbsY] |= shift;
    }

    private int getClip(final int x, final int y, final int height) {
        final int regionAbsX = (id >> 8) * 64;
        final int regionAbsY = (id & 0xff) * 64;
        if(clips[height] == null){
            return 0;
        }
        return clips[height][x - regionAbsX][y - regionAbsY];
    }

    public int id() {
        return id;
    }

    public boolean members() {
        return members;
    }

}