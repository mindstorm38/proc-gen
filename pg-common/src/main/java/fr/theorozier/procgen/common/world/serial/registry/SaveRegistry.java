package fr.theorozier.procgen.common.world.serial.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class SaveRegistry<FROM, TO extends Number> {

	private final Map<FROM, TO> mapping = new HashMap<>();
	private final TO none;
	
	public SaveRegistry(TO none) {
		this.none = none;
	}
	
	public TO getMapping(FROM from) {
		return from == null ? this.none : this.mapping.computeIfAbsent(from, fr -> this.nextUid());
	}
	
	public void reset() {
		this.mapping.clear();
	}
	
	public abstract TO nextUid();
	
	public int size() {
		return this.mapping.size();
	}
	
	public Map<FROM, TO> getMappings() {
		return this.mapping;
	}
	
	public Set<Map.Entry<FROM, TO>> getMappingsEntries() {
		return this.mapping.entrySet();
	}
	
	public void forEach(BiConsumer<FROM, TO> consumer) {
		this.mapping.forEach(consumer);
	}
	
	protected void throwIllegalNextUid() throws IllegalStateException {
		throw new IllegalStateException("Can't get next UID on this " + this.getClass().getSimpleName() + ", no more UID available.");
	}
	
}
