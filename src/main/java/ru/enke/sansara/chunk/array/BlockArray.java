package ru.enke.sansara.chunk.array;

public class BlockArray {

    private static final int BLOCK_ARRAY_LENGTH = 4096;

    private final short[] data;

    public BlockArray() {
        this(new short[BLOCK_ARRAY_LENGTH]);
    }

    public BlockArray(short[] data) {
        this.data = data;
    }

    public short[] getData() {
        return data;
    }

    private int get(final int x, final int y, final int z) {
        return data[y << 8 | z << 4 | x] & 65535;
    }

    private void set(final int x, final int y, final int z, final int id) {
        data[y << 8 | z << 4 | x] = (short) id;
    }

    public void setId(final int x, final int y, final int z, final int block) {
        set(x, y, z, block << 4 | getMetadata(x, y, z));
    }

    public int getId(final int x, final int y, final int z) {
        return this.get(x, y, z) >> 4;
    }

    public void setMetadata(final int x, final int y, final int z, final int data) {
        set(x, y, z, getId(x, y, z) << 4 | data);
    }

    public int getMetadata(final int x, final int y, final int z) {
        return get(x, y, z) & 15;
    }

}
