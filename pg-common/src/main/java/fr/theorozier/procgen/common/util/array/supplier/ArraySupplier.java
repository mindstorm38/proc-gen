package fr.theorozier.procgen.common.util.array.supplier;

public interface ArraySupplier<T> {

	int length();
	T get(int index);
	void set(int index, T val);

	static <T> ArraySupplier<T> from(T[] arr) {
		return new ArrayObjectSupplier<>(arr);
	}
	
	static ArraySupplier<Short> from(short[] arr) {
		return new ArrayShortSupplier(arr);
	}
	
}
