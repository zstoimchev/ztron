package dev.core.services;

import dev.core.exceptions.ChunkingException;
import dev.core.models.Chunk;

import java.nio.file.Path;
import java.util.List;

public interface ChunkingService {
    List<Chunk> chunk(Path path) throws ChunkingException;
}
