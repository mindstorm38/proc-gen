package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

public class WorldServerChunk extends WorldChunk {
	
	public WorldServerChunk(WorldDimension world, WorldServerSection section, ImmutableBlockPosition position) {
		super(world, section, position);
	}
	
	public WorldDimension getWorld() {
		return (WorldDimension) super.getWorld();
	}
	
	public WorldServerSection getSection() {
		return (WorldServerSection) super.getSection();
	}
	
}
