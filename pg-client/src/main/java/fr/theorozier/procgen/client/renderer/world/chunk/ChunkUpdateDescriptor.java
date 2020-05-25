package fr.theorozier.procgen.client.renderer.world.chunk;

import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

import java.util.Objects;

@Deprecated
public class ChunkUpdateDescriptor {
	
	private final ImmutableBlockPosition chunkPosition;
	private final BlockRenderLayer renderLayer;
	
	public ChunkUpdateDescriptor(ImmutableBlockPosition position, BlockRenderLayer renderLayer) {
		
		this.chunkPosition = position;
		this.renderLayer = renderLayer;
		
	}
	
	public ImmutableBlockPosition getChunkPosition() {
		return this.chunkPosition;
	}
	
	public BlockRenderLayer getRenderLayer() {
		return this.renderLayer;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChunkUpdateDescriptor that = (ChunkUpdateDescriptor) o;
		return chunkPosition.equals(that.chunkPosition) && renderLayer == that.renderLayer;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(chunkPosition, renderLayer);
	}
	
}
