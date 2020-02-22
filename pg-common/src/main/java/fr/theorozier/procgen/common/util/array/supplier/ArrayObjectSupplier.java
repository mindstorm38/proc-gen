package fr.theorozier.procgen.common.util.array.supplier;

public class ArrayObjectSupplier<T> implements ArraySupplier<T> {
	
	private final T[] arr;
	
	public ArrayObjectSupplier(T[] arr) {
		this.arr = arr;
	}
	
	@Override
	public int length() {
		return this.arr.length;
	}
	
	@Override
	public T get(int index) {
		return this.arr[index];
	}
	
	@Override
	public void set(int index, T val) {
		this.arr[index] = val;
	}
	
}
