package fr.theorozier.procgen.common.util;

public class Vector2f {
	
	private float x;
	private float y;
	
	public Vector2f(float x, float y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float len() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	public static Vector2f from(float rotation, float length) {
		return new Vector2f((float) Math.cos(rotation) * length, (float) Math.sin(rotation) * length);
	}
	
}
