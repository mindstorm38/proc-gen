package fr.theorozier.procgen.renderer.world;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.renderer.world.block.BlockFaces;
import fr.theorozier.procgen.renderer.world.block.BlockRenderer;
import fr.theorozier.procgen.renderer.world.block.BlockRenderers;
import fr.theorozier.procgen.util.array.BufferedFloatArray;
import fr.theorozier.procgen.util.array.BufferedIntArray;
import fr.theorozier.procgen.world.*;
import fr.theorozier.procgen.world.chunk.Chunk;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.type.BasicFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static fr.theorozier.procgen.world.World.CHUNK_SIZE;
import static fr.theorozier.procgen.world.World.CHUNK_SIZE_MINUS;

public class WorldChunkRenderer implements WorldChunkUpdatedListener {
	
	private final WorldRenderer renderer;
	private final Chunk chunk;
	private final World world;
	private final TextureMap terrainMap;
	
	private final Map<Integer, WorldChunkRenderer> neighbours;
	private final boolean[] lastNeighbours;
	
	private IndicesDrawBuffer drawBuffer;
	
	private float distanceToCameraSquared;
	
	WorldChunkRenderer(WorldRenderer renderer, Chunk chunk) {
		
		this.renderer = renderer;
		this.chunk = chunk;
		this.world = chunk.getWorld();
		this.terrainMap = renderer.getTerrainMap();
		
		this.neighbours = new HashMap<>();
		this.lastNeighbours = new boolean[Direction.values().length];
		
		this.distanceToCameraSquared = 0;
		
	}
	
	public BlockPosition getChunkPosition() {
		return this.chunk.getChunkPosition();
	}
	
	void init() {
		
		this.drawBuffer = this.renderer.getShaderManager().createBasicDrawBuffer(false, true);
	
		this.refreshBuffers();
		
		this.chunk.addUpdatedListener(this);
		
	}
	
	void delete() {
		
		this.drawBuffer.delete();
		this.chunk.removeUpdatedListener(this);
		
	}
	
	void setNeighbour(Direction face, WorldChunkRenderer cr) {
		this.neighbours.put(face.ordinal(), cr);
	}
	
	void removeNeighbour(Direction face) {
		this.neighbours.remove(face.ordinal());
	}
	
	void checkLastNeighbours() {
		
		boolean neighbour;
		boolean refresh = false;
		
		for (int i = 0; i < Direction.values().length; i++) {
			
			neighbour = this.neighbours.containsKey(i);
			
			if (!refresh && neighbour != this.lastNeighbours[i])
				refresh = true;
			
			this.lastNeighbours[i] = neighbour;
			
		}
		
		if (refresh)
			this.refreshBuffers();
		
	}
	
	float updateDistanceToCamera(float x, float y, float z) {
		return this.distanceToCameraSquared = this.chunk.getChunkPosition().distSquared(x, y, z);
	}
	
	float getDistanceToCameraSquared() {
		return this.distanceToCameraSquared;
	}
	
	private void refreshBuffers() {
		
		FloatBuffer verticesBuf = null;
		FloatBuffer texcoordsBuf = null;
		IntBuffer indicesBuf = null;
		
		try {
			
			BufferedFloatArray vertices = new BufferedFloatArray();
			BufferedFloatArray texcoords = new BufferedFloatArray();
			BufferedIntArray indices = new BufferedIntArray();
			
			BlockFaces faces = new BlockFaces();
			WorldBlock worldBlock;
			BlockRenderer renderer;
			
			int cx = this.chunk.getChunkPosition().getX();
			int cy = this.chunk.getChunkPosition().getY();
			int cz = this.chunk.getChunkPosition().getZ();
			
			int wx, wy, wz;
			int lidx = 0;
			
			for (int x = 0; x < CHUNK_SIZE; x++) {
				for (int y = 0; y < CHUNK_SIZE; y++) {
					for (int z = 0; z < CHUNK_SIZE; z++) {
						
						if (!this.chunk.hasBlockAtRelative(x, y, z))
							continue;
						
						worldBlock = this.chunk.getBlockAtRelative(x, y, z);
						renderer = BlockRenderers.getRenderer(worldBlock.getBlockType());
						
						if (renderer != null) {
							
							wx = cx + x;
							wy = cy + y;
							wz = cz + z;
							
							if (y < CHUNK_SIZE_MINUS)
								 faces.topBlock(this.chunk.getBlockTypeAtRelative(x, y + 1, z));
							else faces.topBlock(this.world.getBlockTypeAt(wx, wy + 1, wz));
							
							if (y > 0)
								 faces.bottomBlock(this.chunk.getBlockTypeAtRelative(x, y - 1, z));
							else faces.bottomBlock(this.world.getBlockTypeAt(wx, wy - 1, wz));
							
							if (x < CHUNK_SIZE_MINUS)
								 faces.northBlock(this.chunk.getBlockTypeAtRelative(x + 1, y, z));
							else faces.northBlock(this.world.getBlockTypeAt(wx + 1, wy, wz));
							
							if (x > 0)
								 faces.southBlock(this.chunk.getBlockTypeAtRelative(x - 1, y, z));
							else faces.southBlock(this.world.getBlockTypeAt(wx - 1, wy, wz));
							
							if (z < CHUNK_SIZE_MINUS)
								 faces.eastBlock(this.chunk.getBlockTypeAtRelative(x, y, z + 1));
							else faces.eastBlock(this.world.getBlockTypeAt(wx, wy, wz + 1));
							
							if (z > 0)
								 faces.westBlock(this.chunk.getBlockTypeAtRelative(x, y, z - 1));
							else faces.westBlock(this.world.getBlockTypeAt(wx, wy, wz - 1));
							
							lidx = renderer.getRenderData(worldBlock, wx, wy, wz, lidx, faces, this.terrainMap, vertices, texcoords, indices);
							
						}
						
					}
				}
			}
			
			verticesBuf = MemoryUtil.memAllocFloat(vertices.getSize());
			texcoordsBuf = MemoryUtil.memAllocFloat(texcoords.getSize());
			indicesBuf = MemoryUtil.memAllocInt(this.drawBuffer.setIndicesCount(indices.getSize()));
			
			verticesBuf.put(vertices.result());
			texcoordsBuf.put(texcoords.result());
			indicesBuf.put(indices.result());
			
			verticesBuf.flip();
			texcoordsBuf.flip();
			indicesBuf.flip();
			
			this.drawBuffer.bindVao();
			this.drawBuffer.uploadVboData(BasicFormat.BASIC3D_POSITION, verticesBuf, BufferUsage.DYNAMIC_DRAW);
			this.drawBuffer.uploadVboData(BasicFormat.BASIC_TEX_COORD, texcoordsBuf, BufferUsage.DYNAMIC_DRAW);
			this.drawBuffer.uploadIboData(indicesBuf, BufferUsage.DYNAMIC_DRAW);
			
		} finally {
			
			BufferUtils.safeFree(verticesBuf);
			BufferUtils.safeFree(texcoordsBuf);
			BufferUtils.safeFree(indicesBuf);
			
		}
	
	}
	
	void render(float maxdist) {
		
		if (this.distanceToCameraSquared <= maxdist)
			render();
		
	}
	
	void render() {
		this.drawBuffer.drawElements();
	}
	
	@Override
	public void worldChunkUpdated(Chunk chunk, int x, int y, int z, Block block) {
		
		if (this.chunk == chunk) {
			this.refreshBuffers();
		}
		
	}
	
	@Override
	public void worldChunkUpdated(Chunk chunk) {
		
		if (this.chunk == chunk) {
			this.refreshBuffers();
		}
		
	}
	
}
