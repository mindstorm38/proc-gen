package fr.theorozier.procgen.common.world;

import java.io.File;

public class WorldManager {
	
	private final File worldDirectory;
	private final File playersDirectory;
	private final File dimensionsDirectory;
	
	public WorldManager(File worldDirectory) {
		
		this.worldDirectory = worldDirectory;
		this.playersDirectory = new File(worldDirectory, "players");
		this.dimensionsDirectory = new File(worldDirectory, "dims");
		
	}
	
	public File getWorldDirectory() {
		return this.worldDirectory;
	}
	
	public void load() {
	
	
	
	}

}
