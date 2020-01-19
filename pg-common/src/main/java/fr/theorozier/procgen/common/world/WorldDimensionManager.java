package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.world.gen.ChunkGenerator;
import fr.theorozier.procgen.common.world.gen.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.gen.DimensionHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldDimensionManager {
	
	private final DimensionHandler handler;
	
	private final File worldDirectory;
	private final File playersDirectory;
	private final File dimensionsDirectory;
	
	private final Map<String, File> dimensionsFiles = new HashMap<>();
	private final Map<String, WorldServer> dimensionsWorlds = new HashMap<>();
	private WorldServer[] dimensions;
	
	private final ExecutorService generatorComputer;
	
	public WorldDimensionManager(DimensionHandler handler, File worldDirectory) {
		
		this.handler = handler;
		this.worldDirectory = worldDirectory;
		
		if (!worldDirectory.isDirectory())
			worldDirectory.mkdirs();
		
		this.playersDirectory = new File(worldDirectory, "players");
		this.dimensionsDirectory = new File(worldDirectory, "dims");
		
		if (!this.playersDirectory.isDirectory())
			this.playersDirectory.mkdirs();
		
		if (!this.dimensionsDirectory.isDirectory())
			this.dimensionsDirectory.mkdirs();
		
		this.generatorComputer = Executors.newFixedThreadPool(2);
		
	}
	
	public File getWorldDirectory() {
		return this.worldDirectory;
	}
	
	public void load() {
	
		// TODO : Here, load all dimensions from the world directory
		
		this.handler.worldLoaded(this);
	
	}
	
	public void createNewDimension(String identifier, long seed, ChunkGeneratorProvider provider) {
		
		if (this.dimensionsWorlds.containsKey(identifier))
			throw new IllegalArgumentException("The dimension '" + identifier + "' already exists this manager !");
		
		WorldServer newDim = new WorldServer(this, seed, provider);
		
		this.dimensionsWorlds.put(identifier, newDim);
		
		WorldServer[] newDimensions = new WorldServer[this.dimensions.length + 1];
		System.arraycopy(this.dimensions, 0, newDimensions, 0, this.dimensions.length);
		newDimensions[this.dimensions.length] = newDim;
		
		this.dimensions = newDimensions;
		
	}
	
	public boolean hasDimension(String identifier) {
		return this.dimensionsWorlds.containsKey(identifier);
	}
	
	public ChunkGeneratorProvider getDimensionChunkGeneratorProvider(String identifier) {
		WorldServer world = this.dimensionsWorlds.get(identifier);
		return world == null ? null : world.getChunkGeneratorProvider();
	}
	
	public boolean isDimensionUsingChunkGenerator(String identifier, Class<? extends ChunkGenerator> generatorClass) {
		
		WorldServer world = this.dimensionsWorlds.get(identifier);
		
		if (world != null) {
			return world.getChunkGenerator().getClass().equals(generatorClass);
		} else {
			return false;
		}
		
	}
	
	public void update() {
		
		for (WorldServer dim : this.dimensions) {
			dim.update();
		}
		
	}

}
