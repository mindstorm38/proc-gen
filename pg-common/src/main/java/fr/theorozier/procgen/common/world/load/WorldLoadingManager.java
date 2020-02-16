package fr.theorozier.procgen.common.world.load;

import fr.theorozier.procgen.common.util.ThreadingDispatch;
import fr.theorozier.procgen.common.util.concurrent.PriorityThreadPoolExecutor;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * Basic handler for world dimensions saving and loading from files or from scratch.
 *
 * @author Theo Rozier
 *
 */
public class WorldLoadingManager {
	
	private static final ThreadingDispatch WORLD_CHUNK_LOADING_DISPATCH = ThreadingDispatch.register("WORLD_CHUNK_LOADING", 3);
	private static final long MAX_IDLE_TIME = 60000;

	private final Map<String, DimensionData> dimensionsLoadData = new HashMap<>();
	private WorldServer dimensionManager = null;
	private PriorityThreadPoolExecutor loadingThreadPool = null;
	private long poolIdleStartTime = 0;

	public WorldLoadingManager() { }

	/**
	 * Set active dimension manager to run, it will stop current tasks if an dimension manager is active in.<br>
	 * Should be called from main update loop.
	 * @param manager The dimension manager, or Null to only stop current dimension manager.
	 */
	public void setCurrentDimensionManager(WorldServer manager) {

		if (this.dimensionManager != null) {

			this.dimensionsLoadData.clear();

		}

		this.dimensionManager = manager;

		if (manager != null) {

			if (this.loadingThreadPool == null) {

				int poolSize = WORLD_CHUNK_LOADING_DISPATCH.getEffectiveCount();

				LOGGER.info("Started world loading thread pool (" + poolSize + " threads) ...");
				this.loadingThreadPool = new PriorityThreadPoolExecutor(poolSize, PriorityThreadPoolExecutor.ASC_COMPARATOR);

			}


		} else if (this.loadingThreadPool != null) {
			this.poolIdleStartTime = System.currentTimeMillis();
		}

	}

	/**
	 * Update world loading manager.
	 */
	public void update() {

		if (this.dimensionManager == null && this.loadingThreadPool != null && (System.currentTimeMillis() - this.poolIdleStartTime) > MAX_IDLE_TIME) {

			LOGGER.info("Shutting down world loading thread pool... (" + this.loadingThreadPool.getCorePoolSize() + " threads)");
			this.loadingThreadPool.shutdown(); // FIXME: Potential leaks if tasks never finished
			this.loadingThreadPool = null;

		}

	}

	private DimensionData getDimensionData(WorldDimension dim) {

		if (this.dimensionManager == null)
			throw new IllegalStateException("Can't get dimension data if no dimension manager is running");

		DimensionData data = this.dimensionsLoadData.get(dim.getIdentifier());

		if (data == null) {

			try {

				data = new DimensionData(dim);
				this.dimensionsLoadData.put(dim.getIdentifier(), data);

			} catch (IllegalStateException e) {

				LOGGER.log(Level.WARNING, "Failed to created dimension data !", e);
				return null;

			}

		}

		return data;

	}

	public boolean isSectionSaved(WorldDimension dim, SectionPositioned pos) {
		DimensionData data = this.getDimensionData(dim);
		return data != null && data.isSectionSaved(pos);
	}
	
	public boolean isSectionSaving(WorldDimension dim, SectionPositioned pos) {
		return false;
	}
	
	public void loadSectionTo(WorldDimension dim, WorldServerSection section) {
	
	}
	
	public boolean isSectionLoading(WorldDimension dim, SectionPositioned pos) {
		return false;
	}

	public static String getRegionFileName(SectionPositioned pos) {
		return pos.getX() + "." + pos.getZ() + ".pgr";
	}

}
