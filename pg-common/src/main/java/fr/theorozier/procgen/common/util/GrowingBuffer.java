package fr.theorozier.procgen.common.util;

import java.nio.Buffer;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class GrowingBuffer<T extends Buffer> {
	
	private final IntFunction<T> bufferBuilder;
	private final Consumer<T> freeBuffer;
	private final BiConsumer<T, T> oldWriter;
	private final int grow;
	
	private T buffer = null;
	
	public GrowingBuffer(IntFunction<T> bufferBuilder, Consumer<T> freeBuffer, BiConsumer<T, T> oldWriter, int grow) {
		this.bufferBuilder = bufferBuilder;
		this.freeBuffer = freeBuffer;
		this.oldWriter = oldWriter;
		this.grow = grow;
	}
	
	public GrowingBuffer(IntFunction<T> bufferBuilder, Consumer<T> freeBuffer, BiConsumer<T, T> oldWriter) {
		this(bufferBuilder, freeBuffer, oldWriter, 2048);
	}
	
	public void alloc() {
		if (this.buffer == null) {
			this.buffer = this.bufferBuilder.apply(0);
		}
	}
	
	public void free() {
		if (this.buffer != null) {
			this.freeBuffer.accept(this.buffer);
			this.buffer = null;
		}
	}
	
	public void clear() {
		if (this.buffer != null) {
			this.buffer.clear();
		}
	}
	
	public void flip() {
		if (this.buffer != null) {
			this.buffer.flip();
		}
	}
	
	public int position() {
		return this.buffer == null ? 0 : this.buffer.position();
	}
	
	public void position(int position) {
		if (this.buffer != null) {
			this.buffer.position(position);
		}
	}
	
	public int limit() {
		return this.buffer == null ? 0 : this.buffer.limit();
	}
	
	public void limit(int limit) {
		if (this.buffer != null) {
			this.buffer.limit(limit);
		}
	}
	
	public T get() {
		return this.buffer;
	}
	
	/**
	 * <p>Ensure that the internal and returned buffer has specified available length after the buffer position.</p>
	 * <p><b><i>If <code>len = 0</code>, then the returned buffer can be null if it was not already initialized.</i></b></p>
	 * @param len Length to ensure after the buffer position.
	 * @return The internal buffer, can be null if .
	 */
	public T ensure(int len) {
		
		if (len < 1) {
			throw new IllegalArgumentException("Invalid len to ensure, minimum is 1.");
		}
		
		T buf = this.buffer;
		
		int cap, pos;
		
		if (buf == null) {
			cap = pos = 0;
		} else {
			cap = buf.capacity();
			pos = buf.position();
		}
		
		int need = pos + len;
		int miss = need - cap;
		
		if (miss > 0) {
			
			this.buffer = Objects.requireNonNull(this.bufferBuilder.apply(cap + this.round(miss)), "Buffer builder returned null.");
			
			if (buf != null) {
				
				buf.limit(pos);
				buf.position(0);
				
				this.oldWriter.accept(this.buffer, buf);
				this.freeBuffer.accept(buf);
				
			}
			
			buf = this.buffer;
			
		}
		
		buf.limit(buf.capacity());
		
		return buf;
		
	}
	
	private int round(int missing) {
		int grow = this.grow;
		return ((missing + grow - 1) / grow) * grow;
	}
	
}
