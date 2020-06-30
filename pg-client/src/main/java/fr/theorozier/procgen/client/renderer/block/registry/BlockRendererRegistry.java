package fr.theorozier.procgen.client.renderer.block.registry;

import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.registry.Registry;

public interface BlockRendererRegistry extends Registry<BlockRenderer> {

	default void register(Block block, BlockRenderer value) {
		this.register(block.getIdentifier(), value);
	}

}
