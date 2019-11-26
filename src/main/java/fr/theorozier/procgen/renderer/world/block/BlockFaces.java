package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.block.Block;

import java.util.Arrays;

public class BlockFaces {

	// Top     Y+1
	// Bottom  Y-1
	// North   X+1
	// South   X-1
	// East    Z+1
	// West    Z-1
	private boolean top, bottom, north, south, east, west;
	
	public boolean isObscured() {
		return !this.top && !this.bottom && !this.north && !this.south && !this.east && !this.west;
	}
	
	public boolean isVisible() {
		return this.top || this.bottom || this.north || this.south || this.east || this.west;
	}
	
	public boolean isTop() {
		return top;
	}
	
	public void setTop(boolean top) {
		this.top = top;
	}
	
	public boolean isBottom() {
		return bottom;
	}
	
	public void setBottom(boolean bottom) {
		this.bottom = bottom;
	}
	
	public boolean isNorth() {
		return north;
	}
	
	public void setNorth(boolean north) {
		this.north = north;
	}
	
	public boolean isSouth() {
		return south;
	}
	
	public void setSouth(boolean south) {
		this.south = south;
	}
	
	public boolean isEast() {
		return east;
	}
	
	public void setEast(boolean east) {
		this.east = east;
	}
	
	public boolean isWest() {
		return west;
	}
	
	public void setWest(boolean west) {
		this.west = west;
	}
	
	public void topBlock(Block block) {
		this.top = !block.isOpaque();
	}
	
	public void bottomBlock(Block block) {
		this.bottom = !block.isOpaque();
	}
	
	public void northBlock(Block block) {
		this.north = !block.isOpaque();
	}
	
	public void southBlock(Block block) {
		this.south = !block.isOpaque();
	}
	
	public void eastBlock(Block block) {
		this.east = !block.isOpaque();
	}
	
	public void westBlock(Block block) {
		this.west = !block.isOpaque();
	}
	
	@Override
	public String toString() {
		return Arrays.toString(new boolean[]{this.top, this.bottom, this.north, this.south, this.east, this.west});
	}
	
}
