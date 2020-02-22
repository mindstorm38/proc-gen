package fr.theorozier.procgen.common.world.serial.registry;

public class SaveShortRegistry<FROM> extends SaveRegistry<FROM, Short> {
	
	private int count = 0;
	
	public SaveShortRegistry() {
		super((short) 0);
	}
	
	@Override
	public void reset() {
		super.reset();
		this.count = 0;
	}
	
	@Override
	public Short nextUid() {
		
		int  next = ++count;
		
		if (next > 0xFFFF)
			super.throwIllegalNextUid();
		
		return (short) next;
		
	}
	
}
