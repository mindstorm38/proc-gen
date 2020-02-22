package fr.theorozier.procgen.common.world.serial.rle;

import fr.theorozier.procgen.common.util.array.supplier.ArraySupplier;
import fr.theorozier.procgen.common.world.biome.Biome;
import fr.theorozier.procgen.common.world.serial.registry.SaveShortRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class SectionBiomeRLE extends RunLengthEncoder<Biome> {
	
	private SaveShortRegistry<Biome> registry;
	private Map<Short, Biome> mappedBiomes;
	
	public SectionBiomeRLE() {
		super(0x100, 3);
	}
	
	// ENCODE //
	
	public int encode(ArraySupplier<Biome> data, DataOutputStream stream, SaveShortRegistry<Biome> registry) throws IOException {
		this.registry = registry;
		int length = super.encode(data, stream);
		this.registry = null;
		return length;
	}
	
	@Override
	protected void writeLength(DataOutputStream stream, int length) throws IOException {
		stream.writeByte(length);
	}
	
	@Override
	protected void writeValue(DataOutputStream stream, int value) throws IOException {
		stream.writeShort(value);
	}
	
	@Override
	protected int encode(Biome value) {
		return this.registry.getMapping(value);
	}
	
	// DECODE //
	
	public void decode(ArraySupplier<Biome> data, DataInputStream stream, int length, Map<Short, Biome> mappedBiomes) throws IOException {
		this.mappedBiomes = mappedBiomes;
		super.decode(data, stream, length);
		this.mappedBiomes = null;
	}
	
	@Override
	protected int readLength(DataInputStream stream) throws IOException {
		return stream.readUnsignedByte();
	}
	
	@Override
	protected int readValue(DataInputStream stream) throws IOException {
		return stream.readShort();
	}
	
	@Override
	protected Biome decode(int raw) {
		return this.mappedBiomes.get((short) raw);
	}
	
}
