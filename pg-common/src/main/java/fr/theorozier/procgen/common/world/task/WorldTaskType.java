package fr.theorozier.procgen.common.world.task;

public enum WorldTaskType {
	
	GENERATE (true),
	LOADING (true),
	SAVING (false);

	// Just an information boolean, currently only for dev.
	public final boolean usePrimitive;

	WorldTaskType(boolean usePrimitive) {
		this.usePrimitive = usePrimitive;
	}

}
