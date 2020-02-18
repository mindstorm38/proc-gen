package fr.theorozier.procgen.common.world.task.section;

import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.task.DimensionLoader;
import fr.theorozier.procgen.common.world.task.DimensionRegionFile;
import fr.theorozier.procgen.common.world.task.WorldTask;
import fr.theorozier.procgen.common.world.task.WorldTaskType;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;
import io.sutil.ThreadUtils;

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
	
	public final void gotoNextStatus() {
		
		WorldSectionStatus status = this.status.getNext();
		
		if (status != null)
			this.status = status;
		
	}
	
	public boolean isFinished() {
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
						other = loader.getDimension().getPrimitiveSectionAt(x, z);
						if (other == null || !other.getStatus().isAsLeastAt(this.status)) {
							return null;
						}
					}
				}
			}
			
		}
		
		return new WorldTask(this, WorldTaskType.GENERATE, distanceToLoaders, () -> {
			
			next.generate(loader.getGenerator(), WorldPrimitiveSection.this);
			ThreadUtils.safesleep(10); // TODO Remove this
			
		});
	
	}
	
	public WorldTask getLoadingTask(DimensionLoader loader, int distanceToLoaders) {

		ImmutableSectionPosition pos = this.getSectionPos();

		return new WorldTask(this, WorldTaskType.LOADING, distanceToLoaders, () -> {

			DimensionRegionFile file = loader.getSectionRegionFile(pos, false);

			if (file == null)
				return;

			try {

				InputStream stream = file.getSectionInputStream(pos.getX() & 31, pos.getZ() & 31);



			} catch (IOException ignored) {}

		});

	}
	
}
