package fr.theorozier.procgen.block.state;

import fr.theorozier.procgen.block.state.property.EnumProperty;
import fr.theorozier.procgen.world.Axis;
import fr.theorozier.procgen.world.Direction;

public class DefaultProperties {
	
	public static final EnumProperty<Direction> DIRECTION = new EnumProperty<>("direction", Direction.class, Direction.values());
	public static final EnumProperty<Axis>           AXIS = new EnumProperty<>("axis", Axis.class, Axis.values());
	
}
