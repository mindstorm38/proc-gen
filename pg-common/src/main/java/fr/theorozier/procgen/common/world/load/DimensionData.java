package fr.theorozier.procgen.common.world.load;

import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.SectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.sutil.pool.FixedObjectPool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * A dimension data handler.
 *
 */
public class DimensionData {

    private final WorldServer world;
    private final File worldDir;
    private final File regionsDir;

    private final Map<SectionPositioned, DimensionRegionFile> regions = new HashMap<>();

    public DimensionData(WorldServer world) {

        this.world = world;
        this.worldDir = world.getWorldDir();
        this.regionsDir = new File(this.worldDir, "regions");
        
        SaveUtils.mkdirOrThrowException(this.regionsDir, "The sections already exists but it's a file.");

    }

    public File getWorldDir() {
        return this.worldDir;
    }

    public File getRegionsDir() {
        return this.regionsDir;
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
        return this.getSectionRegionFile(pos).isSectionSaved(pos.getX() & 31, pos.getZ() & 31);
    }

    public void cleanup() {
    
    }
    
}
