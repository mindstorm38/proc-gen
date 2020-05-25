package fr.theorozier.procgen.client.renderer.world.chunk;

import fr.theorozier.procgen.common.world.chunk.WorldChunk;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ChunkRedrawFunction extends BiConsumer<WorldChunk, ChunkUploadDescriptor> { }
