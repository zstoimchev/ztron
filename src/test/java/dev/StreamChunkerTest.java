package dev;

import dev.core.exceptions.ChunkingException;
import dev.core.models.Chunk;
import dev.infrastructure.chunking.StreamChunker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StreamChunkerTest {

    @TempDir
    Path tempDir;

    @Test
    void chunksFileCorrectly() throws Exception, ChunkingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "12345678");  // 8 bytes

        StreamChunker chunker = new StreamChunker(4);
        List<Chunk> chunks = chunker.chunk(file);

        assertEquals(2, chunks.size());
        assertEquals(0, chunks.get(0).index());
        assertEquals(1, chunks.get(1).index());
    }

    @Test
    void singleChunkForSmallFile() throws Exception, ChunkingException {
        Path file = tempDir.resolve("short.txt");
        Files.writeString(file, "Short");

        StreamChunker chunker = new StreamChunker(1024);
        List<Chunk> chunks = chunker.chunk(file);

        assertEquals(1, chunks.size());
    }

    @Test
    void oddNumberOfChunks() throws Exception, ChunkingException {
        Path file = tempDir.resolve("odd.txt");
        Files.writeString(file, "twoThree".repeat(7)); // 56 bytes

        StreamChunker chunker = new StreamChunker(10);
        List<Chunk> chunks = chunker.chunk(file);

        assertEquals(6, chunks.size());  // 56/10 = 5.6, so 6 chunks
    }

    @Test
    void emptyFileProducesNoChunks() throws Exception, ChunkingException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        StreamChunker chunker = new StreamChunker(4);
        List<Chunk> chunks = chunker.chunk(file);

        assertTrue(chunks.isEmpty());
    }

    @Test
    void throwsExceptionForNullPath() {
        StreamChunker chunker = new StreamChunker(4);

        assertThrows(ChunkingException.class, () -> chunker.chunk(null));
    }

    @Test
    void throwsExceptionForNonExistentFile() {
        StreamChunker chunker = new StreamChunker(4);
        Path nonExistent = Path.of("/does/not/exist.txt");

        assertThrows(ChunkingException.class, () -> chunker.chunk(nonExistent));
    }

    @Test
    void largeFileDoesNotCrash() throws Exception, ChunkingException {
        Path file = tempDir.resolve("large.bin");
        byte[] data = new byte[1024 * 1024]; // 1 MB
        for (int i = 0; i < data.length; i++) data[i] = (byte) (i % 256);
        Files.write(file, data);

        StreamChunker chunker = new StreamChunker(1024);
        List<Chunk> chunks = chunker.chunk(file);

        assertEquals(1024, chunks.size());  // 1MB / 1KB = 1024 chunks
    }
}