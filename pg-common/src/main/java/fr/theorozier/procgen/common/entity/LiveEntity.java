package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;
import io.sutil.math.MathHelper;

public abstract class LiveEntity extends MotionEntity {
	
	protected int health;
	protected float fallResistance;
	
	public LiveEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.health = 1;
		this.fallResistance = 3f;
		
	}
	
	@Override
	public void update() {
		
		super.update();
		
		this.updateHealth();
		
	}
	
	public void updateHealth() {
		
		if (this.health <= 0)
			this.setDead();
		
	}
	
	public void damage(DamageSource source, float damage) {
		this.health -= MathHelper.floorFloatInt(damage);
	}
	
	@Override
	public void fallen(double distance) {
		
		float damage = (float) (distance - this.fallResistance);
		
		if (damage > 0) {
			this.damage(DamageSource.FALL, damage / this.fallResistance);
		}
		
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
}
