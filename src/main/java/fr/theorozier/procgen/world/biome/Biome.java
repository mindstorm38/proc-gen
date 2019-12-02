package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.block.Blocks;
import fr.theorozier.procgen.util.ErrorUtils;
import fr.theorozier.procgen.world.biome.surface.BiomeSurface;
import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.Feature;
import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.PlacementFeature;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.config.OreFeatureConfig;
import fr.theorozier.procgen.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.world.feature.placement.ConfiguredPlacement;
import fr.theorozier.procgen.world.feature.placement.Placement;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.PlacementConfig;
import fr.theorozier.procgen.world.feature.placement.config.UndergroundConfig;
import io.sutil.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Biome {

	private final short uid;
	private final String identifier;
	
	private final float depth;
	private final float scale;
	private final BiomeWeatherRange weather;
	private final BiomeSurface surface;
	private final List<ConfiguredFeature<?>> features;
	
	public Biome(int uid, String identifier, float depth, float scale, BiomeWeatherRange weather, BiomeSurface surface) {
		
		if (uid <= 0)
			throw ErrorUtils.invalidUidArgument("Biome");
		
		this.uid = (short) uid;
		this.identifier = StringUtils.requireNonNullAndEmpty(identifier, "Biome identifier can't be empty.");
		
		this.depth = depth;
		this.scale = scale;
		this.weather = weather;
		this.surface = surface;
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
	
	public BiomeSurface getSurface() {
		return this.surface;
	}
	
	public <C extends FeatureConfig> void addFeature(Feature<C> feature, C config) {
		this.features.add(new ConfiguredFeature<>(feature, config));
	}
	
	public List<ConfiguredFeature<?>> getConfiguredFeatures() {
		return this.features;
	}
	
	@Override
	public String toString() {
		return "<biome '" + this.getIdentifier() + "'>";
	}
	
	public <PC extends PlacementConfig, C extends FeatureConfig> void addPlacedFeature(Placement<PC> placement, PC placementConfig, Feature<C> feature, C config) {
		
		this.addFeature(Features.PLACEMENT, new PlacementFeatureConfig(
				new ConfiguredPlacement<>(placement, placementConfig),
				new ConfiguredFeature<>(feature, config)
		));
		
	}
	
	// Common Features //
	
	public static void addOres(Biome biome) {
		
		biome.addPlacedFeature(
				Placements.UNDERGROUND,
				new UndergroundConfig(0, 128, 40, 0.4f),
				Features.ORE,
				new OreFeatureConfig(Blocks.COAL_ORE, 10, 20)
		);
		
	}
	
}
