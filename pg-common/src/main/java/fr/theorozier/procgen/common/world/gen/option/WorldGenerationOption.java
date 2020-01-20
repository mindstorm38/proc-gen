package fr.theorozier.procgen.common.world.gen.option;

public class WorldGenerationOption {
	
	private final long seed;
	
	public WorldGenerationOption(long seed) {
		this.seed = seed;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
}
