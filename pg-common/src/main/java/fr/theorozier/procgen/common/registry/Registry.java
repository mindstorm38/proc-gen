package fr.theorozier.procgen.common.registry;

public interface Registry<T> {
	
	void register(String key, T value);
	void unregister(String key);
	boolean contains(String key);
	T get(String key);
	
}
