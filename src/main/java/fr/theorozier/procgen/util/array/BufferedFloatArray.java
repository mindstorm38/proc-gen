package fr.theorozier.procgen.util.array;

public class BufferedFloatArray {
	
	public static final int DEFAULT_GROW = 1024;
	
	private float[] arr;
	private int size;
	private int grow;
	
	public BufferedFloatArray() {
		
		this.arr = new float[DEFAULT_GROW];
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
	
	public float[] result() {
		
		final float[] result = new float[this.size];
		System.arraycopy(this.arr, 0, result, 0, this.size);
		return result;
		
	}
	
	public BufferedFloatArray put(float value) {
		
		if (this.size >= this.arr.length) {
			
			float[] narr = new float[this.arr.length + this.grow];
			System.arraycopy(this.arr, 0, narr, 0, this.arr.length);
			this.arr = narr;
			
		}
		
		this.arr[this.size++] = value;
		
		return this;
		
	}
	
}
