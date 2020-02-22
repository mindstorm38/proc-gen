package fr.theorozier.procgen.common.util.array.supplier;

public class ArrayShortSupplier implements ArraySupplier<Short> {
	
	private final short[] arr;
	
	public ArrayShortSupplier(short[] arr) {
		this.arr = arr;
	}
	
	@Override
	public int length() {
		return this.arr.length;
	}
	
	@Override
	public Short get(int index) {
		return this.arr[index];
	}
	
	@Override
	public void set(int index, Short val) {
		this.arr[index] = val;
	}
	
}
