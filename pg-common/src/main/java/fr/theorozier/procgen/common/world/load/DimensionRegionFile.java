package fr.theorozier.procgen.common.world.load;

import com.github.luben.zstd.ZstdOutputStream;
import fr.theorozier.procgen.common.world.position.ImmutableSectionPosition;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static io.msengine.common.util.GameLogger.LOGGER;

public class DimensionRegionFile {
	
	// Metadata and header bytes constants.
	private static final int REGION_METADATA_SECTORS = 1;
	private static final int REGION_METADATA_BYTES   = REGION_METADATA_SECTORS << 12;
	private static final int SECTION_HEADER_BYTES    = 5;
	
	// Section saving version formats.
	private static final byte SECTION_VERSION_ZSTD   = 1;
	private static final byte SECTION_VERSION_LAST   = SECTION_VERSION_ZSTD; // Last stable save format.
	
	// Empty sector constant for fast native writing.
	private static final byte[] EMPTY_SECTOR         = new byte[4096];
	
	// Maximum section sectors count.
	private static final int MAX_SECTORS_COUNT       = 4095;
	
	// CLASS //
	
	private final ImmutableSectionPosition position; // TODO Check if usefull
	private final RandomAccessFile raFile;
	
	private final int[] sectionOffsets = new int[1024];
	private final List<Boolean> freeSectors;
	
	public DimensionRegionFile(SectionPositioned pos, RandomAccessFile raFile) throws IOException {
		
		this.position = pos.immutableSectionPos();
		this.raFile = raFile;
		
		if (raFile.length() < REGION_METADATA_BYTES) {
			
			// Missing space for common region header.
			raFile.seek(raFile.length());
			raFile.write(EMPTY_SECTOR, 0, (int) raFile.length() - REGION_METADATA_BYTES);
			
		} else if ((raFile.length() & 0xFFF) != 0) {
			
			// Region file size is not a multiple of 4096.
			raFile.seek(raFile.length());
			raFile.write(EMPTY_SECTOR, 0, 4096 - ((int) raFile.length() & 0xFFF));
			
		}
		
		// Do not count header sectors.
		int sectorsCount = (int) (raFile.length() >> 12) - REGION_METADATA_SECTORS;
		
		// Initialize all sectors to free state.
		this.freeSectors = new ArrayList<>(sectorsCount);
		for (int i = 0; i < sectorsCount; ++i)
			this.freeSectors.add(true);
		
		// Reset cursor to start.
		raFile.seek(0);
		
		// Iterate over all stored offsets and update free state.
		for (int i = 0, offset, sectoff, sectcnt; i < 1024; ++i) {
			
			offset = raFile.readInt();
			this.sectionOffsets[i] = offset;
			
			sectoff = getSectOffset(offset);
			sectcnt = getSectCount(offset);
			
			// If section has sectors, then set all these sectors to 'not free'.
			if (offset != 0 && (sectoff + sectcnt) <= sectorsCount) {
				for (int j = 0; j < sectcnt; ++j) {
					this.freeSectors.set(sectoff + j, false);
				}
			}
			
		}
		
	}
	
	// WRITES //
	
	public OutputStream getSectionBufferStream(int x, int z, int formatVersion) throws IOException {
		
		if (formatVersion == SECTION_VERSION_ZSTD) {
			return new ZstdOutputStream(new SectionBuffer(x, z, formatVersion));
		}
		
		throw new IllegalArgumentException("Invalid format version '" + formatVersion + "'.");
		
	}
	
	public OutputStream getSectionBufferStream(int x, int z) throws IOException {
		return this.getSectionBufferStream(x, z, SECTION_VERSION_LAST);
	}
	
	private class SectionBuffer extends ByteArrayOutputStream {
		
		private final int x, y, version;
		
		private SectionBuffer(int x, int y, int version) {
			super(2048);
			this.x = x;
			this.y = y;
			this.version = version;
		}
		
		@Override
		public void close() throws IOException {
			super.close();
			DimensionRegionFile.this.writeSectionData(this.x, this.y, this.buf, this.count, this.version);
		}
		
	}
	
	public synchronized void writeSectionData(int x, int z, byte[] data, int length, int version) throws IOException {
		
		if (length < 1)
			throw new IllegalArgumentException("Can't write 0-length section data.");
		
		int offset = this.getSectionOffset(x, z);
		int sectorsOffset = getSectOffset(offset);
		int sectorsCount = getSectCount(offset);
		
		int sectorsCountNeeded = (SECTION_HEADER_BYTES + length - 1) >> 12 + 1;
		
		if (sectorsCountNeeded > MAX_SECTORS_COUNT) {
			
			LOGGER.severe("Can't save section data at " + x + "/" + z + ", take too much sectors (" + sectorsCountNeeded + " > " + MAX_SECTORS_COUNT + ").");
			return;
			
		}
		
		if (sectorsCount != sectorsCountNeeded) {
		
			// Set all previously used sectors to free.
			for (int i = 0; i < sectorsCount; ++i)
				this.freeSectors.set(sectorsOffset + i, true);
		
			// Computing a new free position for new sector.
			sectorsOffset = 0;
			sectorsCount = 0;
			
			for (int i = 0; i < this.freeSectors.size(); ++i) {
				
				if (this.freeSectors.get(i)) {
					
					if (sectorsCount == 0)
						sectorsOffset = i;
					
					if ((++sectorsCount) == sectorsCountNeeded)
						break;
					
				} else if (sectorsCount != 0) {
					
					sectorsCount = 0;
					sectorsOffset = 0;
					
				}
				
			}
			
			// Check if enough free sectors available.
			if (sectorsCount != sectorsCountNeeded) {
			
				if (sectorsCount == 0)
					sectorsOffset = (int) (this.raFile.length() >> 12) - REGION_METADATA_SECTORS;
				
				this.raFile.seek((sectorsOffset + REGION_METADATA_SECTORS) << 12);
				
				int missingSectors = sectorsCountNeeded - sectorsCount;
				
				for (int i = 0; i < missingSectors; ++i)
					this.raFile.write(EMPTY_SECTOR);
				
				sectorsCount = sectorsCountNeeded;
			
			}
			
			// After sectors freed, write new offset.
			this.setSectionOffset(x, z, buildSectionOffset(sectorsOffset, sectorsCount));
			
			// Mark all sectors to 'not free'.
			for (int i = 0; i < sectorsCount; i++)
				this.freeSectors.set(sectorsOffset + i, false);
			
		}
		
		// After all operations to free space for 'length' at 'sectorsOffset'.
		this.writeSectionDataAtSector(sectorsOffset, data, length, version);
		
	}
	
	private void writeSectionDataAtSector(int sectorsOffset, byte[] data, int length, int version) throws IOException {
		
		this.raFile.seek((sectorsOffset + REGION_METADATA_SECTORS) << 12);
		this.raFile.writeInt(length);
		this.raFile.write(version);
		this.raFile.write(data, 0, length);
		
	}
	
	// LOAD UTILS //
	
	public boolean isSectionSaved(int x, int z) {
		return getSectOffset(this.getSectionOffset(x, z)) != 0;
	}
	
	// FORMAT UTILS //
	
	public int getSectionOffset(int x, int z) {
		return this.sectionOffsets[getRegionIndex(x, z)];
	}
	
	public void setSectionOffset(int x, int z, int offset) throws IOException {
		int index = getRegionIndex(x, z);
		this.sectionOffsets[index] = offset;
		this.raFile.seek(index * 4);
		this.raFile.writeInt(offset);
	}
	
	public static int getRegionIndex(int x, int z) {
		return x + z * 32;
	}
	
	public static int getSectOffset(int offset) {
		return offset >> 12;
	}
	
	public static int getSectCount(int offset) {
		return offset & 1023;
	}
	
	public static int buildSectionOffset(int sectOffset, int sectCount) {
		return (sectOffset << 12) | sectCount;
	}
	
}
