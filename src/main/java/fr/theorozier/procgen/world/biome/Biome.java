package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.util.ErrorUtils;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.Feature;
import fr.theorozier.procgen.world.feature.FeatureConfig;
import io.sutil.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Biome {

	private final short uid;
	private final String identifier;
	
	private final float depth;
	private final float scale;
	private final BiomeWeatherRange weather;
	private final List<ConfiguredFeature<?>> features;
	
	public Biome(int uid, String identifier, float depth, float scale, BiomeWeatherRange weather) {
		
		if (uid <= 0)
			throw ErrorUtils.invalidUidArgument("Biome");
		
		this.uid = (short) uid;
		this.identifier = StringUtils.requireNonNullAndEmpty(identifier, "Biome identifier can't be empty.");
		
		this.depth = depth;
		this.scale = scale;
		this.weather = weather;
		this.features = new ArrayList<>();
		
	}
	
	public short getUid() {
		return this.uid;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public float getDepth() {
		return this.depth;
	}
	
	public float getScale() {
		return this.scale;
	}
	
	public BiomeWeatherRange getWeather() {
		return this.weather;
	}
	
	public <C extends FeatureConfig> void addFeature(Feature<C> feature, C config) {
		this.features.add(new ConfiguredFeature<>(feature, config));
	}

}
