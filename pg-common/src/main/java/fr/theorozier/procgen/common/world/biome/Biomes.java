package fr.theorozier.procgen.common.world.biome;

import fr.theorozier.procgen.common.world.biome.surface.*;

import java.util.HashMap;
import java.util.Map;

public class Biomes {

	private static final Map<Short, Biome> uidRegister = new HashMap<>();
	private static final Map<String, Biome> identifierRegister = new HashMap<>();
	
	public static final BiomeSurface                           NO_SURFACE = new BiomeSurface(0);
	public static final GrassSurface                        GRASS_SURFACE = new GrassSurface();
	public static final DesertSurface                      DESERT_SURFACE = new DesertSurface();
	public static final BeachSurface                        BEACH_SURFACE = new BeachSurface();
	public static final UnderwaterDirtSurface     UNDERWATER_DIRT_SURFACE = new UnderwaterDirtSurface();
	public static final UnderwaterSandSurface     UNDERWATER_SAND_SURFACE = new UnderwaterSandSurface();
	public static final UnderwaterGravelSurface UNDERWATER_GRAVEL_SURFACE = new UnderwaterGravelSurface();
	
	public static final EmptyBiome            EMPTY = registerBiome(new EmptyBiome(1, "empty"));
	public static final PlainBiome            PLAIN = registerBiome(new PlainBiome(2, "plain"));
	public static final ForestBiome          FOREST = registerBiome(new ForestBiome(3, "forest"));
	public static final LowHillBiome       LOW_HILL = registerBiome(new LowHillBiome(4, "low_hill"));
	public static final DesertBiome          DESERT = registerBiome(new DesertBiome(5, "desert"));
	public static final DesertHillBiome DESERT_HILL = registerBiome(new DesertHillBiome(6, "desert_hill"));
	public static final SavannaBiome        SAVANNA = registerBiome(new SavannaBiome(8, "savanna"));
	public static final JungleBiome          JUNGLE = registerBiome(new JungleBiome(10, "jungle"));
	public static final MountainsBiome    MOUNTAINS = registerBiome(new MountainsBiome(11, "moutains"));
	
	// Water
	public static final RiverBiome            RIVER = registerBiome(new RiverBiome(20, "river"));
	public static final OceanBiome            OCEAN = registerBiome(new OceanBiome(21, "ocean"));
	
	// Cold
	public static final TaigaBiome            TAIGA = registerBiome(new TaigaBiome(30, "taiga"));
	public static final TundraBiome          TUNDRA = registerBiome(new TundraBiome(31, "tundra"));
	
	public static <B extends Biome> B registerBiome(B biome) {
	
		uidRegister.put(biome.getUid(), biome);
		identifierRegister.put(biome.getIdentifier(), biome);
		
		return biome;
	
	}
	
	public static Biome getBiome(String identifier) {
		return identifierRegister.get(identifier);
	}
	
}
