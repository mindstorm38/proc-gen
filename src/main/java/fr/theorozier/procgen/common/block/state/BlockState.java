package fr.theorozier.procgen.common.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BlockState {

	private final Block owner;
	private final ImmutableMap<BlockStateProperty<?>, ?> properties;
	private final List<AxisAlignedBB> boundingBoxes;
	private Table<BlockStateProperty<?>, ?, BlockState> statesByValues;
	private short uid;
	
	public BlockState(Block owner, ImmutableMap<BlockStateProperty<?>, ?> properties) {
		
		this.owner = owner;
		this.properties = properties;
		this.boundingBoxes = new ArrayList<>();
		this.uid = 0;
		
	}
	
	public Block getBlock() {
		return this.owner;
	}
	
	public boolean isBlock(Block block) {
		return this.owner == block;
	}
	
	public short getUid() {
		return this.uid;
	}
	
	public void setUid(short uid) {
		this.uid = uid;
	}
	
	public void setupBoundingBoxes() {
		
		this.boundingBoxes.clear();
		this.owner.getStateCollision(this, this.boundingBoxes);
		
	}
	
	public void forEachBoundingBox(Consumer<AxisAlignedBB> bbConsumer) {
		
		for (int i = 0, size = this.boundingBoxes.size(); i < size; ++i)
			bbConsumer.accept(this.boundingBoxes.get(i));
		
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
	
	public boolean isInRenderLayer(BlockRenderLayer layer) {
		return this.owner.getRenderLayer() == layer;
	}
	
	public boolean isBlockOpaque() {
		return this.owner.isOpaque();
	}
	
	@Override
	public String toString() {
		return "BlockState{" +
				"block=" + owner.getIdentifier() +
				", properties=" + properties +
				", uid=" + uid +
				'}';
	}
	
}
