package fr.theorozier.procgen.common.util.array.stack;

import fr.theorozier.procgen.common.util.ShortConsumer;

import java.util.NoSuchElementException;

public class FixedShortStack extends FixedStack {

	private final short[] stack;
	
	public FixedShortStack(int capacity) {
		this.stack = new short[capacity];
	}
	
	public boolean push(short val) {
		
		if (this.ptr < this.stack.length) {
			
			this.stack[this.ptr++] = val;
			return true;
			
		} else {
			return false;
		}
		
	}
	
	public short peek() {
		
		if (!isEmpty()) {
			return this.stack[--this.ptr];
		} else {
			throw new NoSuchElementException();
		}
		
	}
	
	public int get(int idx) {
		return this.stack[idx];
	}
	
	public void forEach(ShortConsumer consumer) {
		
		int size = this.ptr;
		short[] stack = this.stack;
		
		for (int i = 0; i < size; ++i) {
			consumer.accept(stack[i]);
		}
		
	}
	
	public void forEachAndClear(ShortConsumer consumer) {
		this.forEach(consumer);
		this.clear();
	}
	
	public void copyTo(FixedShortStack to) {
		if (to != this) {
			int destSize = Math.min(this.ptr, to.stack.length - 1);
			System.arraycopy(this.stack, 0, to.stack, 0, destSize);
			to.ptr = destSize;
		}
	}
	
}
