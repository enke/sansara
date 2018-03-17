package ru.enke.sansara.chunk.array;

public class NibbleArray {

    public static final int NIBLE_ARRAY_LENGTH = 2048;

    private byte[] data;

    public NibbleArray() {
        this(new byte[NIBLE_ARRAY_LENGTH]);
    }

    public NibbleArray(final byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void set(final int x, final int y, final int z, final byte value) {
        set(y << 8 | z << 4 | x, value);
    }

    public void set(final int index, byte value) {
        value &= 0xf;
        int half = index / 2;
        byte previous = data[half];

        if (index % 2 == 0) {
            data[half] = (byte) ((previous & 240) | value);
        } else {
            data[half] = (byte) ((previous & 15) | (value << 4));
        }
    }

    public byte get(final int x, final int y, final int z) {
        return get(y << 8 | z << 4 | x);
    }

    public byte get(final int index) {
        byte value = data[index / 2];

        if (index % 2 == 0) {
            return (byte) (value & 15);
        } else {
            return (byte) ((value & 240) >> 4);
        }
    }

}
