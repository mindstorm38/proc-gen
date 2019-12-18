package fr.theorozier.procgen.world.util;

public enum Axis {

	X, Y, Z;
	
	public final Direction[] directions = new Direction[2];
	
	Axis() {}
	
	static {
		
		X.directions[0] = Direction.NORTH;
		X.directions[1] = Direction.SOUTH;
		
		Y.directions[0] = Direction.TOP;
		Y.directions[1] = Direction.BOTTOM;
		
		Z.directions[0] = Direction.EAST;
		Z.directions[1] = Direction.WEST;
		
	}

}
