package fr.theorozier.procgen.common.block.state;

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
	
	@SuppressWarnings("unchecked")
	public String getValueNameSafe(Object rawValue) {
		return this.getValueName((T) rawValue);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return this.name.equals(((BlockStateProperty<?>) o).name);
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public String toString() {
		return "BlockStateProperty{" +
				"name='" + name + '\'' +
				'}';
	}
	
}
