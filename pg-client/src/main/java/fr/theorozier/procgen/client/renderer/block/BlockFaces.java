package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.position.Direction;

public class BlockFaces {

	private byte data = 0;
	
	public void setFace(Direction dir, boolean b) {
		if (b) this.data |= 1 << dir.ordinal();
		else this.data &= ~(1 << dir.ordinal());
	}
	
	public boolean getFace(Direction dir) {
		return ((this.data >> dir.ordinal()) & 1) == 1;
	}
	
	public boolean isObscured() {
		return this.data == 0;
	}
	
	public boolean isVisible() {
		return this.data != 0;
	}
	
	public boolean isTop() {
		return this.getFace(Direction.TOP);
	}
	
	public boolean isBottom() {
		return this.getFace(Direction.BOTTOM);
	}
	
	public boolean isNorth() {
		return this.getFace(Direction.NORTH);
	}
	
	public boolean isSouth() {
		return this.getFace(Direction.SOUTH);
	}
	
	public boolean isEast() {
		return this.getFace(Direction.EAST);
	}
	
	public boolean isWest() {
		return this.getFace(Direction.WEST);
	}
	
	public void setFaceBlock(BlockState state, Direction otherDirection, BlockState other) {
		this.setFace(otherDirection, state.getBlock().mustRenderFace(state, otherDirection, other));
	}
	
	public byte toByte() {
		return this.data;
	}
	
	public void setData(byte data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return Integer.toBinaryString(this.data);
	}
	
}
