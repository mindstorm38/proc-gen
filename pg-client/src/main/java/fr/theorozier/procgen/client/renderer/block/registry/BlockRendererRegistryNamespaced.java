package fr.theorozier.procgen.client.renderer.block.registry;

import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.common.registry.Registry;
import fr.theorozier.procgen.common.registry.RegistryNamespaced;

public class BlockRendererRegistryNamespaced extends RegistryNamespaced<BlockRenderer> implements BlockRendererRegistry {
	
	public BlockRendererRegistryNamespaced(Registry<BlockRenderer> delegate, String namespace) {
		super(delegate, namespace);
	}
	
}
