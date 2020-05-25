package fr.theorozier.procgen.client.renderer.world.util;

import io.msengine.client.renderer.vertex.VertexArrayFormat;
import io.msengine.client.renderer.vertex.VertexBufferFormat;
import io.msengine.client.renderer.vertex.VertexElement;

public class WorldSequentialFormat extends VertexArrayFormat {
	
	public static final String SEQUENTIAL_MAIN = "main";
	public static final WorldSequentialFormat SEQUENTIAL = new WorldSequentialFormat();
	
	private WorldSequentialFormat(VertexBufferFormat... buffers) {
		super(new VertexBufferFormat(SEQUENTIAL_MAIN, VertexElement.POSITION_3F, VertexElement.COLOR_3F, VertexElement.TEX_COORD_2F));
	}
	
}
