package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.entity.animation.EntityFrame;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.util.ExplosionCreator;

public class PrimedTNTEntity extends FallingBlockEntity {
	
	private final EntityFrame flashingFrame;
	private int remainTick;
	
	public PrimedTNTEntity(WorldBase world, long uid) {
		
		super(world, uid);
		super.setState(Blocks.TNT.getDefaultState());
		
		this.flashingFrame = new EntityFrame();
		this.remainTick = 60;
		
	}
	
	// BASIC UPDATE //
	
	@Override
	public void update() {
		
		super.update();
		
		this.flashingFrame.setLast();
		this.flashingFrame.addValue(0.6f);
		
		if (--this.remainTick == 0)
			this.explode();
		
	}
	
	// EXPLOSION //
	
	public void explode() {
		
		if (this.isInServer) {
			
			ExplosionCreator.createExplosion(this.serverWorld, (float) this.posX, (float) this.posY, (float) this.posZ, 6);
			this.setDead();
			
		}
		
	}
	
	// PROPERTIES GET & SET //
	
	public EntityFrame getFlashingFrame() {
		return flashingFrame;
	}
	
}
