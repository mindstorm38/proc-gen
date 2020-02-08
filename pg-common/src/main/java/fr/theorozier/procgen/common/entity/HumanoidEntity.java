package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.entity.animation.EntityFrame;
import fr.theorozier.procgen.common.world.WorldBase;

public class HumanoidEntity extends WalkingEntity {
	
	protected final EntityFrame armsFrame;
	
	public HumanoidEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.armsFrame = new EntityFrame();
		
	}
	
	// BASIC UPDATES //
	
	@Override
	protected void updateMotion() {
		
		super.updateMotion();
		
		this.armsFrame.setLast();
		this.armsFrame.addValue(0.1f);
		
	}
	
	// PROPERTIES GET & SET //
	
	public EntityFrame getArmsFrame() {
		return this.armsFrame;
	}
	
}
