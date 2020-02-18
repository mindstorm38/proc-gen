package fr.theorozier.procgen.common.world.load.section;

public enum WorldLoadingType {
	
	GENERATE (true),
	LOADING (true),
	SAVING (false);

	// Just an information boolean, currently only for dev.
	public final boolean usePrimitive;

	WorldLoadingType(boolean usePrimitive) {
		this.usePrimitive = usePrimitive;
	}

}
