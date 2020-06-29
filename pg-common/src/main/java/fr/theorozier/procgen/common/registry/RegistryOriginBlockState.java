package fr.theorozier.procgen.common.registry;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class RegistryOriginBlockState extends RegistryOrigin<Block> {
	
	private final Map<Short, BlockState> uidStateRegister = new HashMap<>();
	
	public void computeStatesUids() {
		
		short idx = 0;
		
		for (Block block : this.values()) {
			for (BlockState state : block.getStateContainer().getStates()) {
				this.uidStateRegister.put(++idx, state); // Pre-increment to avoid using 0
				state.setUid(idx);
			}
		}
		
		this.freeze();
		
	}
	
	public BlockState get(short uid) {
		return uid == 0 ? null : this.uidStateRegister.get(uid);
	}
	
}
