package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

public class WorldPrimitiveSection extends WorldServerSection {
	
	private WorldSectionStatus status;
	
	public WorldPrimitiveSection(WorldServer world, SectionPositioned position) {
		
		super(world, position);
		
		this.status = WorldSectionStatus.EMPTY;
		
	}
	
	public final WorldSectionStatus getStatus() {
		return this.status;
	}
	
	public final void setStatus(WorldSectionStatus status) {
		this.status = status;
	}
	
	public final void gotoNextStatus() {
		
		WorldSectionStatus status = this.status.getNext();
		
		if (status != null)
			this.status = status;
		
	}
	
	public boolean isFinished() {
		return this.status == WorldSectionStatus.FINISHED;
	}
	
	public PriorityRunnable getNextStatusLoadingTask(WorldServer world, int distanceToLoaders) {
	
		WorldSectionStatus next = this.status.getNext();
		
		if (next == null)
			return null;
		
		if (next.doRequireSameAround()) {
			
			int maxX = this.getSectionPos().getX() + 1;
			int maxZ = this.getSectionPos().getZ() + 1;
			WorldPrimitiveSection other;
			
			for (int x = this.getSectionPos().getX() - 1; x <= maxX; ++x) {
				for (int z = this.getSectionPos().getZ() - 1; z <= maxZ; ++z) {
					other = world.getPrimitiveSectionAt(x, z);
					if (other == null || !other.getStatus().isAsLeastAt(this.status)) {
						return null;
					}
				}
			}
			
		}
		
		return new PriorityRunnable() {
			
			@Override
			public int getPriority() {
				return distanceToLoaders;
			}
			
			public void run() {
				next.generate(world.getChunkGenerator(), WorldPrimitiveSection.this);
			}
			
		};
	
	}
	
}
