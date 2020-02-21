package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.world.chunk.Heightmap;
import fr.theorozier.procgen.common.world.chunk.WorldServerChunk;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.AbsSectionPosition;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

import java.util.Random;

public interface WorldAccessorServer extends WorldAccessor {

    // PROPERTIES //

    /**
     * @return The world generation seed used by chunk generator.
     */
    long getSeed();

    /**
     * @return The world global random, used in entities for example.
     */
    Random getRandom();

    /**
     * @return This world sea level.
     */
    int getSeaLevel();
    
    // SECTIONS //
    
    @Override
    WorldServerSection getSectionAt(int x, int z);
    
    @Override
    default WorldServerSection getSectionAt(AbsSectionPosition pos) {
        return this.getSectionAt(pos.getX(), pos.getZ());
    }
    
    @Override
    default WorldServerSection getSectionAtBlock(int x, int z) {
        return this.getSectionAt(x >> 4, z >> 4);
    }
    
    @Override
    default WorldServerSection getSectionAtBlock(AbsBlockPosition pos) {
        return this.getSectionAtBlock(pos.getX(), pos.getZ());
    }
    
    // CHUNKS //
    
    @Override
    WorldServerChunk getChunkAt(int x, int y, int z);
    
    @Override
    default WorldServerChunk getChunkAt(AbsBlockPosition pos) {
        return this.getChunkAt(pos.getX(), pos.getY(), pos.getZ());
    }
    
    @Override
    default WorldServerChunk getChunkAtBlock(int x, int y, int z) {
        return this.getChunkAt(x >> 4, y >> 4, z >> 4);
    }
    
    @Override
    default WorldServerChunk getChunkAtBlock(AbsBlockPosition pos) {
        return this.getChunkAtBlock(pos.getX(), pos.getY(), pos.getZ());
    }
    
    // HEIGHTMAPS //

    short getHeightAt(Heightmap.Type type, int x, int z);

    default short getHeightAt(Heightmap.Type type, AbsSectionPosition pos) {
        return this.getHeightAt(type, pos.getX(), pos.getZ());
    }

    default ImmutableBlockPosition getBlockHeightAt(Heightmap.Type type, AbsSectionPosition pos) {
        return new ImmutableBlockPosition(pos, this.getHeightAt(type, pos));
    }

}
