package ru.enke.sansara.chunk;

public class ChunkPosition {

    private final int x;
    private final int z;

    public ChunkPosition(final int x, final int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getBlockX(final int x) {
        return this.x * 16 + x;
    }

    public int getBlockZ(final int z) {
        return this.z * 16 + z;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        final ChunkPosition that = (ChunkPosition) o;
        if(x != that.x) return false;

        return z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }

}
