package fr.theorozier.procgen.common.world.task;

import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.task.section.WorldPrimitiveSection;

public class WorldTask implements PriorityRunnable {
	
	private final WorldServerSection section;
	private final WorldTaskType type;
	private final int priority;
	private final Runnable action;
	
	public WorldTask(WorldServerSection section, WorldTaskType type, int priority, Runnable action) {
		
		this.section = section;
		this.type = type;
		this.priority = priority;
		this.action = action;
		
	}
	
	public WorldServerSection getSection() {
		return this.section;
	}
	
	public boolean hasPrimitiveSection() {
		return this.section instanceof WorldPrimitiveSection;
	}
	
	public WorldPrimitiveSection getPrimitiveSection() {
		return (WorldPrimitiveSection) this.section;
	}
	
	public WorldTaskType getType() {
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
