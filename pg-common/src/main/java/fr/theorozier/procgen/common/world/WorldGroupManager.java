package fr.theorozier.procgen.common.world;

import java.io.File;

public class WorldGroupManager {
	
	private final File worldDirectory;
	private final File playersDirectory;
	private final File dimensionsDirectory;
	
	public WorldGroupManager(File worldDirectory) {
		
		this.worldDirectory = worldDirectory;
		
		if (!worldDirectory.isDirectory())
			worldDirectory.mkdirs();
		
		this.playersDirectory = new File(worldDirectory, "players");
		this.dimensionsDirectory = new File(worldDirectory, "dims");
		
		if (!this.playersDirectory.isDirectory())
			this.playersDirectory.mkdirs();
		
		if (!this.dimensionsDirectory.isDirectory())
			this.dimensionsDirectory.mkdirs();
		
	}
	
	public File getWorldDirectory() {
		return this.worldDirectory;
	}
	
	public void load() {
	
	
	
	}

}
