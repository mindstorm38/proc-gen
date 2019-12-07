package fr.theorozier.procgen.world.tick;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WorldTickList<T> {
	
	private static final int MAX_TICK_COUNT = 65536;
	
	private final World world;
	
	private final HashSet<WorldTickEntry<T>> tickEntriesHashed = new HashSet<>();
	private final TreeSet<WorldTickEntry<T>> tickEntriesTree = new TreeSet<>(WorldTickEntry.buildComparator());
	private final Queue<WorldTickEntry<T>> tickEntriesForNow = new ArrayDeque<>();
	
	private final Predicate<T> filter;
	private final Consumer<WorldTickEntry<T>> ticker;
	
	public WorldTickList(World world, Predicate<T> filter, Consumer<WorldTickEntry<T>> ticker) {
	
		this.world = world;
		this.filter = filter;
		this.ticker = ticker;
		
	}
	
	public void tick() {
	
		int remain = this.tickEntriesTree.size();
	
		if (remain != this.tickEntriesHashed.size())
			throw new IllegalArgumentException("Tick entries not synced.");
		
		if (remain > MAX_TICK_COUNT)
			remain = MAX_TICK_COUNT;
		
		long currentTime = this.world.getTime();
		
		Iterator<WorldTickEntry<T>> iterator = this.tickEntriesTree.iterator();
		WorldTickEntry<T> entry;
		
		while (remain > 0 && iterator.hasNext()) {
		
			entry = iterator.next();
			
			if (entry.getScheduledTime() > currentTime)
				break;
			
			if (this.world.canTickAt(entry.getPosition())) {
				
				iterator.remove();
				this.tickEntriesHashed.remove(entry);
				this.tickEntriesForNow.add(entry);
				
				remain--;
				
			}
			
		}
		
		while ((entry = this.tickEntriesForNow.poll()) != null) {
			this.ticker.accept(entry);
		}
		
	}
	
	public void scheduleTick(T target, BlockPosition pos, int scheduledTimeOffset, TickPriority priority) {
	
		if (this.filter.test(target)) {
			this.addEntry(new WorldTickEntry<T>(target, pos, this.world.getTime() + scheduledTimeOffset, priority));
		}
		
	}
	
	private void addEntry(WorldTickEntry<T> entry) {
		
		if (!this.tickEntriesHashed.contains(entry)) {
			
			this.tickEntriesHashed.add(entry);
			this.tickEntriesTree.add(entry);
			
		}
		
	}
	
}
