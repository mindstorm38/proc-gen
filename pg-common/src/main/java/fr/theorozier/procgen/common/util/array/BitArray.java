package fr.theorozier.procgen.common.util.array;

public class BitArray {

	private final byte ssize; // Segment size
	private final long smask; // Segment mask
	private final long[] arr; // Data array
	
	public BitArray(int ssize, int length) {
		
		if (ssize < 1 || ssize > 64) {
			throw new IllegalArgumentException("Invalid segment size for a bit array, 1 <= ssize <= 64.");
		}
		
		this.ssize = (byte) ssize;
		this.smask = (1 << this.ssize) - 1;
		this.arr = new long[this.getExpectedLen(length)];
		
	}
	
	private int getExpectedLen(int count) {
		return ((count * this.ssize) / 64) + 1;
	}
	
	public void set(int index, long value) {
		
		int bitoff = index * this.ssize;
		
		int arridx = bitoff / 64;
		int bitidx = bitoff % 64;
		
		if (bitidx == 0) {
			
			long mask = this.smask << (64 - this.ssize);
			this.arr[arridx] &= ~mask;
			this.arr[arridx] |= (value & mask) << (64 - this.ssize);
			
		}
		
		int count1 = Math.min(64 - bitidx, this.ssize);
		
	}

}
