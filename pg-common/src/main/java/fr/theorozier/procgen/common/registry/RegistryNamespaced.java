package fr.theorozier.procgen.common.registry;

public class RegistryNamespaced<T> implements Registry<T> {
	
	private final Registry<T> delegate;
	private final String namespace;
	
	public RegistryNamespaced(Registry<T> delegate, String namespace) {
		this.delegate = delegate;
		this.namespace = namespace;
	}
	
	private String buildKey(String key) {
		return this.namespace + ":" + key;
	}
	
	@Override
	public void register(String key, T value) {
		this.delegate.register(this.buildKey(key), value);
	}
	
	@Override
	public void unregister(String key) {
		this.delegate.unregister(this.buildKey(key));
	}
	
	@Override
	public boolean contains(String key) {
		return this.delegate.contains(this.buildKey(key));
	}
	
	@Override
	public T get(String key) {
		return this.delegate.get(this.buildKey(key));
	}
	
}