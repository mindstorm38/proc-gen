package fr.theorozier.procgen.common.world.chunk.primitive;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.load.DimensionLoader;
import fr.theorozier.procgen.common.world.load.DimensionRegionFile;
import fr.theorozier.procgen.common.world.serial.WorldSectionSerializer;
import fr.theorozier.procgen.common.world.task.WorldTask;
import fr.theorozier.procgen.common.world.task.WorldTaskType;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	/**
	 * Goto the next section status, and return true if new status is the last.
	 * @return True if new status is last (using {@link WorldSectionStatus#isLast()}).
	 */
	public final boolean gotoNextStatus() {
		
		WorldSectionStatus status = this.status.getNext();
		
		if (status != null)
			this.status = status;
		
		return this.status.isLast();
		
	}
	
	public final boolean isFinished() {
		return this.status.isLast();
	}
	
	public WorldTask getNextStatusGenerateTask(DimensionLoader loader, int distanceToLoaders) {
	
		WorldSectionStatus next = this.status.getNext();
		
		if (next == null)
			return null;
		
		if (next.doRequireSameAround()) {
			
			int maxX = this.getSectionPos().getX() + 1;
			int maxZ = this.getSectionPos().getZ() + 1;
			WorldPrimitiveSection other;
			
			for (int x = this.getSectionPos().getX() - 1; x <= maxX; ++x) {
				for (int z = this.getSectionPos().getZ() - 1; z <= maxZ; ++z) {
					if (!loader.getDimension().isSectionLoadedAt(x, z)) {
						other = loader.getPrimitiveSection(x, z);
						if (other == null || !other.getStatus().isAsLeastAt(this.status)) {
							return null;
						}
					}
				}
			}
			
		}
		
		return new WorldTask(this, WorldTaskType.GENERATE, distanceToLoaders, () -> {
			
			next.generate(loader.getGenerator(), loader.getVirtualWorld(), WorldPrimitiveSection.this);
			// ThreadUtils.safesleep(20); // TODO Remove this
			
		});
	
	}
	
	/**
	 * Build a loading task for this primitive section.
	 * @param loader Dimension loader used to get region files.
	 * @param distanceToLoaders Minimum distance to loaders.
	 * @return The loading task, or Null if failed
	 */
	public WorldTask getLoadingTask(DimensionLoader loader, int distanceToLoaders) {

		ImmutableSectionPosition pos = this.getSectionPos();
		DimensionRegionFile file = loader.getSectionRegionFile(pos, false);

		if (file != null) {
			
			return new WorldTask(this, WorldTaskType.LOADING, distanceToLoaders, () -> {
				
				try {
					
					InputStream raw = file.getSectionInputStream(pos.getX() & 31, pos.getZ() & 31);
					DataInputStream in = new DataInputStream(raw);
					WorldSectionSerializer.TEMP_INSTANCE.deserialize(this, in);
					in.close();
					
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
			});
			
		} else {
			return null;
		}

	}
	
}
