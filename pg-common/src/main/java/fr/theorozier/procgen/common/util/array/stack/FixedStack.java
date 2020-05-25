package fr.theorozier.procgen.common.util.array.stack;

public abstract class FixedStack {
	
	protected int ptr = 0;
	
	public void clear() {
		this.ptr = 0;
	}
	
	public int getPointer() {
		return this.ptr;
	}
	
	public boolean isEmpty() {
		return this.ptr == 0;
	}
	
	public boolean hasAny() {
		return this.ptr != 0;
	}
	
}
