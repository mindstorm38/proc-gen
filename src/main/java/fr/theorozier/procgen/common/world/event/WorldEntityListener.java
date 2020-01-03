package fr.theorozier.procgen.common.world.event;

import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.world.WorldBase;

public interface WorldEntityListener {

	void worldEntityAdded(WorldBase world, Entity entity);
	void worldEntityRemoved(WorldBase world, Entity entity);

}
