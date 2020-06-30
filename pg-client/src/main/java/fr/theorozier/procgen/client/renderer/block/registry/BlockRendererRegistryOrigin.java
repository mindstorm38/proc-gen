package fr.theorozier.procgen.client.renderer.block.registry;

import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.common.registry.RegistryNamespaced;
import fr.theorozier.procgen.common.registry.RegistryOrigin;

public class BlockRendererRegistryOrigin extends RegistryOrigin<BlockRenderer> {
	
	@Override
	protected RegistryNamespaced<BlockRenderer> newNamespacedRegistry(String ns) {
		return new BlockRendererRegistryNamespaced(this, ns);
	}
	
	@Override
	public BlockRendererRegistry namespaced(String namespace) {
		return (BlockRendererRegistry) super.namespaced(namespace);
	}
	
}
