package fr.theorozier.procgen.common.world.biome;

import fr.theorozier.procgen.common.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.common.world.feature.Features;
import fr.theorozier.procgen.common.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.common.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.common.world.feature.placement.ConfiguredPlacement;
import fr.theorozier.procgen.common.world.feature.placement.Placements;
import fr.theorozier.procgen.common.world.feature.placement.config.ChanceConfig;

public class PlainBiome extends Biome {
	
	public PlainBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.26f, 5f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.addFeature(Features.PLACEMENT, new PlacementFeatureConfig(
				new ConfiguredPlacement<>(Placements.SURFACE_CHANCE, new ChanceConfig(0.2f)),
				new ConfiguredFeature<>(Features.TREE, FeatureConfig.EMPTY)
		));
		
		addOres(this);
		addPlantGrass(this);
		addBasicFlowers(this);
		
	}
	
}
