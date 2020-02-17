package fr.theorozier.procgen.common.world.load.chunk;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.sutil.ThreadUtils;

import java.util.Objects;

public class WorldPrimitiveSection extends WorldServerSection {
	
	private WorldSectionStatus status;
	
	public WorldPrimitiveSection(WorldDimension dimension, SectionPositioned position) {
		
		super(dimension, position);
		
		this.status = WorldSectionStatus.EMPTY;
		
	}
	
	public final WorldSectionStatus getStatus() {
		return this.status;
	}
	
	public final void setStatus(WorldSectionStatus status) {
		this.status = Objects.requireNonNull(status);
	}
	
	public final void gotoNextStatus() {
		
		WorldSectionStatus status = this.status.getNext();
		
		if (status != null)
			this.status = status;
		
	}
	
	public boolean isFinished() {
		return this.status.isLast();
	}
	
	public WorldLoadingTask getNextStatusGenerateTask(WorldDimension world, int distanceToLoaders) {
	
		WorldSectionStatus next = this.status.getNext();
		
		if (next == null)
			return null;
		
		if (next.doRequireSameAround()) {
			
			int maxX = this.getSectionPos().getX() + 1;
			int maxZ = this.getSectionPos().getZ() + 1;
			WorldPrimitiveSection other;
			
			for (int x = this.getSectionPos().getX() - 1; x <= maxX; ++x) {
				for (int z = this.getSectionPos().getZ() - 1; z <= maxZ; ++z) {
					if (!world.isSectionLoadedAt(x, z)) {
						other = world.getPrimitiveSectionAt(x, z);
						if (other == null || !other.getStatus().isAsLeastAt(this.status)) {
							return null;
						}
					}
				}
			}
			
		}
		
		return new WorldLoadingTask(this, WorldLoadingType.GENERATE, distanceToLoaders, () -> {
			
			next.generate(world.getLoader().getGenerator(), WorldPrimitiveSection.this);
			ThreadUtils.safesleep(10); // TODO Remove this
			
		});
	
	}
	
	public WorldLoadingTask getLoadingTask() {
		return null;
	}
	
}
