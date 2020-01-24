package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.util.concurrent.PriorityThreadPoolExecutor;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGenerator;
import fr.theorozier.procgen.common.world.gen.chunk.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.gen.WorldIncompatException;
import fr.theorozier.procgen.common.world.gen.chunk.WorldPrimitiveSection;

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
public class WorldDimensionManager {
	
	private final File worldDirectory;
	private final File playersDirectory;
	private final File dimensionsDirectory;
	
	private final Map<String, File> dimensionsFiles = new HashMap<>();
	private final Map<String, WorldServer> dimensionsWorlds = new HashMap<>();
	private WorldServer[] dimensions = {};
	
	private final PriorityThreadPoolExecutor generatorComputer;
	
	public WorldDimensionManager(File worldDirectory) {
		
		SaveUtils.mkdirOrThrowException(worldDirectory, "The world directory already exists but it's a file.");
		
		this.worldDirectory = worldDirectory;
		this.playersDirectory = new File(worldDirectory, "players");
		this.dimensionsDirectory = new File(worldDirectory, "dims");
		
		SaveUtils.mkdirOrThrowException(this.playersDirectory, "The players directory already exists but it's a file.");
		SaveUtils.mkdirOrThrowException(this.dimensionsDirectory, "The dimensions directory already exists but it's a file.");
		
		this.generatorComputer = new PriorityThreadPoolExecutor(4, PriorityThreadPoolExecutor.ASC_COMPARATOR);
		
	}
	
	public File getWorldDirectory() {
		return this.worldDirectory;
	}
	
	/**
	 * Load the world, this happen when creating a world, or on open.
	 * @throws WorldIncompatException Potential incompatibility error, if the world can't
	 */
	public void load() throws WorldIncompatException {
	
		// TODO : Here, load all dimensions from the world directory
	
	}
	
	public WorldServer createNewDimension(String identifier, long seed, ChunkGeneratorProvider provider) {
		
		if (this.dimensionsWorlds.containsKey(identifier))
			throw new IllegalArgumentException("The dimension '" + identifier + "' already exists this manager !");
		
		WorldServer newDim = new WorldServer(this, seed, provider);
		
		this.dimensionsWorlds.put(identifier, newDim);
		
		WorldServer[] newDimensions = new WorldServer[this.dimensions.length + 1];
		System.arraycopy(this.dimensions, 0, newDimensions, 0, this.dimensions.length);
		newDimensions[this.dimensions.length] = newDim;
		
		this.dimensions = newDimensions;
		
		return newDim;
		
	}
	
	public boolean hasDimension(String identifier) {
		return this.dimensionsWorlds.containsKey(identifier);
	}
	
	public ChunkGeneratorProvider getDimensionChunkGeneratorProvider(String identifier) {
		WorldServer world = this.dimensionsWorlds.get(identifier);
		return world == null ? null : world.getChunkGeneratorProvider();
	}
	
	/**
	 * Check if a dimension in this world is using a specific chunk generator.
	 * @param identifier The dimension identifier.
	 * @param generatorClass The generator class.
	 * @return True if the world's chunk generator is of class <code>generatorClass</code>, also return true if there is no dimensions with this identifier.
	 */
	public boolean isDimensionUsingChunkGenerator(String identifier, Class<? extends ChunkGenerator> generatorClass) {
		
		WorldServer world = this.dimensionsWorlds.get(identifier);
		
		if (world != null) {
			return world.getChunkGenerator().getClass().equals(generatorClass);
		} else {
			return true;
		}
		
	}
	
	public void update() {
		
		for (WorldServer dim : this.dimensions) {
			dim.update();
		}
		
	}
	
	Future<WorldPrimitiveSection> submitWorldLoadingTask(WorldPrimitiveSection section, PriorityRunnable run) {
		return this.generatorComputer.submit(run, section);
	}

}
