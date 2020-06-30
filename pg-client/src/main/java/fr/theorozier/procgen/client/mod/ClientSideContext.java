package fr.theorozier.procgen.client.mod;

import fr.theorozier.procgen.client.renderer.block.registry.BlockRendererRegistry;
import fr.theorozier.procgen.client.renderer.world.WorldRenderer;
import fr.theorozier.procgen.client.world.WorldList;
import fr.theorozier.procgen.common.mod.SideContext;
import io.msengine.client.gui.GuiManager;
import io.msengine.client.option.Options;

public interface ClientSideContext extends SideContext {

	Options getOptions();
	
	WorldList getWorldList();
	
	WorldRenderer getWorldRenderer();
	
	GuiManager getGuiManager();
	
	BlockRendererRegistry getBlockRendererRegistry(String namespace);

}
