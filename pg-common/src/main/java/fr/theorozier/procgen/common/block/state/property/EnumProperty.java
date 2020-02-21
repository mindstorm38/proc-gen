package fr.theorozier.procgen.common.block.state.property;

import fr.theorozier.procgen.common.block.state.BlockStateProperty;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;

public class EnumProperty<T extends Enum<T>> extends BlockStateProperty<T> {
	
	private final Class<T> enumClass;
	private final T defaultValue;
	private final EnumSet<T> valuesSet;
	private final String[] names;
	
	public EnumProperty(String name, Class<T> enumClass, T[] values) {
		
		super(name);
		
		Objects.requireNonNull(values);
		
		this.enumClass = enumClass;
		
		this.defaultValue = values[0];
		this.valuesSet = EnumSet.of(values[0]);
		this.names = new String[values.length];
		
		for (int i = 0; i < values.length; ++i) {
			
			if (i > 0)
				this.valuesSet.add(values[i]);
			
			this.names[i] = values[i].name().toLowerCase();
			
		}
		
	}
	
	@Override
	public Class<T> getValueClass() {
		return this.enumClass;
	}
	
	@Override
	public Collection<T> getAllowedValues() {
		return this.valuesSet;
	}
	
	@Override
	public T getDefaultValue() {
		return this.defaultValue;
	}
	
	@Override
	public String getValueName(T value) {
		return this.names[value.ordinal()];
	}
	
	@Override
	public T getValueFromName(String name) {

		name = name.toUpperCase();

		for (T cnst : this.valuesSet) {
			if (name.equals(cnst.name())) {
				return cnst;
			}
		}
		
		return this.defaultValue;
		
	}
	
}
