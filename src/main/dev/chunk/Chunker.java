package dev.chunk;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Chunker {
    private final int chunkSize;

    public List<Chunk> chunk(Path path) throws IOException {
        List<Chunk> chunks = new ArrayList<>();
        try (InputStream in = Files.newInputStream(path)) {
            long index = 0;
            byte[] buffer = new byte[chunkSize];
            int read;
            while ((read = in.read(buffer)) != -1) {
                byte[] data = (read == chunkSize) ? buffer.clone() : copyOf(buffer, read);
                chunks.add(new Chunk(index++, data));
            }
        }
        return chunks;
    }

    private static byte[] copyOf(byte[] src, int len) {
        byte[] out = new byte[len];
        System.arraycopy(src, 0, out, 0, len);
        return out;
    }
}
