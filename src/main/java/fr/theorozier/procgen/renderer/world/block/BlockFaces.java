package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.world.Direction;

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
	
	public void topBlock(Block block) {
		this.setFace(Direction.TOP, !block.isOpaque());
	}
	
	public void bottomBlock(Block block) {
		this.setFace(Direction.BOTTOM, !block.isOpaque());
	}
	
	public void northBlock(Block block) {
		this.setFace(Direction.NORTH, !block.isOpaque());
	}
	
	public void southBlock(Block block) {
		this.setFace(Direction.SOUTH, !block.isOpaque());
	}
	
	public void eastBlock(Block block) {
		this.setFace(Direction.EAST, !block.isOpaque());
	}
	
	public void westBlock(Block block) {
		this.setFace(Direction.WEST, !block.isOpaque());
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
