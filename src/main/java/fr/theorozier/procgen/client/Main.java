package fr.theorozier.procgen.client;

import io.msengine.client.game.RenderGameOptions;

public class Main {
	
	public static void main(String[] args) {
		
		final RenderGameOptions options = new RenderGameOptions(ProcGenGame.class);
		options.setInitialWindowTitle("Procedural Generation Test");
		options.setResourceNamespace("procgen");
		options.setLoggerName("ProcGen");
		
		final ProcGenGame game = new ProcGenGame(options);
		
		game.start();
		
	}
	
}
