package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;

public abstract class Entity {

	protected final WorldBase world;
	protected final long uid;
	
	protected float posX;
	protected float posY;
	protected float posZ;
	
	public Entity(WorldBase world, long uid) {
		
		this.world = world;
		this.uid = uid;
		
	}
	
	public long getUid() {
		return this.uid;
	}
	
	public void update() {
	
	}

}
