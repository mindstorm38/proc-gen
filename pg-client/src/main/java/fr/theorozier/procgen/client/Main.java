package fr.theorozier.procgen.client;

import io.msengine.client.game.RenderGameOptions;
import io.sutil.FileUtils;

import java.io.File;

public class Main {
	
	public static void main(String[] args) {
		
		final RenderGameOptions options = new RenderGameOptions(ProcGenGame.class);
		options.setInitialWindowTitle("Procedural Generation (A Minecraft Clone)");
		options.setResourceNamespace("procgen");
		options.setLoggerName("ProcGen");
		options.setAppdataDir(new File(FileUtils.getAppDataDirectory(), ".procgen"));
		
		final ProcGenGame game = new ProcGenGame(options);
		game.start();
		
	}
	
}
