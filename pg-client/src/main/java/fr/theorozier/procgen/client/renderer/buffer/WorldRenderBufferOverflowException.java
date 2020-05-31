package fr.theorozier.procgen.client.renderer.buffer;

public class WorldRenderBufferOverflowException extends RuntimeException {
	
	public WorldRenderBufferOverflowException(int remaining, int missing) {
		super("A render buffer overflown, current remaining: " + remaining + ", missing: " + missing + ".");
	}
	
}
