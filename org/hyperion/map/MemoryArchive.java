package org.hyperion.map;


public class MemoryArchive {

    private static final int INDEX_DATA_CHUNK_SIZE = 12;
    private final ByteStream cache;
    private final ByteStream index;

    public MemoryArchive(final ByteStream cache, final ByteStream index) {
        this.cache = cache;
        this.index = index;
    }

    public byte[] get(final int dataIndex) {
        try{
            if(index.length() < (dataIndex * INDEX_DATA_CHUNK_SIZE))
                return null;
            index.setOffset(dataIndex * INDEX_DATA_CHUNK_SIZE);
            final long fileOffset = index.getLong();
            final int fileSize = index.getInt();
            cache.setOffset(fileOffset);
            final byte[] buffer = cache.read(fileSize);
            return buffer;
        }catch(final Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public int contentSize() {
        return index.length() / 12;
    }

}





