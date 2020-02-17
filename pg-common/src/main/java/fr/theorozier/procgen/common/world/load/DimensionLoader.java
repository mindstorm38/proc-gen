package fr.theorozier.procgen.common.world.load;

import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.load.chunk.WorldLoadingTask;
import fr.theorozier.procgen.common.world.load.chunk.WorldPrimitiveSection;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.sutil.pool.FixedObjectPool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * A dimension loader, handle regions saving/loading.
 *
 * @author Théo Rozier
 *
 */
public class DimensionLoader {

    private final WorldDimension dimension;
    private final ChunkGenerator generator;

    private final File worldDir;
    private final File regionsDir;

    // File loading system.
    private final Map<SectionPositioned, DimensionRegionFile> regions = new HashMap<>();

    // Common loading system, using primitive sections.
    private final Map<SectionPositioned, WorldPrimitiveSection> primitiveSections = new HashMap<>();
    private final Map<SectionPositioned, Future<WorldLoadingTask>> loadingTasks = new HashMap<>();
    
    // This list also contains all primitive sections positions that will be only deleted when primitive section is finished.
    private final List<ImmutableSectionPosition> loadingTasksList = new ArrayList<>();

    public DimensionLoader(WorldDimension dimension) {

        if (dimension.getLoader() != null)
            throw new IllegalArgumentException("This dimension already had a loader.");

        this.dimension = dimension;
        this.generator = Objects.requireNonNull(this.dimension.getMetadata().getChunkGeneratorProvider().create(this.dimension), "ChunkGenerator provider returned Null.");

        this.worldDir = dimension.getDirectory();
        this.regionsDir = new File(this.worldDir, "regions");
        
        SaveUtils.mkdirOrThrowException(this.regionsDir, "The sections already exists but it's a file.");

    }

    public WorldDimension getDimension() {
        return this.dimension;
    }

    public ChunkGenerator getGenerator() {
        return this.generator;
    }

    public File getWorldDir() {
        return this.worldDir;
    }

    public File getRegionsDir() {
        return this.regionsDir;
    }
    
    public void loadSection(SectionPositioned rawPos) {
        
        if (this.isSectionLoading(rawPos))
            return;
        
        ImmutableSectionPosition pos = rawPos.immutableSectionPos();
        WorldPrimitiveSection primitiveSection = new WorldPrimitiveSection(this.dimension, pos);
        
        this.primitiveSections.put(pos, primitiveSection);
        this.loadingTasksList.add(pos);
        
        if (this.isSectionSaved(pos)) {
        
        } else {
        
        }
        
    }

    public void update() {
    
    }

    private void updateChunkLoading() {

    

    }
    
    /**
     * To know if a section is loading.
     * @param pos The section position.
     * @return True if the section is currently loading.
     */
    public boolean isSectionLoading(SectionPositioned pos) {
        return this.primitiveSections.containsKey(pos);
    }
    
    /**
     * Get primitive section at specified position.
     * @param pos Section position.
     * @return The primitive section, or Null if no primitive section there.
     */
    public WorldPrimitiveSection getPrimitiveSection(SectionPositioned pos) {
        return this.primitiveSections.get(pos);
    }

    /**
     * Get a region file from region position (region are groups of 32x32 sections).
     * @param pos Region position.
     * @return The opened region file, should never return closed region file.
     */
    public DimensionRegionFile getRegionFile(SectionPositioned pos) {
    
        return this.regions.computeIfAbsent(pos, p -> {
        
            File file = new File(this.regionsDir, WorldLoadingManager.getRegionFileName(pos));
        
            try {
            
                SaveUtils.mkdirOrThrowException(file, "Can't create region file '" + file + "' because a file with the same name already exits.");
                RandomAccessFile rafile = new RandomAccessFile(file, "rw");
            
                return new DimensionRegionFile(rafile);
            
            } catch (IllegalStateException | IOException e) {
            
                LOGGER.log(Level.WARNING, "Failed to create a region file '" + file + "'.", e);
                return null;
            
            }
        
        });
        
    }
    
    /**
     * Get a region file from section position (region are groups of 32x32 sections).
     * @param pos Section position.
     * @return The opened region file, should never return closed region file.
     */
    public DimensionRegionFile getSectionRegionFile(SectionPositioned pos) {

        try (FixedObjectPool<SectionPosition>.PoolObject poolPos = SectionPosition.POOL.acquire()) {
            return this.getRegionFile(poolPos.get().set(pos.getX() >> 5, pos.getZ() >> 5));
        }

    }
    
    /**
     * To know if a section is saved, can be used to know if the section have to be generated.
     * @param pos Section position.
     * @return True if the section is saved in the region file.
     */
    public boolean isSectionSaved(SectionPositioned pos) {
        // TODO : Create other method than getSectionRegionFile to not create region file instance if not created.
        return this.getSectionRegionFile(pos).isSectionSaved(pos.getX() & 31, pos.getZ() & 31);
    }

    /**
     * No used for now.
     */
    public void cleanup() {
    
    }
    
}
