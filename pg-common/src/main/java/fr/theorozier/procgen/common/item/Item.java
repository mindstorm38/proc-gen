package fr.theorozier.procgen.common.item;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.LiveEntity;
import fr.theorozier.procgen.common.util.ErrorUtils;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import io.sutil.StringUtils;

public class Item {

	protected final short uid;
	protected final String identifier;
	
	protected byte stackSize;
	
	public Item(int uid, String identifier) {
		
		if (uid <= 0)
			throw ErrorUtils.invalidUidArgument("Item");
		
		this.uid = (short) uid;
		this.identifier = StringUtils.requireNonNullAndEmpty(identifier, "Item's identifier can't be null or empty.");
	
		this.stackSize = 64;
		
	}
	
	// Properties //
	
	public final short getUid() {
		return this.uid;
	}
	
	public final String getIdentifier() {
		return this.identifier;
	}
	
	public byte getStackSize() {
		return this.stackSize;
	}
	
	public void setStackSize(byte stackSize) {
		
		if (stackSize < 1)
			throw new IllegalArgumentException("Invalid size, must be greater than 0.");
		
		this.stackSize = stackSize;
		
	}
	
	// Interacts //
	
	public void useOnBlock(WorldDimension world, LiveEntity entity, ItemStack stack, BlockPositioned pos, BlockState block, Direction face) {
		if (!block.getBlock().interactBlock(world, pos, block, face, entity, stack)) {
			this.useOnBlockNoInteract(world, entity, stack, pos, block, face);
		}
	}
	
	public void useOnBlockNoInteract(WorldDimension world, LiveEntity entity, ItemStack stack, BlockPositioned pos, BlockState block, Direction face) { }
	
	public void useOffBlock(WorldDimension world, LiveEntity entity, ItemStack stack) {}
	
}
