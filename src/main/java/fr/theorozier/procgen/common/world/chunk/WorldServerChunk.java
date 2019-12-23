package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

public class WorldServerChunk extends WorldChunk {
	
	public WorldServerChunk(WorldServer world, WorldServerSection section, ImmutableBlockPosition position) {
		super(world, section, position);
	}
	
	public WorldServer getWorldServer() {
		return (WorldServer) this.getWorld();
	}
	
	public WorldServerSection getServerSection() {
		return (WorldServerSection) this.getSection();
	}
	
}
