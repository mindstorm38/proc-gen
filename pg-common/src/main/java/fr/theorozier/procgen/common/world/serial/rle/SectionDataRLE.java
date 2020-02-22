package fr.theorozier.procgen.common.world.serial.rle;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.util.array.supplier.ArraySupplier;
import fr.theorozier.procgen.common.world.serial.registry.SaveShortRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class SectionDataRLE extends RunLengthEncoder<Short> {
	
	private SaveShortRegistry<BlockState> registry;
	private Map<Short, BlockState> mappedBlockStates;
	
	public SectionDataRLE() {
		super(0x100, 3);
	}
	
	// ENCODE //
	
	public int encode(ArraySupplier<Short> data, DataOutputStream stream, SaveShortRegistry<BlockState> registry) throws IOException {
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
	protected int encode(Short realUid) {
		return this.registry.getMapping(Blocks.getBlockState(realUid));
	}
	
	// DECODE //
	
	public void decode(ArraySupplier<Short> data, DataInputStream stream, int length, Map<Short, BlockState> mappedBlockStates) throws IOException {
		this.mappedBlockStates = mappedBlockStates;
		super.decode(data, stream, length);
		this.mappedBlockStates = null;
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
	protected Short decode(int raw) {
		BlockState state = this.mappedBlockStates.get((short) raw);
		return state == null ? null : state.getSaveUid();
	}
	
}
