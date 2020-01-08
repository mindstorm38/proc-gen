package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;

public abstract class WalkingEntity extends LiveEntity {
	
	public WalkingEntity(WorldBase world, long uid) {
		super(world, uid);
	}
	
	@Override
	public void update() {
		
		super.update();
		
	}
	
}
