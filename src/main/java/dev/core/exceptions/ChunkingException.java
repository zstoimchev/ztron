package dev.core.exceptions;

import java.io.IOException;

public class ChunkingException extends Throwable {
    public ChunkingException(String message, IOException error) {
        super(message, error);
    }

    public ChunkingException(String message) {
        super(message);
    }
}
