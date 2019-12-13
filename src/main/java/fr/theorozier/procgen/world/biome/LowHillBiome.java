package fr.theorozier.procgen.world.biome;

import fr.theorozier.procgen.world.feature.ConfiguredFeature;
import fr.theorozier.procgen.world.feature.Features;
import fr.theorozier.procgen.world.feature.config.FeatureConfig;
import fr.theorozier.procgen.world.feature.config.PlacementFeatureConfig;
import fr.theorozier.procgen.world.feature.placement.ConfiguredPlacement;
import fr.theorozier.procgen.world.feature.placement.Placements;
import fr.theorozier.procgen.world.feature.placement.config.ChanceConfig;

public class LowHillBiome extends Biome {
	
	public LowHillBiome(int uid, String identifier) {
		
		super(uid, identifier, 0.27f, 14f, Biomes.GRASS_SURFACE, Biomes.UNDERWATER_DIRT_SURFACE);
		
		this.addFeature(Features.PLACEMENT, new PlacementFeatureConfig(
				new ConfiguredPlacement<>(Placements.SURFACE_CHANCE, new ChanceConfig(0.2f)),
				new ConfiguredFeature<>(Features.TREE, FeatureConfig.EMPTY)
		));
		
		addOres(this);
		addPlantGrass(this);
		addBasicFlowers(this);
		
	}
	
}
