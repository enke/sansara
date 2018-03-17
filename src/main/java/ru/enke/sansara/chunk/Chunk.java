package ru.enke.sansara.chunk;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

import static ru.enke.sansara.chunk.ChunkSection.CHUNK_SECTION_LENGTH;

public class Chunk {

    public static final int BIOMES_DATA_LENGTH = 256;

    private final ChunkSection[] sections = new ChunkSection[16];
    private final int[] height = new int[256];
    private final ChunkPosition position;

    public Chunk(final ChunkPosition position) {
        this.position = position;
    }

    public ChunkPosition getPosition() {
        return position;
    }

    public ChunkSection[] getSections() {
        return sections;
    }

    public int[] getHeight() {
        return height;
    }

    public void setBlockId(final int x, final int y, final int z, final int id) {
        final ChunkSection section = getChunkSectionOrCreate(y);

        if(y > height[x * z]) {
            height[z << 4 | x] = y;
        }

        section.setBlockId(x, y & 15, z, id);
    }

    public int getBlockId(final int x, final int y, final int z) {
        final ChunkSection section = getChunkSection(y);

        if(section == null) {
            return 0;
        }

        return section.getBlockId(x, y & 15, z);
    }

    public void setBlockMetadata(final int x, final int y, final int z, final int data) {
        getChunkSectionOrCreate(y).setBlockMetadata(x, y & 15, z, data);
    }

    public int getBlockMetadata(final int x, final int y, final int z) {
        final ChunkSection section = getChunkSection(y);

        if(section == null) {
            return 0;
        }

        return section.getBlockMetadata(x, y & 15, z);
    }

    public void setBlockLight(final int x, final int y, final int z, final int light) {
        getChunkSectionOrCreate(y).setBlockLight(x, y & 15, z, light);
    }

    public int getBlockLight(final int x, final int y, final int z) {
        final ChunkSection section = getChunkSection(y);

        if(section == null) {
            return 0;
        }

        return section.getBlockLight(x, y & 15, z);
    }

    public int getSkyLight(final int x, final int y, final int z) {
        final ChunkSection section = getChunkSection(y);

        if(section == null) {
            return 0;
        }

        return section.getSkyLight(x, y & 15, z);
    }

    public void setSkyLight(final int x, final int y, final int z, final int light) {
        getChunkSectionOrCreate(y).setSkyLight(x, y & 15, z, light);
    }

    public boolean canBlockSeeTheSky(final int x, final int y, final int z) {
        return y >= height[z << 4 | x];
    }

    @Nullable
    private ChunkSection getChunkSection(final int y) {
        return sections[y >> 4];
    }

    private ChunkSection getChunkSectionOrCreate(final int y) {
        final ChunkSection section = getChunkSection(y);

        if(section == null) {
            final ChunkSection newSection = new ChunkSection(y >> 4);
            sections[y >> 4] = newSection;

            return newSection;
        }

        return section;
    }

    public byte[] getData() {
        final int totalSectors = (int) Arrays.stream(sections).filter(Objects::nonNull).count();
        final byte[] data = new byte[totalSectors * CHUNK_SECTION_LENGTH + 256];
        int currentIndex = 0;

        for(final ChunkSection section : sections) {
            if(section == null) {
                continue;
            }

            for(final short block : section.getBlocks()) {
                data[currentIndex++] = (byte) (block & 255);
                data[currentIndex++] = (byte) (block >> 8);
            }

            final byte[] blocksLight = section.getBlocksLight();
            System.arraycopy(blocksLight, 0, data, currentIndex, blocksLight.length);
            currentIndex += blocksLight.length;

            final byte[] skyLight = section.getSkyLight();
            System.arraycopy(skyLight, 0, data, currentIndex, skyLight.length);
            currentIndex += skyLight.length;
        }

        // TODO: Add biomes.
        for(int i = 0; i < 256; ++i) {
            data[currentIndex++] = 0;
        }

        return data;
    }

    public short getMask() {
        short mask = 0;

        for(final ChunkSection section : sections) {
            if(section != null) {
                mask |= 1 << section.getY();
            }
        }

        return mask;
    }

}
