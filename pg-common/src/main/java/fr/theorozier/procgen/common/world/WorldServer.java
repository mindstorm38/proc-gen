package fr.theorozier.procgen.common.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.theorozier.procgen.common.util.SaveUtils;
import fr.theorozier.procgen.common.world.load.DimensionMetadata;
import fr.theorozier.procgen.common.world.task.WorldTaskManager;
import io.msengine.common.util.GameProfiler;
import io.sutil.profiler.Profiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * A save and creation manager for real world, composed of multiple dimensions.<br>
 * <b>Methods in this class should not be used from other threads than main.</b>
 *
 * @author Theo Rozier
 *
 */
public class WorldServer {
	
	private static final String DIMENSION_METADATA_FILE = "meta.json";
	private static final Profiler PROFILER = GameProfiler.getInstance();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(DimensionMetadata.class, new DimensionMetadata.Serializer())
			.setPrettyPrinting()
			.create();
	
	private final File worldDirectory;
	private final File playersDirectory;
	private final File dimensionsDirectory;
	
	private final Map<String, WorldDimension> dimensionsWorlds = new HashMap<>();
	private WorldDimension[] dimensions = {};
	
	private WorldTaskManager taskManager = null;
	
	public WorldServer(File worldDirectory) {
		
		SaveUtils.mkdirOrThrowException(worldDirectory, "The world directory already exists but it's a file.");
		
		this.worldDirectory = worldDirectory;
		this.playersDirectory = new File(worldDirectory, "players");
		this.dimensionsDirectory = new File(worldDirectory, "dims");
		
		SaveUtils.mkdirOrThrowException(this.playersDirectory, "The players directory already exists but it's a file.");
		SaveUtils.mkdirOrThrowException(this.dimensionsDirectory, "The dimensions directory already exists but it's a file.");
		
	}
	
	/**
	 * @return World directory containing all dimensions and players.
	 */
	public File getWorldDirectory() {
		return this.worldDirectory;
	}
	
	/**
	 * Load the world dimensions from file system. This is called before starting a world.
	 */
	public void load(WorldTaskManager taskManager) {
	
		this.taskManager = taskManager;
		
		this.dimensionsDirectory.listFiles((File file) -> {
		
			if (file.isDirectory()) {
				String identifier = file.getName();
				if (SaveUtils.isValidSavableName(identifier)) {
					this.loadDimension(identifier);
				}
			}
			
			return false;
		
		});
		
	}
	
	/**
	 * Called to unload world dimensions, after this, this world {@link #load(WorldTaskManager) still loadable}.
	 */
	public void unload() {
		
		for (WorldDimension dim : this.dimensions) {
			saveDimensionMetadata(dim);
			// TODO : Final save all dimensions.
		}
		
		this.dimensions = new WorldDimension[0];
		this.dimensionsWorlds.clear();
		
		this.taskManager = null;
		
	}
	
	/**
	 * Create a new dimension if not already existing.
	 * @param identifier The world dimension identifier.
	 * @param metadata The dimension metadata, contains seed and chunk generator provider.
	 * @return The new dimension's world instance.
	 * @throws IllegalStateException If the world can't be created, or from {@link #saveDimensionMetadata(WorldDimension)}.
	 * @throws IllegalArgumentException If the world identifier is already used or the identifier is invalid (according to {@link SaveUtils#isValidSavableName(String)}).
	 */
	public WorldDimension createNewDimension(String identifier, DimensionMetadata metadata) {
		
		if (this.hasDimension(identifier))
			throw new IllegalArgumentException("The dimension '" + identifier + "' already exists in this world !");
		
		if (!SaveUtils.isValidSavableName(identifier))
			throw new IllegalArgumentException("Invalid identifier format.");
		
		File dimensionDir = new File(this.dimensionsDirectory, identifier);
		SaveUtils.mkdirOrThrowException(dimensionDir, "The world directory can't be created because a file with same name already exists.");
		
		WorldDimension newDim = new WorldDimension(this, identifier, dimensionDir, metadata);
		saveDimensionMetadata(newDim);
		
		return this.rawAddDimension(newDim);
		
	}
	
	public WorldDimension loadDimension(String identifier) {
		
		if (this.hasDimension(identifier))
			return this.dimensionsWorlds.get(identifier);
		
		File dimensionDir = new File(this.dimensionsDirectory, identifier);
		
		if (!dimensionDir.isDirectory())
			throw new IllegalStateException("Can't load dimension '" + identifier + "' because its directory is not a directory '" + dimensionDir + "'.");
		
		DimensionMetadata metadata = loadDimensionMetadata(dimensionDir);
		WorldDimension dimension = new WorldDimension(this, identifier, dimensionDir, metadata);
		
		return this.rawAddDimension(dimension);
		
	}
	
	private WorldDimension rawAddDimension(WorldDimension dimension) {
		
		this.dimensionsWorlds.put(dimension.getIdentifier(), dimension);
		
		WorldDimension[] newDimensions = new WorldDimension[this.dimensions.length + 1];
		System.arraycopy(this.dimensions, 0, newDimensions, 0, this.dimensions.length);
		newDimensions[this.dimensions.length] = dimension;
		
		this.dimensions = newDimensions;
		
		return dimension;
		
	}
	
	/**
	 * Used to know if a dimension identifier
	 * @param identifier Dimension identifier.
	 * @return True if this identifier already register a dimension.
	 */
	public boolean hasDimension(String identifier) {
		return this.dimensionsWorlds.containsKey(identifier);
	}
	
	/**
	 * Get the dimension registered with specified identifier.
	 * @param identifier The dimension identifier.
	 * @return The dimension or Null if none loaded with this identifier.
	 */
	public WorldDimension getDimension(String identifier) {
		return this.dimensionsWorlds.get(identifier);
	}
	
	/**
	 * @return The first registered dimension.
	 */
	public WorldDimension getMainDimension() {
		return this.dimensions.length == 0 ? null : this.dimensions[0];
	}
	
	/**
	 * Update all dimensions.
	 */
	public void update() {
		
		for (WorldDimension dim : this.dimensions) {
			
			PROFILER.startSection(dim.getIdentifier());
			dim.update();
			PROFILER.endSection();
			
		}
		
	}

	public Optional<WorldTaskManager> getTaskManager() {
		return Optional.ofNullable(this.taskManager);
	}

	public void withTaskManager(Consumer<WorldTaskManager> manager) {
		if (this.taskManager != null) {
			manager.accept(this.taskManager);
		}
	}

	/**
	 * Save metadata of specified dimension into its directory.
	 * @param dimension Dimension instance.
	 * @throws IllegalStateException If the metadata file is a directory, or if some I/O failure occurs.
	 */
	public static void saveDimensionMetadata(WorldDimension dimension) {
		
		File metadataFile = getDimensionMetadataFile(dimension.getDirectory());
		
		if (metadataFile.isDirectory())
			throw new IllegalStateException("The metadata file for dimension '" + dimension.getIdentifier() + "'");
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metadataFile))) {
			GSON.toJson(dimension.getMetadata(), DimensionMetadata.class, writer);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to save dimension metadata to '" + metadataFile + "'.", e);
		}
		
	}
	
	/**
	 * Load dimension metadata from the specified dimension directory.
	 * @param dimensionDirectory Dimension directory.
	 * @return Dimension metadata.
	 * @throws IllegalStateException If metadata file doesn't exists, or if some I/O failure occurs.
	 */
	public static DimensionMetadata loadDimensionMetadata(File dimensionDirectory) {
		
		File metadataFile = getDimensionMetadataFile(dimensionDirectory);
	
		if (metadataFile.isFile()) {
			
			try (BufferedReader reader = new BufferedReader(new FileReader(metadataFile))) {
				return GSON.fromJson(reader, DimensionMetadata.class);
			} catch (IOException e) {
				throw new IllegalStateException("Failed to load dimension metadata from '" + metadataFile + "'.");
			}
			
		} else {
			throw new IllegalStateException("The metadata file doesn't exists (at '" + metadataFile + "').");
		}
		
	}
	
	/**
	 * Refresh dimension metadata for specified dimension, log warnings if not possible.
	 * @param dimension The dimension to refresh metadata.
	 */
	public static void refreshDimensionMetadata(WorldDimension dimension) {
		
		try {
			
			DimensionMetadata newMetadata = loadDimensionMetadata(dimension.getDirectory());
			
			if (newMetadata != null) {
				dimension.refreshMetadata(newMetadata);
			} else {
				LOGGER.warning("Failed to get new metadata for refreshing dimension '" + dimension.getIdentifier() + "'.");
			}
			
		} catch (IllegalStateException e) {
			LOGGER.log(Level.WARNING, "Error while refreshing dimension '" + dimension.getIdentifier() + ".", e);
		}
		
	}
	
	/**
	 * Static utility to get metadata file for specified dimension directory.
	 * @param dimensionDirectory Dimension directory.
	 * @return Metadata file.
	 */
	public static File getDimensionMetadataFile(File dimensionDirectory) {
		return new File(dimensionDirectory, DIMENSION_METADATA_FILE);
	}

}
