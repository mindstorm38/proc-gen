package fr.theorozier.procgen.common.util.array;

import java.lang.reflect.Array;

/**
 * A buffered array that grow by a fixed amount of size if
 * current size is too small.<br>
 * Not atomic.
 * @param <T> The type of array.
 */
public class BufferedArray<T> {
	
	public static final int DEFAULT_GROW = 1024;
	
	private final Class<T> clazz;
	
	private T[] arr;
	private int size;
	private int grow;
	
	public BufferedArray(Class<T> clazz) {
		
		this.clazz = clazz;
		
		this.arr = this.newArray(0);
		this.size = 0;
		this.grow = DEFAULT_GROW;
		
	}
	
	@SuppressWarnings("unchecked")
	private T[] newArray(int size) {
		return (T[]) Array.newInstance(clazz, size);
	}
	
	/**
	 * Set a new grow value.
	 * @param grow The new grow value.
	 */
	public void setGrow(int grow) {
		
		if (grow < 2)
			throw new IllegalArgumentException("Invalid grow value, must be greater or equals than 2.");
		
		this.grow = grow;
		
	}
	
	/**
	 * Get the real size of the array.
	 * @return The real size of the internal array.
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Compute and return an array of the real size ({@link #getSize()}) with the real content.
	 * @return The result array.
	 */
	public T[] result() {
		
		final T[] result = this.newArray(this.size);
		System.arraycopy(this.arr, 0, result, 0, this.size);
		return result;
		
	}
	
	/**
	 * Put a new single value in the next array element, if not enough
	 * size, grow the internal array.
	 * @param value The value to append.
	 * @return Self reference.
	 */
	public BufferedArray<T> put(T value) {
		
		if (this.size >= this.arr.length) {
			
			T[] narr = this.newArray(this.arr.length + this.grow);
			System.arraycopy(this.arr, 0, narr, 0, this.arr.length);
			this.arr = narr;
			
		}
		
		this.arr[this.size++] = value;
		
		return this;
		
	}
	
}
