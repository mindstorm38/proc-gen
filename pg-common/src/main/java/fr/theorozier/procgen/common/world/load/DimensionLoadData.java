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

public class DimensionLoadData {

    private final WorldServer world;
    private final File worldDir;
    private final File regionsDir;

    private final Map<SectionPositioned, Boolean> sectionsSaveStates = new HashMap<>();
    private final Map<SectionPositioned, DimensionRegionFile> regions = new HashMap<>();

    public DimensionLoadData(WorldServer world) {

        this.world = world;
        this.worldDir = world.getWorldDir();
        this.regionsDir = new File(worldDir, "regions");

        SaveUtils.mkdirOrThrowException(this.regionsDir, "The sections already exists but it's a file.");

    }

    public File getWorldDir() {
        return this.worldDir;
    }

    public File getRegionsDir() {
        return this.regionsDir;
    }

    public DimensionRegionFile getSectionRegion(SectionPositioned pos) {

        try (FixedObjectPool<SectionPosition>.PoolObject poolPos = SectionPosition.POOL.acquire()) {

            SectionPositioned regionPos = poolPos.get().set(pos.getX() >> 5, pos.getZ() >> 5);

            return this.regions.computeIfAbsent(regionPos, p -> {

                File file = new File(this.regionsDir, WorldLoadingManager.getRegionFileName(regionPos));

                try {

                    SaveUtils.mkdirOrThrowException(file, "Can't create region file '" + file + "' because a file with the same name already exits.");
                    RandomAccessFile rafile = new RandomAccessFile(file, "rw");

                    return new DimensionRegionFile(regionPos, rafile);

                } catch (IllegalStateException | IOException e) {

                    LOGGER.log(Level.WARNING, "Failed to create a region file '" + file + "'.", e);
                    return null;

                }
    
            });

        }

    }

    public boolean isSectionSaved(SectionPositioned pos) {
        return this.getSectionRegion(pos).isSectionSaved(pos.getX() & 31, pos.getZ() & 31);
    }

}
