package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

public class WorldServerChunk extends WorldChunk {
	
	public WorldServerChunk(WorldServer world, WorldServerSection section, ImmutableBlockPosition position) {
		super(world, section, position);
	}
	
	public WorldServer getWorld() {
		return (WorldServer) super.getWorld();
	}
	
	public WorldServerSection getSection() {
		return (WorldServerSection) super.getSection();
	}
	
}
