package fr.theorozier.procgen.common.world.gen.chunk;

import fr.theorozier.procgen.common.util.concurrent.PriorityRunnable;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.sutil.ThreadUtils;

public class WorldPrimitiveSection extends WorldServerSection {
	
	private WorldSectionStatus status;
	
	public WorldPrimitiveSection(WorldServer world, SectionPositioned position) {
		
		super(world, position);
		
		this.status = WorldSectionStatus.EMPTY;
		
	}
	
	public final WorldSectionStatus getStatus() {
		return this.status;
	}
	
	public boolean isZeroZero() {
		return this.getSectionPos().getX() == 0 && this.getSectionPos().getZ() == 0;
	}
	
	public final void gotoNextStatus() {
		
		WorldSectionStatus status = this.status.getNext();
		
		if (this.isZeroZero())
			System.out.println("Section 0,0 status from " + this.status.getIdentifier() + " to " + status.getIdentifier());
		
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
		
		if (this.isZeroZero())
			System.out.println("Section 0,0 try loading task from status " + this.status.getIdentifier() + " to " + next.getIdentifier());
		
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
		
		return new PriorityRunnable() {
			
			@Override
			public int getPriority() {
				return distanceToLoaders;
			}
			
			public void run() {
				
				if (WorldPrimitiveSection.this.isZeroZero()) {
					System.out.println("Running generation for section 0,0 for status " + next.getIdentifier());
				}
				
				next.generate(world.getChunkGenerator(), WorldPrimitiveSection.this);
				ThreadUtils.safesleep(10); // TODO Remove this
				
			}
			
		};
	
	}
	
}
