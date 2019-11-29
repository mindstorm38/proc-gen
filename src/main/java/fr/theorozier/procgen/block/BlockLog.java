package fr.theorozier.procgen.block;

import fr.theorozier.procgen.world.Axis;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.common.osf.OSFNumber;

public class BlockLog extends Block {
	
	private static final Axis DEFAULT_AXIS = Axis.Y;
	
	public BlockLog(int uid, String identifier) {
		super(uid, identifier);
	}
	
	public void setLogAxis(WorldBlock block, Axis axis) {
		block.getMetadata().set("axis", new OSFNumber((byte) axis.ordinal()));
	}
	
	public Axis getLogAxis(WorldBlock block) {
		
		final int id = block.getMetadata().getByte("axis", (byte) -1);
		return id >= 0 && id < Axis.values().length ? Axis.values()[id] : DEFAULT_AXIS;
		
	}
	
	@Override
	public void initBlock(WorldBlock block) {
		this.setLogAxis(block, DEFAULT_AXIS);
	}
	
}
