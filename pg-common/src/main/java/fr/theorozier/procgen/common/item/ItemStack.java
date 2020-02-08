package fr.theorozier.procgen.common.item;

public class ItemStack {
	
	private final Item item;
	private byte count;
	
	public ItemStack(Item item, byte count) {
		
		this.item = item;
		this.count = count;
		
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public byte getCount() {
		return this.count;
	}
	
	public void setCount(byte count) {
		
		if (count < 0)
			throw new IllegalArgumentException("Invalid size, must be greater or equals than 0.");
		
		this.count = count;
	}
	
	public byte getMaxCount() {
		return this.item.getStackSize();
	}
	
}
