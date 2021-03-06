package fr.theorozier.procgen.common.world.tick;

import fr.theorozier.procgen.common.world.position.ImmutableBlockPosition;

import java.util.Comparator;

public class WorldTickEntry<T> {
	
	private static long currentUid = 0;
	
	private final T target;
	private final ImmutableBlockPosition position;
	private final long scheduledTime;
	private final TickPriority priority;
	private final long uid;
	
	WorldTickEntry(T target, ImmutableBlockPosition position, long scheduledTime, TickPriority priority) {
		
		this.target = target;
		this.position = position;
		this.scheduledTime = scheduledTime;
		this.priority = priority;
		this.uid = currentUid++;
		
	}
	
	public T getTarget() {
		return this.target;
	}
	
	public ImmutableBlockPosition getPosition() {
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
				
				cv = e1.priority.compareTo(e2.priority);
				return cv == 0 ? Long.compare(e1.uid, e2.uid) : cv;
				
			} else {
				return cv;
			}
			
		};
		
	}

}
