package fr.theorozier.procgen.client.renderer.util;

import fr.theorozier.procgen.common.util.GrowingBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;

public class MemoryGrowingBuffer<T extends Buffer> extends GrowingBuffer<T> {
	
	public MemoryGrowingBuffer(IntFunction<T> bufferBuilder, BiConsumer<T, T> oldWriter, int grow) {
		super(bufferBuilder, MemoryUtil::memFree, oldWriter, grow);
	}
	
	public MemoryGrowingBuffer(IntFunction<T> bufferBuilder, BiConsumer<T, T> oldWriter) {
		super(bufferBuilder, MemoryUtil::memFree, oldWriter);
	}
	
	public static MemoryGrowingBuffer<FloatBuffer> newFloatBuffer(int grow) {
		return new MemoryGrowingBuffer<>(MemoryUtil::memAllocFloat, FloatBuffer::put, grow);
	}
	
	public static MemoryGrowingBuffer<FloatBuffer> newFloatBuffer() {
		return new MemoryGrowingBuffer<>(MemoryUtil::memAllocFloat, FloatBuffer::put);
	}
	
	public static MemoryGrowingBuffer<IntBuffer> newIntBuffer(int grow) {
		return new MemoryGrowingBuffer<>(MemoryUtil::memAllocInt, IntBuffer::put, grow);
	}
	
	public static MemoryGrowingBuffer<IntBuffer> newIntBuffer() {
		return new MemoryGrowingBuffer<>(MemoryUtil::memAllocInt, IntBuffer::put);
	}
	
}
