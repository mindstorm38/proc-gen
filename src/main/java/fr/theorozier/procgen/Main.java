package fr.theorozier.procgen;

import fr.theorozier.procgen.game.ProcGenGame;
import io.msengine.client.game.RenderGameOptions;

public class Main {
	
	public static void main(String[] args) {
		
		final RenderGameOptions options = new RenderGameOptions(Main.class);
		options.setInitialWindowTitle("Procedural Generation Test");
		
		final ProcGenGame game = new ProcGenGame(options);
		
		game.start();
		
	}
	
}
