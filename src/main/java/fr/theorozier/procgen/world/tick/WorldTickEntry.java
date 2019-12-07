package fr.theorozier.procgen.world.tick;

import fr.theorozier.procgen.world.BlockPosition;

import java.util.Comparator;

public class WorldTickEntry<T> {
	
	private final T target;
	private final BlockPosition position;
	private final long scheduledTime;
	private final TickPriority priority;
	
	WorldTickEntry(T target, BlockPosition position, long scheduledTime, TickPriority priority) {
		
		this.target = target;
		this.position = position;
		this.scheduledTime = scheduledTime;
		this.priority = priority;
		
	}
	
	public T getTarget() {
		return this.target;
	}
	
	public BlockPosition getPosition() {
		return this.position;
	}
	
	public long getScheduledTime() {
		return this.scheduledTime;
	}
	
	public TickPriority getPriority() {
		return this.priority;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WorldTickEntry<?> that = (WorldTickEntry<?>) o;
		return this.position.equals(that.position) && this.target == that.target;
	}
	
	@Override
	public int hashCode() {
		return this.position.hashCode();
	}
	
	public static Comparator<WorldTickEntry<?>> buildComparator() {
		
		return (e1, e2) -> {
			
			// Comparation Value
			int cv = Long.compare(e1.scheduledTime, e2.scheduledTime);
			
			if (cv == 0) {
				return e1.priority.compareTo(e2.priority);
			} else {
				return cv;
			}
			
		};
		
	}

}
