package fr.theorozier.procgen.block;

import fr.theorozier.procgen.block.state.BlockStateContainer;
import fr.theorozier.procgen.block.state.DefaultProperties;
import fr.theorozier.procgen.world.Axis;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.common.osf.OSFNumber;

public class BlockLog extends Block {
	
	private static final Axis DEFAULT_AXIS = Axis.Y;
	
	public BlockLog(int uid, String identifier) {
		
		super(uid, identifier);
		
		this.setDefaultState(this.getDefaultState().with(DefaultProperties.AXIS, Axis.Y));
		
	}
	
	@Override
	public void registerStateContainerProperties(BlockStateContainer.Builder builder) {
		builder.register(DefaultProperties.AXIS);
	}
	
	public void setLogAxis(WorldBlock block, Axis axis) {
		block.getMetadata().set("axis", new OSFNumber((byte) axis.ordinal()));
	}
	
	public Axis getLogAxis(WorldBlock block) {
		
		final int id = block.getMetadata().getByte("axis", (byte) -1);
		return id >= 0 && id < Axis.values().length ? Axis.values()[id] : DEFAULT_AXIS;
		
	}
	
	@Override
	public void initBlock(World world, WorldBlock block) {
		this.setLogAxis(block, DEFAULT_AXIS);
	}
	
}
