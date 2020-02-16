package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.util.concurrent.PriorityThreadPoolExecutor;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.gen.chunk.WorldPrimitiveSection;
import fr.theorozier.procgen.common.world.load.*;
import io.msengine.common.util.GameProfiler;
import io.sutil.profiler.Profiler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 *
 * A save and creation manager for real world, composed of multiple dimensions.
 *
 * @author Theo Rozier
 *
 */
public class WorldServer {
	
	private static final Profiler PROFILER = GameProfiler.getInstance();
	
	private final File worldDirectory;
	private final File playersDirectory;
	private final File dimensionsDirectory;
	
	private final Map<String, File> dimensionsFiles = new HashMap<>();
	private final Map<String, WorldDimension> dimensionsWorlds = new HashMap<>();
	private WorldDimension[] dimensions = {};
	
	private WorldLoadingManager loadingManager = null;
	
	@Deprecated
	private final PriorityThreadPoolExecutor generatorComputer;
	
	public WorldServer(File worldDirectory) {
		
		SaveUtils.mkdirOrThrowException(worldDirectory, "The world directory already exists but it's a file.");
		
		this.worldDirectory = worldDirectory;
		this.playersDirectory = new File(worldDirectory, "players");
		this.dimensionsDirectory = new File(worldDirectory, "dims");
		
		SaveUtils.mkdirOrThrowException(this.playersDirectory, "The players directory already exists but it's a file.");
		SaveUtils.mkdirOrThrowException(this.dimensionsDirectory, "The dimensions directory already exists but it's a file.");
		
		this.generatorComputer = new PriorityThreadPoolExecutor(4, PriorityThreadPoolExecutor.ASC_COMPARATOR);
		
	}
	
	/**
	 * Get the world directory, where
	 * @return
	 */
	public File getWorldDirectory() {
		return this.worldDirectory;
	}
	
	/**
	 * Load the world dimensions from file system. This is called before starting a world.
	 */
	public void load(WorldLoadingManager loadingManager) {
	
		this.loadingManager = loadingManager;
		
		// TODO : Here, load all dimensions from the world directory
	
	}
	
	public void unload() {
		
		this.loadingManager = null;
		
	}
	
	/**
	 * Create a new dimension if not already existing.
	 * @param identifier The world dimension identifier.
	 * @param seed The dimension seed for generation.
	 * @param provider The chunk provider to use for this world.
	 * @return The new dimension's world instance.
	 */
	public WorldDimension createNewDimension(String identifier, long seed, ChunkGeneratorProvider provider) {
		
		if (this.dimensionsWorlds.containsKey(identifier))
			throw new IllegalArgumentException("The dimension '" + identifier + "' already exists this manager !");
		
		File worldDir = new File(this.dimensionsDirectory, identifier);
		SaveUtils.mkdirOrThrowException(worldDir, "The world directory can't be created because a file with same name already exists.");
		
		WorldDimension newDim = new WorldDimension(this, identifier, worldDir, seed, provider);
		
		this.dimensionsWorlds.put(identifier, newDim);
		
		WorldDimension[] newDimensions = new WorldDimension[this.dimensions.length + 1];
		System.arraycopy(this.dimensions, 0, newDimensions, 0, this.dimensions.length);
		newDimensions[this.dimensions.length] = newDim;
		
		this.dimensions = newDimensions;
		
		return newDim;
		
	}
	
	public boolean hasDimension(String identifier) {
		return this.dimensionsWorlds.containsKey(identifier);
	}
	
	public ChunkGeneratorProvider getDimensionChunkGeneratorProvider(String identifier) {
		WorldDimension world = this.dimensionsWorlds.get(identifier);
		return world == null ? null : world.getChunkGeneratorProvider();
	}
	
	/**
	 * Check if a dimension in this world is using a specific chunk generator.
	 * @param identifier The dimension identifier.
	 * @param generatorClass The generator class.
	 * @return True if the world's chunk generator is of class <code>generatorClass</code>, also return true if there is no dimensions with this identifier.
	 */
	public boolean isDimensionUsingChunkGenerator(String identifier, Class<? extends ChunkGenerator> generatorClass) {

		WorldDimension world = this.dimensionsWorlds.get(identifier);
		
		if (world != null) {
			return world.getChunkGenerator().getClass().equals(generatorClass);
		} else {
			return true;
		}

	}
	
	public WorldDimension getDimension(String identifier) {
		return this.dimensionsWorlds.get(identifier);
	}

	public WorldDimension getMainDimension() {
		return this.dimensions.length == 0 ? null : this.dimensions[0];
	}

	public void update() {
		
		for (WorldDimension dim : this.dimensions) {
			dim.update();
		}
		
	}
	
	Future<WorldPrimitiveSection> submitWorldLoadingTask(WorldPrimitiveSection section, PriorityRunnable run) {
		return this.generatorComputer.submit(run, section);
	}
	
	void submitOtherTask(PriorityRunnable run) {
		this.generatorComputer.submit(run);
	}

}
