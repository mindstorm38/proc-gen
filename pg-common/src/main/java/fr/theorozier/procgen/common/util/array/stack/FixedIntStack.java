package fr.theorozier.procgen.common.util.array.stack;

import java.util.NoSuchElementException;
import java.util.function.IntConsumer;

public class FixedIntStack extends FixedStack {
	
	private final int[] stack;
	
	public FixedIntStack(int capacity) {
		this.stack = new int[capacity];
	}
	
	public boolean push(int val) {
		
		if (this.ptr < this.stack.length) {
			
			this.stack[this.ptr++] = val;
			return true;
			
		} else {
			return false;
		}
		
	}
	
	public int peek() {
		
		if (!isEmpty()) {
			return this.stack[--this.ptr];
		} else {
			throw new NoSuchElementException();
		}
		
	}
	
	public int get(int idx) {
		return this.stack[idx];
	}
	
	public void forEach(IntConsumer consumer) {
		
		int size = this.ptr;
		int[] stack = this.stack;
		
		for (int i = 0; i < size; ++i) {
			consumer.accept(stack[i]);
		}
		
	}
	
	public void forEachAndClear(IntConsumer consumer) {
		this.forEach(consumer);
		this.clear();
	}
	
}
