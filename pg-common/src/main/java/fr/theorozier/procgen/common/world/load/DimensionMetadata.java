package fr.theorozier.procgen.common.world.load;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import fr.theorozier.procgen.common.world.gen.provider.ChunkGeneratorProvider;
import fr.theorozier.procgen.common.world.gen.provider.ChunkGeneratorProviders;
import io.msengine.common.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.Objects;

public class DimensionMetadata {

	private static final String METADATA_FORMAT = "format";
	private static final String METADATA_SEED   = "seed";
	private static final String METADATA_CGP    = "chunk_generator_provider";
	private static final String METADATA_CGO    = "chunk_generator_options";
	private static final String METADATA_TIME   = "time";
	
	private final long seed;
	private final ChunkGeneratorProvider chunkGeneratorProvider;
	private final JsonObject chunkGeneratorOptions;
	
	private long time = 0L;
	
	public DimensionMetadata(long seed, ChunkGeneratorProvider chunkGeneratorProvider, JsonObject chunkGeneratorOptions) {
		
		this.seed = seed;
		this.chunkGeneratorProvider = Objects.requireNonNull(chunkGeneratorProvider);
		this.chunkGeneratorOptions = Objects.requireNonNull(chunkGeneratorOptions);
		
	}
	
	public DimensionMetadata(long seed, ChunkGeneratorProvider chunkGeneratorProvider) {
		this(seed, chunkGeneratorProvider, new JsonObject());
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public ChunkGeneratorProvider getChunkGeneratorProvider() {
		return this.chunkGeneratorProvider;
	}
	
	public JsonObject getChunkGeneratorOptions() {
		return this.chunkGeneratorOptions;
	}
	
	public long getTime() {
		return this.time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public void setDynamics(DimensionMetadata other) {
		this.time = other.time;
	}
	
	public static class Serializer implements JsonSerializer<DimensionMetadata>, JsonDeserializer<DimensionMetadata> {
		
		@Override
		public JsonElement serialize(DimensionMetadata src, Type typeOfSrc, JsonSerializationContext context) {
			
			JsonObject json = new JsonObject();
			
			// Common static infos
			json.addProperty(METADATA_FORMAT, 1);
			json.addProperty(METADATA_SEED, src.seed);
			json.addProperty(METADATA_CGP, src.chunkGeneratorProvider.getIdentifier());
			json.add(METADATA_CGO, src.chunkGeneratorOptions);
			
			// Common dynamics infos
			json.addProperty(METADATA_TIME, src.time);
			
			return json;
			
		}
		
		@Override
		public DimensionMetadata deserialize(JsonElement root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (!root.isJsonObject())
				throw new JsonParseException("Dimension metadata root element should be an object.");
			
			JsonObject json = root.getAsJsonObject();
			
			// Common static infos
			int format = JsonUtils.getInt(json, METADATA_FORMAT, 1);
			long seed = JsonUtils.getLongRequired(json, METADATA_SEED, "Dimension metadata long 'seed' is required.");
			String chunkGeneratorProviderIdentifier = JsonUtils.getStringRequired(json, METADATA_CGP, "Dimension metadata string 'chunk_generator_provider' is required.");
			
			ChunkGeneratorProvider chunkGeneratorProvider = ChunkGeneratorProviders.get(chunkGeneratorProviderIdentifier);
			
			if (chunkGeneratorProvider == null)
				throw new JsonParseException("Invalid dimension metadata chunk generator provider '" + chunkGeneratorProviderIdentifier + "', not registered.");
			
			JsonObject chunkGeneratorOptions;
			
			if (json.has(METADATA_CGO)) {
				
				if (!json.get(METADATA_CGO).isJsonObject())
					throw new JsonParseException("Invalid dimension metadata chunk generator options, if provided it must be an object.");
				
				chunkGeneratorOptions = json.getAsJsonObject(METADATA_CGO);
				
			} else {
				chunkGeneratorOptions = new JsonObject();
			}
			
			DimensionMetadata metadata = new DimensionMetadata(seed, chunkGeneratorProvider, chunkGeneratorOptions);
			
			// Common dynamics infos
			metadata.setTime(JsonUtils.getLong(json, METADATA_TIME, 0L));
			
			return metadata;
			
		}
		
	}
	
}
