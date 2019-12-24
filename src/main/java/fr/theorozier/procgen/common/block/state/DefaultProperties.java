package fr.theorozier.procgen.common.block.state;

import fr.theorozier.procgen.common.block.state.property.EnumProperty;
import fr.theorozier.procgen.common.world.position.Axis;
import fr.theorozier.procgen.common.world.position.Direction;

public class DefaultProperties {
	
	public static final EnumProperty<Direction> DIRECTION = new EnumProperty<>("direction", Direction.class, Direction.values());
	public static final EnumProperty<Axis>           AXIS = new EnumProperty<>("axis", Axis.class, Axis.values());
	
}
