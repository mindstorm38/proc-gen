package fr.theorozier.procgen.common.mod;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.registry.Registry;

public interface SideContext {
	
	Registry<Block> getBlockRegistry(String namespace);
	
}
