package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.entity.animation.EntityFrame;
import fr.theorozier.procgen.common.world.WorldBase;

public class HumanEntity extends WalkingEntity {
	
	protected final EntityFrame armsFrame;
	
	public HumanEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.armsFrame = new EntityFrame();
		
	}
	
	// BASIC UPDATES //
	
	@Override
	protected void updateMotion() {
		
		super.updateMotion();
		
		this.armsFrame.setLast();
		this.armsFrame.addValue(0.6f);
		
	}
	
	// PROPERTIES GET & SET //
	
	public EntityFrame getArmsFrame() {
		return this.armsFrame;
	}
	
}
