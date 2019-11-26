package fr.theorozier.procgen.block;

import fr.theorozier.procgen.world.WorldBlock;
import io.sutil.StringUtils;

public class Block {
	
	private final short uid;
	private final String identifier;
	
	protected boolean opaque = true;
	
	public Block(int uid, String identifier) {
		
		if (uid <= 0)
			throw new IllegalArgumentException("Block's uid can't be less or equal than 0.");
		
		this.uid = (short) uid;
		this.identifier = StringUtils.requireNonNullAndEmpty(identifier, "Block's identifier can't be null or empty.");
		
	}
	
	public short getUid() {
		return this.uid;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public boolean isUnsavable() {
		return false;
	}
	
	public boolean isOpaque() {
		return this.opaque;
	}
	
	public void initBlock(WorldBlock block) {}
	
}
