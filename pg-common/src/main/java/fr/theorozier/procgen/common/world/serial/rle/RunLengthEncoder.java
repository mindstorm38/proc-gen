package fr.theorozier.procgen.common.world.serial.rle;

import fr.theorozier.procgen.common.util.array.supplier.ArraySupplier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class RunLengthEncoder<T> {

	private final int maxLength;
	private final int blocksLength;
	
	public RunLengthEncoder(int maxLength, int blocksLength) {
		this.maxLength = maxLength;
		this.blocksLength = blocksLength;
	}
	
	// ENCODE //
	
	public int encode(ArraySupplier<T> data, DataOutputStream stream) throws IOException {
		
		int length = data.length();
		
		if (length == 0)
			return 0;
		
		T current = data.get(0);
		T last = null;
		
		int count = 1;
		int written = 0;
		
		for (int i = 1; i < length; ++i) {
			
			last = current;
			current = data.get(i);
			
			if (!last.equals(current) || count == this.maxLength) {
				
				// Add -1 to store RLE count, because count = 0 would never occur.
				this.write(stream, count - 1, this.encode(last));
				written += this.blocksLength;
				
				count = 1;
				
			} else {
				++count;
			}
			
		}
		
		this.write(stream, count - 1, this.encode(last));
		return written + this.blocksLength;
		
	}
	
	private void write(DataOutputStream stream, int length, int value) throws IOException {
		this.writeLength(stream, length);
		this.writeValue(stream, value);
	}
	
	protected abstract void writeLength(DataOutputStream stream, int length) throws IOException;
	protected abstract void writeValue(DataOutputStream stream, int value) throws IOException;
	
	protected abstract int encode(T value);
	
	// DECODE //
	
	public void decode(ArraySupplier<T> data, DataInputStream stream, int length) throws IOException {
		
		int dataIndex = 0;
		int nextIndex;
		
		int rawVal;
		T val;
		
		for (int i = 0; i < length; i += this.blocksLength) {
			
			// Re-add +1 because RLE count is stored with -1 offset (because 0-length RLE value never occur).
			nextIndex = dataIndex + this.readLength(stream) + 1;
			rawVal = this.readValue(stream);
			
			val = this.decode(rawVal);
			
			if (val == null) {
				dataIndex = nextIndex;
			} else {
				
				for (; dataIndex < nextIndex; ++dataIndex) {
					data.set(dataIndex, val);
				}
				
			}
			
		}
	
	}
	
	protected abstract int readLength(DataInputStream stream) throws IOException;
	protected abstract int readValue(DataInputStream stream) throws IOException;
	
	protected abstract T decode(int raw);
	
}
