package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.block.Block;
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
import fr.theorozier.procgen.world.feature.config.PlantFeatureConfig;
import fr.theorozier.procgen.world.feature.placement.ConfiguredPlacement;
import fr.theorozier.procgen.world.feature.placement.Placement;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.*;
import io.msengine.common.util.Color;
import io.sutil.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Biome {

	private final short uid;
	private final String identifier;
	
	private final float depth;
	private final float scale;
	private final BiomeSurface surface;
	private final BiomeSurface underwaterSurface;
	private final List<ConfiguredFeature<?>> features;
	
	private final Color foliageColor;
	private final Color grassColor;
	
	public Biome(int uid, String identifier, float depth, float scale, BiomeSurface surface, BiomeSurface underwaterSurface) {
		
		if (uid <= 0)
			throw ErrorUtils.invalidUidArgument("Biome");
		
		this.uid = (short) uid;
		this.identifier = StringUtils.requireNonNullAndEmpty(identifier, "Biome identifier can't be empty.");
		
		this.depth = depth;
		this.scale = scale;
		this.surface = surface;
		this.underwaterSurface = underwaterSurface;
		this.features = new ArrayList<>();
		
		this.foliageColor = new Color(90, 172, 66);
		this.grassColor = new Color(90, 172, 66);
		
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
	
	public BiomeSurface getSurface() {
		return this.surface;
	}
	
	public BiomeSurface getUnderwaterSurface() {
		return this.underwaterSurface;
	}
	
	public Color getFoliageColor() {
		return this.foliageColor;
	}
	
	public Color getGrassColor() {
		return this.grassColor;
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
	
	public static void addNormalForest(Biome biome) {
		
		biome.addPlacedFeature(
				Placements.SURFACE_COUNT_EXTRA,
				new CountExtraConfig(10, 1, 0.2f),
				Features.TREE,
				FeatureConfig.EMPTY
		);
		
	}
	
	public static void addBasicFlowers(Biome biome) {
		
		biome.addPlacedFeature(
				Placements.SURFACE_CHANCE_MULTIPLE,
				new ChanceCountConfig(10, 0.2f),
				Features.PLANT,
				new PlantFeatureConfig(Blocks.PLANT_POPPY, Biome::basicFlowersCanPlaceOn)
		);
		
		biome.addPlacedFeature(
				Placements.SURFACE_CHANCE_MULTIPLE,
				new ChanceCountConfig(10, 0.2f),
				Features.PLANT,
				new PlantFeatureConfig(Blocks.PLANT_DANDELION, Biome::basicFlowersCanPlaceOn)
		);
		
		biome.addPlacedFeature(
				Placements.SURFACE_COUNT,
				new CountConfig(30),
				Features.PLANT,
				new PlantFeatureConfig(Blocks.PLANT_GRASS, Biome::basicFlowersCanPlaceOn)
		);
		
	}
	
	public static boolean basicFlowersCanPlaceOn(Block block) {
		return block == Blocks.GRASS || block == Blocks.DIRT;
	}
	
}
