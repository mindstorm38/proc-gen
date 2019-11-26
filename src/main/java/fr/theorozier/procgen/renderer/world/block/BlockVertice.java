package fr.theorozier.procgen.renderer.world.block;

public class BlockVertice {

	private final float x, y, z;
	
	public BlockVertice(float x, float y, float z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockVertice t = (BlockVertice) o;
		return x == t.x && y == t.y && z == t.z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}
	
}
