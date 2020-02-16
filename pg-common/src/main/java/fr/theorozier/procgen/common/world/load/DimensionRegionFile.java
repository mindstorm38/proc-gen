package fr.theorozier.procgen.common.world.load;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * Represent a region file, and all its format.
 * This class manager region file sectors of 4Ko.
 *
 * @author Theo Rozier
 *
 */
public class DimensionRegionFile {
	
	// Metadata and header bytes constants.
	private static final int REGION_METADATA_SECTORS = 1;
	private static final int REGION_METADATA_BYTES   = REGION_METADATA_SECTORS << 12;
	private static final int SECTION_HEADER_BYTES    = 5;
	
	// Section saving version formats.
	private static final byte SECTION_VERSION_RAW    = 0;
	private static final byte SECTION_VERSION_ZSTD   = 1;
	private static final byte SECTION_VERSION_LAST   = SECTION_VERSION_ZSTD; // Last stable save format.

	// Empty sector constant for fast native writing.
	private static final byte[] EMPTY_SECTOR         = new byte[4096];
	
	// Maximum section sectors count.
	private static final int MAX_SECTORS_COUNT       = 2047;
	
	// CLASS //
	
	private final RandomAccessFile raFile;
	
	private final int[] sectionOffsets = new int[1024];
	private final List<Boolean> freeSectors;
	
	public DimensionRegionFile(RandomAccessFile raFile) throws IOException {
		
		this.raFile = raFile;
		
		if (raFile.length() < REGION_METADATA_BYTES) {
			
			// Missing space for common region header.
			raFile.seek(0);
			for (int i = 0; i < REGION_METADATA_SECTORS; ++i)
				raFile.write(EMPTY_SECTOR);
			
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

	/**
	 * Get a section output stream to write in the specified format version.<br>
	 * Formats constants :<br>
	 * <ul>
	 *     <li><b>SECTION_VERSION_RAW</b> : Section not compressed.</li>
	 *     <li><b>SECTION_VERSION_ZSTD</b> : Section Zstd compressed V1</li>
	 *     <li><b>SECTION_VERSION_LAST</b> : Equals to last stable version : <b>SECTION_VERSION_ZSTD</b></li>
	 * </ul>
	 * @param x The relative X section coordinate in this region.
	 * @param z The relative Z section coordinate in this region.
	 * @param formatVersion The format version used to encode the section to sectors data.
	 *                         If version is wrong, then raw (uncompressed) data stream is returned and <b>SECTION_VERSION_RAW</b> is used.
	 * @return The section output stream, implementing {@link SectionOutputStream} used to write the section to this region sectors.
	 * @throws IOException If creation of output streams fails.
	 */
	@SuppressWarnings("unchecked")
	public <R extends OutputStream & SectionOutputStream> R getSectionOutputStream(int x, int z, int formatVersion) throws IOException {

		switch (formatVersion) {
			case SECTION_VERSION_ZSTD:
				return (R) new SectionZstdOutputStream(x, z);
			default:
				return (R) new SectionBuffer(x, z, SECTION_VERSION_RAW);
		}
		
	}

	/**
	 * Get a section output stream to write in the last stable format version.
	 * @param x The relative X section coordinate in this region.
	 * @param z The relative Z section coordinate in this region.
	 * @return The section output stream, implementing {@link SectionOutputStream} used to write the section to this region sectors.
	 * @throws IOException If the format version is invalid, or creation of output streams fails.
	 * @see #getSectionOutputStream(int, int, int)
	 */
	public OutputStream getSectionOutputStream(int x, int z) throws IOException {
		return this.getSectionOutputStream(x, z, SECTION_VERSION_LAST);
	}

	/**
	 * Internal class to implement {@link SectionOutputStream}.
	 */
	private class SectionZstdOutputStream extends ZstdOutputStream implements SectionOutputStream {

		public SectionZstdOutputStream(int x, int y) throws IOException {
			super(new SectionBuffer(x, y, SECTION_VERSION_ZSTD));
		}

		@Override
		public void writeSectionData() throws IOException {
			((SectionBuffer) this.out).writeSectionData();
		}

		@Override
		public void writeSectionDataAndClose() throws IOException {
			this.writeSectionData();
			this.close();
		}

	}

	/**
	 * Low level byte array stream used to write section data in the region sectors.
	 */
	private class SectionBuffer extends ByteArrayOutputStream implements SectionOutputStream {
		
		private final int x, y, version;
		
		private SectionBuffer(int x, int y, int version) {
			super(2048);
			this.x = x;
			this.y = y;
			this.version = version;
		}

		@Override
		public void writeSectionData() throws IOException {
			DimensionRegionFile.this.writeSectionData(this.x, this.y, this.buf, this.count, this.version);
		}

		@Override
		public void writeSectionDataAndClose() throws IOException {
			this.writeSectionData();
			this.close();
		}

	}

	/**
	 * Common interface providing write section data methods.
	 */
	public interface SectionOutputStream {

		void writeSectionData() throws IOException;
		void writeSectionDataAndClose() throws IOException;

	}

	/**
	 * Raw write method for a specified section.
	 * @param x The relative X section coordinate in this region.
	 * @param z The relative Z section coordinate in this region.
	 * @param data Byte array to write.
	 * @param length Byte array length to effectively write.
	 * @param version The format to write in the section header, not used more than that in this method, passed data must be already encoded as wanted.
	 *                   This version will be used when reading to return the appropriate input stream for decoding.
	 * @throws IOException If write errors occurs on the random access file.
	 * @see #getSectionOutputStream(int, int, int)
	 * @see #getSectionOutputStream(int, int)
	 */
	public synchronized void writeSectionData(int x, int z, byte[] data, int length, int version) throws IOException {
		
		if (length < 1)
			throw new IllegalArgumentException("Can't write 0-length section data.");
		
		int offset = this.getSectionOffset(x, z);
		int sectorsOffset = getSectOffset(offset);
		int sectorsCount = getSectCount(offset);
		
		int sectorsCountNeeded = (SECTION_HEADER_BYTES + length - 1) >> 12 + 1;
		
		if (sectorsCountNeeded > MAX_SECTORS_COUNT) {

			// Remember that unlike sectors count, offsets don't need to be checked because if all sections
			// has the maximum number of sectors (MAX_SECTORS_COUNT), then offset would still be able to encode
			// the last section section offset.

			LOGGER.severe("Can't save section data at " + x + "/" + z + ", take too much sectors (" + sectorsCountNeeded + " > " + MAX_SECTORS_COUNT + ").");
			return;
			
		}
		
		if (sectorsCount != sectorsCountNeeded) {
		
			// Set all previously used sectors to free.
			for (int i = 0; i < sectorsCount; ++i)
				this.freeSectors.set(sectorsOffset + i, true);
		
			// Computing a new free position for new sectors.
			sectorsCount = 0;

			for (int i = 0; i < this.freeSectors.size(); ++i) {
				
				if (this.freeSectors.get(i)) {
					
					if (sectorsCount == 0)
						sectorsOffset = i;
					
					if ((++sectorsCount) == sectorsCountNeeded)
						break;
					
				} else if (sectorsCount != 0) {
					sectorsCount = 0;
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

	/**
	 * Internal raw method to write section at specified sector.
	 * @param sectorsOffset Sector offset, sector 0 begin right after the region metadata.
	 * @param data Byte array to write.
	 * @param length Byte array length to take from data.
	 * @param version Version format to write.
	 * @throws IOException If write errors occurs.
	 */
	private void writeSectionDataAtSector(int sectorsOffset, byte[] data, int length, int version) throws IOException {
		
		this.raFile.seek((sectorsOffset + REGION_METADATA_SECTORS) << 12);
		this.raFile.writeInt(length);
		this.raFile.write(version);
		this.raFile.write(data, 0, length);
		
	}

	// READ //

	/**
	 * Retreive a section {@link InputStream}, used to read the data of a section from sectors.
	 * @param x The relative X section coordinate in this region.
	 * @param z The relative Z section coordinate in this region.
	 * @return The section {@link InputStream}, or <b>Null</b>
	 * @throws IOException If read errors occurs.
	 */
	private InputStream getSectionInputStream(int x, int z) throws IOException {

		int offset = this.getSectionOffset(x, z);
		int sectorsCount = getSectCount(offset);

		// If the section has no sectors, then return null.
		if (sectorsCount == 0)
			return null;

		int sectorsOffset = getSectOffset(offset);
		int sectorsByteOffset = (sectorsOffset + REGION_METADATA_SECTORS) << 12;

		// If the sectors byte offset is too large to the file length.
		if (sectorsByteOffset >= this.raFile.length())
			return null;

		// Go to the sector position in the file.
		this.raFile.seek((sectorsOffset + REGION_METADATA_SECTORS) << 12);

		// Get section length in bytes
		int dataLength = this.raFile.readInt();

		// If data length is too much for the file length, return null.
		if ((sectorsByteOffset + dataLength) > this.raFile.length())
			return null;

		// Get section data format version.
		int dataVersion = this.raFile.read();

		// Read the data of specified length.
		byte[] data = new byte[dataLength];
		this.raFile.read(data);

		ByteArrayInputStream commonInStream = new ByteArrayInputStream(data);

		// Return appropriate
		switch (dataVersion) {
			case SECTION_VERSION_ZSTD:
				return new ZstdInputStream(commonInStream);
			default: // If version is wrong, use uncompressed InputStream.
				return commonInStream;
		}

	}
	
	// LOAD UTILS //

	/**
	 * To know if a section has at least 1 sectors in the region file.
	 * @param x The relative X section coordinate in this region.
	 * @param z The relative Z section coordinate in this region.
	 * @return The section is already saved.
	 */
	public boolean isSectionSaved(int x, int z) {
		return getSectCount(this.getSectionOffset(x, z)) != 0;
	}
	
	/**
	 * Close the random access file.
	 */
	public void close() throws IOException {
		this.raFile.close();
	}
	
	// FORMAT UTILS //

	/**
	 * Get the section offset data. Following the format :<br>
	 * <pre><code>
	 *   byte off | 3 | 2 | 1 | 0 |
	 *            +---+---+-+-+---+
	 * bits taken |    21   | 11  |
	 *     	      +---------+-----+
	 *              /           \
	 *     Section start      Number of sectors
	 *     sector offset      used by section
	 * </code></pre>
	 * @param x The relative X section coordinate in this region.
	 * @param z The relative Z section coordinate in this region.
	 * @return Section offset data.
	 */
	public int getSectionOffset(int x, int z) {
		return this.sectionOffsets[getRegionIndex(x, z)];
	}

	/**
	 * Set section offset data (built using {@link #buildSectionOffset(int, int)}).
	 * It addition to {@link #sectionOffsets} update, it will also write to the random access file.
	 * @param x The relative X section coordinate in this region.
	 * @param z The relative Z section coordinate in this region.
	 * @param offset The section offset data.
	 * @throws IOException If write errors occurs to r.a. file.
	 * @see #getSectionOffset(int, int)
	 */
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
		return offset >> 11;
	}
	
	public static int getSectCount(int offset) {
		return offset & 2047;
	}
	
	public static int buildSectionOffset(int sectOffset, int sectCount) {
		return (sectOffset << 11) | sectCount;
	}
	
}
