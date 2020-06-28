package fr.theorozier.procgen.client.renderer.buffer;

public class WorldRenderBufferOverflowException extends RuntimeException {
	
	public WorldRenderBufferOverflowException(int capacity, int remaining, int missing) {
		super("A render buffer overflown, current cap: " + capacity + ", remaining: " + remaining + ", missing: " + missing + ".");
	}
	
	public WorldRenderBufferOverflowException(int cursor, int expected) {
		super("A render buffer overflown, cursor: " + cursor + ", expected: " + expected + ".");
	}
	
}
