package fr.theorozier.procgen.common.world.load;

import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.io.RandomAccessFile;

public class DimensionRegionStorage {

    private final ImmutableSectionPosition position;
    private final RandomAccessFile randomAccessFile;

    private final long[] savedSections = new long[4]; // 4*64 bits = 256 bits (booleans).

    public DimensionRegionStorage(SectionPositioned pos, RandomAccessFile file) {

        this.position = pos.immutableSectionPos();
        this.randomAccessFile = file;

    }

    public boolean isSectionSaved(int x, int y) {

        int idx = WorldSection.getSectionIndex(x, y);
        return (this.savedSections[idx >> 6] >> (idx & 63)) == 1;

    }

}
