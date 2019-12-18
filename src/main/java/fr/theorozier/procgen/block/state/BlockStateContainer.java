package fr.theorozier.procgen.block.state;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.util.MapUtils;
import fr.theorozier.procgen.util.SaveUtils;

import java.util.*;
import java.util.stream.Stream;

public class BlockStateContainer {

	private final Block owner;
	private final ImmutableMap<String, BlockStateProperty<?>> properties;
	private final ImmutableList<BlockState> states;
	
	public BlockStateContainer(Block owner, Map<String, BlockStateProperty<?>> properties) {
		
		this.owner = owner;
		this.properties = ImmutableMap.copyOf(properties);
		
		Stream<List<?>> stream = Stream.of(Collections.emptyList());
		
		Map<ImmutableMap<BlockStateProperty<?>, ?>, BlockState> statesByProperties = Maps.newHashMap();
		List<BlockState> states = Lists.newArrayList();
		
		for (BlockStateProperty<?> property : properties.values()) {
			
			stream = stream.flatMap((lst) ->
				property.getAllowedValues().stream()
						.map(allowedValue -> {
							
							List<Object> newList = Lists.newArrayList(lst);
							newList.add(allowedValue);
							return newList;
							
						})
			);
			
		}
		
		stream.forEach(uniqueGroup -> {
			
			ImmutableMap<BlockStateProperty<?>, ?> propertiesValues = ImmutableMap.copyOf(MapUtils.createMap(properties.values(), uniqueGroup));
			BlockState state = new BlockState(this.owner, propertiesValues);
			statesByProperties.put(propertiesValues, state);
			states.add(state);
			
		});
		
		for (BlockState state : states)
			state.setAllStates(statesByProperties);
		
		this.states = ImmutableList.copyOf(states);
		
	}
	
	public BlockState getBaseState() {
		return this.states.get(0);
	}
	
	public static class Builder {
	
		private final Block owner;
		private final HashMap<String, BlockStateProperty<?>> properties;
		
		public Builder(Block owner) {
			
			this.owner = owner;
			this.properties = new HashMap<>();
			
		}
		
		public <T> void register(BlockStateProperty<T> property) {
			
			String name = SaveUtils.validateSavableName(property.getName(), "state container");
			Collection<T> allowedValues = property.getAllowedValues();
			
			if (allowedValues.isEmpty())
				throw new IllegalArgumentException("Given property '" + name + "' returns empty collection for allowed values.");
			
			for (T allowedValue : allowedValues)
				SaveUtils.validateSavableName(property.getValueName(allowedValue), "property '" + name + "' in state container");
			
			if (this.properties.containsKey(name))
				throw new IllegalArgumentException("Given property is already registered in this state container builder.");
			
			this.properties.put(name, property);
			
		}
		
		public BlockStateContainer build() {
			return new BlockStateContainer(this.owner, this.properties);
		}
	
	}
	
}
