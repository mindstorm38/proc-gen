package fr.theorozier.procgen.renderer.world;

import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.msengine.common.util.Color;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class ColorMapManager {
	
	public static final String FOLIAGE    = "textures/colormaps/foliage.png";
	public static final String GRASS      = "textures/colormaps/grass.png";
	public static final int COLORMAP_SIZE = 256;
	public static final int COLORMAP_MAX  = 255;
	
	private final ResourceManager resourceManager;
	private final int[] foliagesPixels;
	private final int[] grassPixels;
	
	private final Color cacheColor;
	
	public ColorMapManager(ResourceManager resourceManager) {
		
		this.resourceManager = resourceManager;
		this.foliagesPixels = new int[COLORMAP_SIZE * COLORMAP_SIZE];
		this.grassPixels = new int[COLORMAP_SIZE * COLORMAP_SIZE];
		
		this.cacheColor = new Color();
		
	}
	
	public void refresh() {
		
		DetailledResource foliageRes = this.resourceManager.getDetailledResource(FOLIAGE);
		DetailledResource grassRes = this.resourceManager.getDetailledResource(GRASS);
		
		if (foliageRes == null || grassRes == null)
			throw new IllegalStateException("Missing foliage or grass color maps.");
		
		loadImageTo(foliageRes.getImage(), this.foliagesPixels);
		loadImageTo(grassRes.getImage(), this.grassPixels);
		
	}
	
	public void getFoliageColor(Consumer<Color> colorConsumer, float tempNorm, float humidityNorm) {
		
		int x = (int) (COLORMAP_MAX * (1 - tempNorm));
		int y = (int) (COLORMAP_MAX * (1 - humidityNorm));
	
		int pxl = this.foliagesPixels[y * COLORMAP_SIZE + x];
		// this.cacheColor.setAll((pxl & 0xFF) / 255f, ((pxl >> 8) & 0xFF) / 255f, ((pxl >> 16) & 0xFF) / 255f);
		this.cacheColor.setAll(((pxl >> 16) & 0xFF) / 255f, ((pxl >> 8) & 0xFF) / 255f, (pxl & 0xFF) / 255f);
		colorConsumer.accept(this.cacheColor);
		
	}
	
	public void getGrassColor(Consumer<Color> colorConsumer, float tempNorm, float humidityNorm) {
		
		int x = (int) (COLORMAP_MAX * (1 - tempNorm));
		int y = (int) (COLORMAP_MAX * (1 - humidityNorm));
		
		int pxl = this.grassPixels[y * COLORMAP_SIZE + x];
		// this.cacheColor.setAll((pxl & 0xFF) / 255f, ((pxl >> 8) & 0xFF) / 255f, ((pxl >> 16) & 0xFF) / 255f);
		this.cacheColor.setAll(((pxl >> 16) & 0xFF) / 255f, ((pxl >> 8) & 0xFF) / 255f, (pxl & 0xFF) / 255f);
		colorConsumer.accept(this.cacheColor);
		
	}
	
	private static void loadImageTo(BufferedImage img, int[] pixels) {
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
	}

}
