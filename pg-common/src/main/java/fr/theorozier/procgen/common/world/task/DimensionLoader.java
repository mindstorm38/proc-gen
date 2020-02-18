package fr.theorozier.procgen.common.world.task;

import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.task.section.WorldPrimitiveSection;
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
    private final Map<SectionPositioned, Future<WorldTask>> tasks = new HashMap<>();
    
    // This list also contains all primitive sections positions that will be only deleted when primitive section is sent to dimension.
    private final List<ImmutableSectionPosition> tasksList = new ArrayList<>();

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
    
    public void loadSection(SectionPositioned pos) {
        
        if (this.isSectionLoading(pos))
            return;
        
        ImmutableSectionPosition immutablePos = pos.immutableSectionPos();
        WorldPrimitiveSection primitiveSection = new WorldPrimitiveSection(this.dimension, immutablePos);
        
        this.primitiveSections.put(immutablePos, primitiveSection);
        this.tasksList.add(immutablePos);
        
        if (this.isSectionSaved(immutablePos)) {
        
        } else {

        }
        
    }
    
    public void saveSection(SectionPositioned pos) {
    
        if (this.isSectionLoading(pos))
            return;
    
        WorldServerSection section = this.dimension.getSectionAt(pos);
        
        if (section != null) {
            
            ImmutableSectionPosition immutablePos = section.getSectionPos();
            
            WorldTask task = section.getSavingTask(this);
            Future<WorldTask> future = this.dimension.getTaskManager().submitWorldTask(task);
            
            this.tasks.put(immutablePos, future);
            this.tasksList.add(immutablePos);
            
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
        return this.primitiveSections.containsKey(pos) || this.tasks.containsKey(pos);
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
     * @param pos Region position ("sectionPosComponents" >> 5).
     * @return The opened region file, should never return Null nor closed region file.
     */
    public DimensionRegionFile getRegionFileCreate(SectionPositioned pos) {

        return this.regions.computeIfAbsent(pos, p -> {

            File file = new File(this.regionsDir, WorldTaskManager.getRegionFileName(pos));

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
     * Get a region file from region position (region are groups of 32x32 sections).
     * @param pos Region position ("sectionPosComponents" >> 5).
     * @return The opened region file (should never return closed region file), or Null if this region file is not opened.
     */
    public DimensionRegionFile getRegionFile(SectionPositioned pos) {
        return this.regions.get(pos);
    }

    /**
     * Get a region file from section position (region are groups of 32x32 sections).
     * @param pos Section position.
     * @param create True to create (or load) and initialize the region file if not cached.
     * @return The opened region file, should never return closed region file but can return Null if 'create' parameter is set to False.
     * @see #getRegionFileCreate(SectionPositioned)
     * @see #getRegionFile(SectionPositioned)
     */
    public DimensionRegionFile getSectionRegionFile(SectionPositioned pos, boolean create) {

        try (FixedObjectPool<SectionPosition>.PoolObject poolPos = SectionPosition.POOL.acquire()) {

            SectionPosition regpos = poolPos.get().set(pos.getX() >> 5, pos.getZ() >> 5);
            return create ? this.getRegionFileCreate(regpos) : this.getRegionFile(regpos);

        }

    }

    /**
     * To know if a section is saved, can be used to know if the section have to be generated.
     * @param pos Section position.
     * @return True if the section is saved in the region file.
     */
    public boolean isSectionSaved(SectionPositioned pos) {
        DimensionRegionFile file = this.getSectionRegionFile(pos, false);
        return file != null && file.isSectionSaved(pos.getX() & 31, pos.getZ() & 31);
    }

    /**
     * No used for now.
     */
    public void cleanup() {
    
    }
    
}
