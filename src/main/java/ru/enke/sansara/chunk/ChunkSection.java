package ru.enke.sansara.chunk;

import ru.enke.sansara.chunk.array.BlockArray;
import ru.enke.sansara.chunk.array.NibbleArray;

public class ChunkSection {

    private final BlockArray blocks;
    private final NibbleArray blocksLight;
    private final NibbleArray skyLight;
    private final int y;

    ChunkSection(final int y) {
        this(y, new BlockArray(), new NibbleArray(), new NibbleArray());
    }

    ChunkSection(final int y, final BlockArray blocks, final NibbleArray blocksLight, final NibbleArray skyLight) {
        this.y = y;
        this.blocks = blocks;
        this.blocksLight = blocksLight;
        this.skyLight = skyLight;
    }

    public short[] getBlocks() {
        return blocks.getData();
    }

    public byte[] getBlocksLight() {
        return blocksLight.getData();
    }

    public byte[] getSkyLight() {
        return skyLight.getData();
    }

    public int getY() {
        return y;
    }

    public void setBlockId(final int x, final int y, final int z, final int id) {
        blocks.setId(x, y, z, id);
    }

    public int getBlockId(final int x, final int y, final int z) {
        return blocks.getId(x, y, z);
    }

    public void setBlockMetadata(final int x, final int y, final int z, final int data) {
        blocks.setMetadata(x, y, z, data);
    }

    public int getBlockMetadata(final int x, final int y, final int z) {
        return blocks.getMetadata(x, y, z);
    }

    public void setBlockLight(final int x, final int y, final int z, final int light) {
        blocksLight.set(x, y, z, (byte) light);
    }

    public int getBlockLight(final int x, final int y, final int z) {
        return blocksLight.get(x, y, z);
    }

    public void setSkyLight(final int x, final int y, final int z, final int light) {
        skyLight.set(x, y, z, (byte) light);
    }

    public int getSkyLight(final int x, final int y, final int z) {
        return skyLight.get(x, y, z);
    }

    public int getBlockY(final int y) {
        return this.y * 16 + y;
    }

}
