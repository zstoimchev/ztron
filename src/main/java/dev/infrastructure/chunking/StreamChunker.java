package dev.infrastructure.chunking;

import dev.core.exceptions.ChunkingException;
import dev.core.models.Chunk;
import dev.core.services.ChunkingService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StreamChunker implements ChunkingService {
    private final int chunkSize;

    public StreamChunker(int chunkSize) {
        if (chunkSize <= 0) throw new IllegalArgumentException("Chunk size must be greater than 0");
        this.chunkSize = chunkSize;
    }

    @Override
    public List<Chunk> chunk(Path path) throws ChunkingException {
        if (path == null) throw new ChunkingException("Path cannot be null");

        if (!Files.exists(path)) throw new ChunkingException("File does not exist: " + path);


        List<Chunk> chunks = new ArrayList<>();
        try (InputStream in = Files.newInputStream(path)) {
            long index = 0;
            byte[] buffer = new byte[chunkSize];
            int read;
            while ((read = in.read(buffer)) != -1) {
                byte[] data = (read == chunkSize) ? buffer.clone() : copyOf(buffer, read);
                chunks.add(new Chunk(index++, data));
            }
        } catch (IOException e) {
            throw new ChunkingException("Failed to chunk file: " + path, e);
        }
        return chunks;
    }

    private static byte[] copyOf(byte[] src, int len) {
        byte[] out = new byte[len];
        System.arraycopy(src, 0, out, 0, len);
        return out;
    }
}
