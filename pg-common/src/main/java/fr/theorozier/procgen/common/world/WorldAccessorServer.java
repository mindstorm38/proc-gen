package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.world.chunk.Heightmap;
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

    // HEIGHTMAPS //

    short getHeightAt(Heightmap.Type type, int x, int z);

    default short getHeightAt(Heightmap.Type type, AbsSectionPosition pos) {
        return this.getHeightAt(type, pos.getX(), pos.getZ());
    }

    default ImmutableBlockPosition getBlockHeightAt(Heightmap.Type type, AbsSectionPosition pos) {
        return new ImmutableBlockPosition(pos, this.getHeightAt(type, pos));
    }

}
