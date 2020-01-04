package fr.theorozier.procgen.common.block.state.property;

import fr.theorozier.procgen.common.block.state.BlockStateProperty;
import fr.theorozier.procgen.common.util.BooleanSet;

import java.util.Collection;

public class BooleanProperty extends BlockStateProperty<Boolean> {
	
	public BooleanProperty(String name) {
		super(name);
	}
	
	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}
	
	@Override
	public Collection<Boolean> getAllowedValues() {
		return BooleanSet.IMMUTABLE;
	}
	
	@Override
	public Boolean getDefaultValue() {
		return false;
	}
	
	@Override
	public String getValueName(Boolean value) {
		return value ? "true" : "false";
	}
	
}
