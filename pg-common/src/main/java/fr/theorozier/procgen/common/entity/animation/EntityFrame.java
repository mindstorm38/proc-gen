package fr.theorozier.procgen.common.entity.animation;

import io.sutil.math.MathHelper;

public class EntityFrame {

	private static final float PI_TWICE = (float) MathHelper.PI_TWICE;
	private static final float PI_HALF = (float) MathHelper.PI_HALF;
	
	private static final float POS_ZERO_THRESHOLD = 0.01f;
	private static final float NEG_ZERO_THRESHOLD = -0.01f;
	
	private float current;
	private float last;
	
	public void addValue(float value) {
		this.current += value;
		this.checkOverflow();
	}
	
	public void setValue(float value) {
		this.current = value;
		this.checkOverflow();
	}
	
	public void setValueRaw(float value) {
		this.current = value;
	}
	
	public void backToZero(float speed) {
	
		if (this.current != 0f) {
			
			float sine = (float) Math.sin(this.current);
			
			if (NEG_ZERO_THRESHOLD < sine && sine < POS_ZERO_THRESHOLD) {
				
				this.last -= this.current;
				this.current = 0;
				return;
				
			}
			
			float delta = (float) -Math.sin(this.current * 2) * speed;
			
			if (delta == 0 && sine != 0) {
				delta = POS_ZERO_THRESHOLD;
			}
			
			this.current += delta;
			
		}
		
	}
	
	public void checkOverflow() {
		
		if (this.current >= PI_TWICE) {
			
			float toRemove = MathHelper.floorFloatInt(this.current / PI_TWICE) * PI_TWICE;
			
			this.current -= toRemove;
			this.last -= toRemove;
			
		}
		
	}
	
	public float getValue() {
		return this.current;
	}
	
	public void setLast() {
		this.last = this.current;
	}
	
	public float getLerped(float alpha) {
		return MathHelper.interpolate(alpha, this.current, this.last);
	}
	
}
