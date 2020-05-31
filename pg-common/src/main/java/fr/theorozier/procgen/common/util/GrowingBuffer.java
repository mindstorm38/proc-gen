package fr.theorozier.procgen.common.util;

import java.nio.Buffer;
import java.util.Objects;

public class GrowingBuffer<T extends Buffer> {
	
	protected final BufferAlloc<T> alloc;
	protected final BufferRealloc<T> realloc;
	protected final BufferFree<T> free;
	protected final int grow;
	
	private T buffer = null;
	
	public GrowingBuffer(BufferAlloc<T> alloc, BufferRealloc<T> realloc, BufferFree<T> free, int grow) {
		this.alloc = alloc;
		this.realloc = realloc;
		this.free = free;
		this.grow = grow;
	}
	
	public GrowingBuffer(BufferAlloc<T> alloc, BufferRealloc<T> realloc, BufferFree<T> free) {
		this(alloc, realloc, free, 2048);
	}
	
	protected T alloc(int size) {
		return this.alloc.alloc(size);
	}
	
	protected T realloc(T old, int nsize) {
		return this.realloc.realloc(old, nsize);
	}
	
	protected void free(T buf) {
		this.free.free(buf);
	}
	
	public void alloc() {
		if (this.buffer == null) {
			this.buffer = this.alloc(0);
		}
	}
	
	public void free() {
		if (this.buffer != null) {
			this.free(this.buffer);
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
			
			int newcap = cap + this.round(miss);
			
			if (this.buffer != null) {
				buf = Objects.requireNonNull(this.realloc(this.buffer, newcap), "Realloc method returned null !");
			} else {
				buf = Objects.requireNonNull(this.alloc(newcap), "");
			}
			
			this.buffer = buf;
			
			/*
			this.buffer = Objects.requireNonNull(this.alloc.apply(cap + this.round(miss)), "Buffer builder returned null.");
			
			if (buf != null) {
				
				buf.limit(pos);
				buf.position(0);
				
				this.realloc.accept(this.buffer, buf);
				this.free.accept(buf);
				
			}
			
			buf = this.buffer;*/
			
		}
		
		buf.limit(buf.capacity());
		
		return buf;
		
	}
	
	private int round(int missing) {
		int grow = this.grow;
		return ((missing + grow - 1) / grow) * grow;
	}
	
	@FunctionalInterface
	public interface BufferAlloc<T extends Buffer> {
		T alloc(int size);
	}
	
	@FunctionalInterface
	public interface BufferRealloc<T extends Buffer> {
		T realloc(T old, int nsize);
	}
	
	@FunctionalInterface
	public interface BufferFree<T extends Buffer> {
		void free(T buf);
	}
	
}
