package fr.theorozier.procgen.common.world.task.section;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class WorldSectionBlockRegistry {
	
	private final List<BlockState> indexedStates = new ArrayList<>();
	private final Map<Short, Short> statesToNewUids = new HashMap<>();
	
	public short getBlockStateSaveUid(short realUid) {
		
		BlockState state = Blocks.getBlockState(realUid);
		
		if (state == null || state.getBlock().isUnsavable())
			return 0;
		
		return this.statesToNewUids.computeIfAbsent(realUid, uid -> {
			
			this.indexedStates.add(state);
			return (short) this.indexedStates.size();
			
		});
		
	}
	
	public int size() {
		return this.indexedStates.size();
	}
	
	public void foreachStates(BiConsumer<BlockState, Short> consumer) {
		for (short i = 0; i < this.indexedStates.size(); ++i) {
			consumer.accept(this.indexedStates.get(i), i);
		}
	}
	
}
