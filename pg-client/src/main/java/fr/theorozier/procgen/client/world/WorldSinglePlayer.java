package fr.theorozier.procgen.client.world;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.event.WorldChunkListener;
import fr.theorozier.procgen.common.world.event.WorldEntityListener;
import fr.theorozier.procgen.common.world.event.WorldLoadingListener;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

public class WorldSinglePlayer extends WorldClient implements
		WorldLoadingListener,
		WorldChunkListener,
		WorldEntityListener {
	
	private final WorldServer server;
	
	public WorldSinglePlayer(WorldServer server) {
		
		this.server = server;
		
		server.getEventManager().addEventListener(WorldLoadingListener.class, this);
		server.getEventManager().addEventListener(WorldChunkListener.class, this);
		server.getEventManager().addEventListener(WorldEntityListener.class, this);
		
	}
	
	public WorldServer getServerWorld() {
		return this.server;
	}
	
	@Override
	public void update() {
		
		this.server.update();
		
		super.update();
		
	}
	
	@Override
	public void worldChunkUpdated(WorldBase world, WorldChunk chunk) {
		this.eventManager.fireListeners(WorldChunkListener.class, l -> l.worldChunkUpdated(this, chunk));
	}
	
	@Override
	public void worldChunkBlockChanged(WorldBase world, WorldChunk chunk, BlockPositioned pos, BlockState state) {
		this.eventManager.fireListeners(WorldChunkListener.class, l -> l.worldChunkBlockChanged(this, chunk, pos, state));
	}
	
	@Override
	public void worldChunkLoaded(WorldBase world, WorldChunk chunk) {
		
		if (this.getSectionAt(chunk.getChunkPos().getX(), chunk.getChunkPos().getZ()) == null) {
			this.sections.put(chunk.getSection().getSectionPos(), chunk.getSection());
		}
		
		this.eventManager.fireListeners(WorldLoadingListener.class, l -> l.worldChunkLoaded(this, chunk));
		
	}
	
	@Override
	public void worldChunkUnloaded(WorldBase world, ImmutableBlockPosition position) {
		this.eventManager.fireListeners(WorldLoadingListener.class, l -> l.worldChunkUnloaded(this, position));
	}
	
	@Override
	public void worldEntityAdded(WorldBase world, Entity entity) {
		this.addEntity(entity);
	}
	
	@Override
	public void worldEntityRemoved(WorldBase world, Entity entity) {
	
	}
	
}
