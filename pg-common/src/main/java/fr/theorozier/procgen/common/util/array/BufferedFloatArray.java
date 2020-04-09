package fr.theorozier.procgen.common.util.array;

import java.nio.FloatBuffer;

public class BufferedFloatArray {
	
	public static final int DEFAULT_GROW = 1024;
	
	private float[] arr;
	private int size;
	private int grow;
	
	public BufferedFloatArray() {
		
		this.arr = new float[0];
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
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void checkOverflow() {
	
		int expectedSize = (this.size + this.grow - 1) / this.grow * this.grow;
		
		if (this.arr.length != expectedSize) {
			
			final float[] narr = new float[expectedSize];
			System.arraycopy(this.arr, 0, narr, 0, expectedSize);
			this.arr = narr;
			
		}
		
	}
	
	public float[] result() {
		
		final float[] result = new float[this.size];
		System.arraycopy(this.arr, 0, result, 0, this.size);
		return result;
		
	}
	
	public void resultToBuffer(FloatBuffer buffer) {
		for (int i = 0; i < this.size; ++i) {
			buffer.put(this.arr[i]);
		}
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
