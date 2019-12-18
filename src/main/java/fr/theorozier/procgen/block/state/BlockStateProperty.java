package fr.theorozier.procgen.block.state;

import java.util.Collection;

public abstract class BlockStateProperty<T> {
	
	private final String name;
	
	public BlockStateProperty(String name) {
		this.name = name;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public abstract Class<T> getValueClass();
	
	public abstract Collection<T> getAllowedValues();
	
	public abstract T getDefaultValue();
	
	public abstract String getValueName(T value);
	
	@Override
	public String toString() {
		return "BlockStateProperty{" +
				"name='" + name + '\'' +
				'}';
	}
	
}
