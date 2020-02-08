package fr.theorozier.procgen.common.entity.animation;

public class EntityInterpolationRange {

	private float value = 0f;
	
	public void increment(float val) {
		
		if (this.value != 1) {
			
			this.value += val;
			
			if (this.value > 1)
				this.value = 1;
			
		}
		
	}
	
	public void decrement(float val) {
		
		if (this.value != 0) {
			
			this.value -= val;
			
			if (this.value < 0)
				this.value = 0;
			
		}
		
	}
	
	public float getValue() {
		return this.value;
	}

}
