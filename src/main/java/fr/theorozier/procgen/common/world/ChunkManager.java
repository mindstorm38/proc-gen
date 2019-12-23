package fr.theorozier.procgen.common.world;

import fr.theorozier.procgen.common.world.gen.ChunkGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Object used to manager world chunks, load them from
 * files or generate them if does not exists.
 *
 * @author Theo Rozier
 *
 */
public class ChunkManager {

	private final WorldServer world;
	private final ChunkGenerator generator;
	
	private final ExecutorService generatorComputer;
	
	public ChunkManager(WorldServer world, ChunkGenerator generator) {
		
		this.world = world;
		this.generator = generator;
		
		this.generatorComputer = Executors.newFixedThreadPool(2);
		
	}
	
	public ChunkGenerator getGenerator() {
		return this.generator;
	}

	public void tick() {
	
	
	
	}
	
}
