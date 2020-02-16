package fr.theorozier.procgen.client.world;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.chunk.WorldChunk;
import fr.theorozier.procgen.common.world.event.WorldChunkListener;
import fr.theorozier.procgen.common.world.event.WorldEntityListener;
import fr.theorozier.procgen.common.world.event.WorldLoadingListener;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

import java.util.Objects;

public class WorldSinglePlayer extends WorldClient implements
		WorldLoadingListener,
		WorldChunkListener,
		WorldEntityListener {
	
	private final WorldDimension server;
	
	public WorldSinglePlayer(WorldDimension server) {
		
		this.server = Objects.requireNonNull(server, "World server expected for a WorldSinglePlayer");
		
		server.forEachSection(sec -> this.sections.put(sec.getSectionPos(), sec));
		this.entities.addAll(server.getEntitiesView());
		
		server.getEventManager().addEventListener(WorldLoadingListener.class, this);
		server.getEventManager().addEventListener(WorldChunkListener.class, this);
		server.getEventManager().addEventListener(WorldEntityListener.class, this);
		
	}
	
	@Override
	public void unload() {
		
		this.server.getEventManager().removeEventListener(WorldLoadingListener.class, this);
		this.server.getEventManager().removeEventListener(WorldChunkListener.class, this);
		this.server.getEventManager().removeEventListener(WorldEntityListener.class, this);
		
	}
	
	public WorldDimension getServerWorld() {
		return this.server;
	}
	
	@Override
	public void update() {
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
		
		if (this.entitiesById.containsKey(entity.getUid()))
			return;
		
		if (this.entitiesById.put(entity.getUid(), entity) == null)
			this.entities.add(entity);
			
		this.eventManager.fireListeners(WorldEntityListener.class, l -> l.worldEntityAdded(this, entity));
		
	}
	
	@Override
	public void worldEntityRemoved(WorldBase world, Entity entity) {
		
		if (this.entitiesById.remove(entity.getUid()) != null)
			this.entities.remove(entity);
		
		this.eventManager.fireListeners(WorldEntityListener.class, l -> l.worldEntityRemoved(this, entity));
		
	}
	
}
