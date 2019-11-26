package fr.theorozier.procgen.util.array;

public class BufferedIntArray {
	
	public static final int DEFAULT_GROW = 1024;
	
	private int[] arr;
	private int size;
	private int grow;
	
	public BufferedIntArray() {
		
		this.arr = new int[DEFAULT_GROW];
		this.size = 0;
		this.grow = DEFAULT_GROW;
		
	}
	
	public void setGrow(int grow) {
		
		if (grow < 2)
			throw new IllegalArgumentException("Invalid grow value, must be greater or equals than 2.");
		
		this.grow = grow;
		
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int[] result() {
		
		final int[] result = new int[this.size];
		System.arraycopy(this.arr, 0, result, 0, this.size);
		return result;
		
	}
	
	public BufferedIntArray put(int value) {
		
		if (this.size >= this.arr.length) {
			
			int[] narr = new int[this.arr.length + this.grow];
			System.arraycopy(this.arr, 0, narr, 0, this.arr.length);
			this.arr = narr;
			
		}
		
		this.arr[this.size++] = value;
		
		return this;
		
	}
	
}
