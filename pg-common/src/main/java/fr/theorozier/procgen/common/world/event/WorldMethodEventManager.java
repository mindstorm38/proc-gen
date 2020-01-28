package fr.theorozier.procgen.common.world.event;

import io.msengine.common.util.event.MethodEventManager;

public class WorldMethodEventManager extends MethodEventManager {
	
	public WorldMethodEventManager() {
		
		super(
				WorldChunkListener.class,
				WorldLoadingListener.class,
				WorldEntityListener.class
		);
		
	}
	
}
