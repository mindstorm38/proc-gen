package fr.theorozier.procgen.common.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.BlockRenderLayer;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import io.sutil.pool.FixedObjectPool;

import java.util.*;
import java.util.function.Consumer;

public class BlockState {

	private final Block owner;
	private final ImmutableMap<BlockStateProperty<?>, ?> properties;
	private final List<AxisAlignedBB> boundingBoxes;
	private Table<BlockStateProperty<?>, ?, BlockState> statesByValues;
	private short uid;
	
	private final String repr;
	
	public BlockState(Block owner, ImmutableMap<BlockStateProperty<?>, ?> properties) {
		
		this.owner = owner;
		this.properties = properties;
		this.boundingBoxes = new ArrayList<>();
		this.uid = 0;
		
		StringBuilder builder = new StringBuilder(owner.getIdentifier());
		
		properties.forEach((prop, val) -> {
			builder.append(',');
			builder.append(prop.getName());
			builder.append(':');
			builder.append(prop.getValueNameSafe(val));
		});
		
		this.repr = builder.toString();
		
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
	
	public short getSaveUid() {
		return this.owner.isUnsavable() ? 0 : this.uid;
	}
	
	public int getPropertiesCount() {
		return this.properties.size();
	}
	
	public ImmutableMap<BlockStateProperty<?>, ?> getProperties() {
		return this.properties;
	}
	
	public void setupBoundingBoxes() {
		
		this.boundingBoxes.clear();
		this.owner.getStateCollision(this, this.boundingBoxes);
		
	}
	
	public void forEachCollidingBoundingBox(int x, int y, int z, AxisAlignedBB other, Consumer<AxisAlignedBB> bbConsumer) {
		
		AxisAlignedBB copy = new AxisAlignedBB();
		for (AxisAlignedBB bb : this.boundingBoxes) {
			
			copy.setPosition(bb);
			copy.move(x, y, z);
			
			if (copy.intersects(other)) {
				
				bbConsumer.accept(copy);
				copy = new AxisAlignedBB();
				
			}
			
		}
		
	}
	
	public boolean isBoundingBoxColliding(int x, int y, int z, AxisAlignedBB other) {
		
		try (FixedObjectPool<AxisAlignedBB>.PoolObject tempHold = AxisAlignedBB.POOL.acquire()) {
			
			AxisAlignedBB temp = tempHold.get();
			
			for (AxisAlignedBB bb : this.boundingBoxes) {
				
				temp.setPosition(bb);
				temp.move(x, y, z);
				
				if (temp.intersects(other)) {
					return true;
				}
				
			}
			
			return false;
			
		}
		
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
		return this.withRaw(property, value);
		
		/*
		T v = this.get(property);
		
		if (v == value)
			return this;
		
		BlockState state = this.statesByValues.get(property, value);
	
		if (state == null)
			throw new IllegalStateException("Block state for " + this.owner.getIdentifier() + " doesn't allow the value " + value + " for property " + property.getName() + ".");
		
		return state;
		*/
		
	}
	
	public BlockState withRaw(BlockStateProperty<?> property, Object value) {
		
		Object v = this.get(property);
		
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
	
	public String repr() {
		return this.repr;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockState that = (BlockState) o;
		return uid == that.uid;
	}
	
	@Override
	public int hashCode() {
		return this.uid;
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
