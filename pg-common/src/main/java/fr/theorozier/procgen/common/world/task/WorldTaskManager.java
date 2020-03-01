package fr.theorozier.procgen.common.world.task;

import fr.theorozier.procgen.common.util.ThreadingDispatch;
import fr.theorozier.procgen.common.util.concurrent.PriorityThreadPoolExecutor;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.util.concurrent.Future;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * Basic handler for world dimensions saving and loading from files or from scratch.
 *
 * @author Theo Rozier
 *
 */
public class WorldTaskManager {
	
	private static final ThreadingDispatch WORLD_TASKS_DISPATCH = ThreadingDispatch.register("WORLD_TASKS", 3);
	private static final long MAX_IDLE_TIME = 60000;

	private WorldServer dimensionManager = null;
	private PriorityThreadPoolExecutor threadPool = null;
	private long poolIdleStartTime = 0;

	public WorldTaskManager() { }

	/**
	 * Set active dimension manager to run, it will stop current tasks if an dimension manager is active in.<br>
	 * Should be called from main update loop.
	 * @param manager The dimension manager, or Null to only stop current dimension manager.
	 */
	public void setCurrentDimensionManager(WorldServer manager) {

		this.dimensionManager = manager;

		if (manager != null) {

			if (this.threadPool == null) {

				int poolSize = WORLD_TASKS_DISPATCH.getEffectiveCount(2);

				LOGGER.info("Starting world tasks thread pool (" + poolSize + " threads) ...");
				this.threadPool = new PriorityThreadPoolExecutor(poolSize, PriorityThreadPoolExecutor.ASC_COMPARATOR);

			}

		} else if (this.threadPool != null) {
			this.poolIdleStartTime = System.currentTimeMillis();
		}

	}

	/**
	 * Update world loading manager.
	 */
	public void update() {

		if (this.dimensionManager == null && this.threadPool != null && (System.currentTimeMillis() - this.poolIdleStartTime) > MAX_IDLE_TIME) {

			LOGGER.info("Shutting down world loading thread pool... (" + this.threadPool.getCorePoolSize() + " threads)");
			this.threadPool.shutdown(); // FIXME: Potential leaks if tasks never finished
			this.threadPool = null;

		}

	}
	
	public Future<WorldTask> submitWorldTask(WorldTask task) {
		return this.threadPool.submit(task, task);
	}
	
	public static String getRegionFileName(SectionPositioned pos) {
		return pos.getX() + "." + pos.getZ() + ".pgr";
	}

}
