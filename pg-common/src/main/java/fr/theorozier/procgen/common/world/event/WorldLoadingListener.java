package fr.theorozier.procgen.common.world.event;

import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.chunk.WorldSection;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;

public interface WorldLoadingListener {
	
	void worldSectionLoaded(WorldBase world, WorldSection section);
	void worldSectionUnloaded(WorldBase world, ImmutableSectionPosition position);
	
}
