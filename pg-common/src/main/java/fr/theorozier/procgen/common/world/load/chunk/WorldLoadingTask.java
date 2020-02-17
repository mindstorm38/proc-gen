package fr.theorozier.procgen.common.world.load.chunk;

import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;

public class WorldLoadingTask implements PriorityRunnable {
	
	private final WorldServerSection section;
	private final WorldLoadingType type;
	private final int priority;
	private final Runnable action;
	
	public WorldLoadingTask(WorldServerSection section, WorldLoadingType type, int priority, Runnable action) {
		
		this.section = section;
		this.type = type;
		this.priority = priority;
		this.action = action;
		
	}
	
	public WorldServerSection getSection() {
		return this.section;
	}
	
	public boolean isPrimitiveSection() {
		return this.section.getClass() == WorldPrimitiveSection.class;
	}
	
	public WorldPrimitiveSection getPrimitiveSection() {
		return (WorldPrimitiveSection) this.section;
	}
	
	public WorldLoadingType getType() {
		return this.type;
	}
	
	@Override
	public int getPriority() {
		return this.priority;
	}
	
	@Override
	public void run() {
		this.action.run();
	}
	
}
