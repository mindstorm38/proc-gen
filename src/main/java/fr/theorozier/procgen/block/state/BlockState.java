package fr.theorozier.procgen.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import fr.theorozier.procgen.block.Block;

import java.util.HashMap;
import java.util.Map;

public class BlockState {

	private final Block owner;
	private final ImmutableMap<BlockStateProperty<?>, ?> properties;
	private Table<BlockStateProperty<?>, ?, BlockState> statesByValues;
	
	public BlockState(Block owner, ImmutableMap<BlockStateProperty<?>, ?> properties) {
		
		this.owner = owner;
		this.properties = properties;
		
	}
	
	public Block getBlock() {
		return this.owner;
	}
	
	public void setAllStates(Map<ImmutableMap<BlockStateProperty<?>, ?>, BlockState> states) {
		
		if (this.statesByValues != null)
			throw new IllegalStateException("States by values are already defined for this block state.");
		
		Table<BlockStateProperty<?>, Object, BlockState> statesByValues = HashBasedTable.create();
		
		HashMap<BlockStateProperty<?>, Object> tempMapForGet = new HashMap<>(this.properties);
		
		this.properties.forEach((prop, value) -> {
			prop.getAllowedValues().forEach(allowedValue -> {
				if (allowedValue != value) {
					
					Object oldValue = tempMapForGet.put(prop, allowedValue);
					statesByValues.put(prop, allowedValue, states.get(tempMapForGet));
					tempMapForGet.put(prop, oldValue);
					
				}
			});
		});
		
		this.statesByValues = statesByValues.isEmpty() ? statesByValues : ArrayTable.create(statesByValues);
		
	}
	
	public boolean has(BlockStateProperty<?> property) {
		return this.properties.containsKey(property);
	}
	
	public <T> T get(BlockStateProperty<T> property) {
		
		Object v = this.properties.get(property);
		
		if (v == null)
			throw new IllegalStateException("Block state for " + this.owner.getIdentifier() + " doesn't define the property " + property.getName() + ".");
		
		return property.getValueClass().cast(v);
		
	}
	
	public <T> BlockState with(BlockStateProperty<T> property, T value) {
	
		T v = this.get(property);
		
		if (v == value)
			return this;
		
		BlockState state = this.statesByValues.get(property, value);
	
		if (state == null)
			throw new IllegalStateException("Block state for " + this.owner.getIdentifier() + " doesn't allow the value " + value + " for property " + property.getName() + ".");
		
		return state;
		
	}
	
}
