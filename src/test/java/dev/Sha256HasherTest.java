package dev;

import dev.core.exceptions.HashingException;
import dev.core.models.Chunk;
import dev.core.models.Hash;
import dev.infrastructure.hashing.Sha256Hasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Sha256HasherTest {

    private Sha256Hasher hasher;

    @BeforeEach
    void setUp() {
        hasher = new Sha256Hasher();
    }

    @Test
    void identicalDataProducesSameHash() throws HashingException {
        byte[] data = "Hello".getBytes();

        Hash h1 = hasher.hash(data);
        Hash h2 = hasher.hash(data);

        assertEquals(h1, h2, "Identical data should produce identical hashes");
    }

    @Test
    void differentDataProducesDifferentHash() throws HashingException {
        byte[] data1 = "Hello".getBytes();
        byte[] data2 = "World".getBytes();

        Hash h1 = hasher.hash(data1);
        Hash h2 = hasher.hash(data2);

        assertNotEquals(h1, h2, "Different data should produce different hashes");
    }

    @Test
    void hashIsCorrectLength() throws HashingException {
        byte[] data = "test".getBytes();
        Hash hash = hasher.hash(data);

        assertEquals(32, hash.value().length, "SHA-256 should produce 32 bytes");
    }

    @Test
    void combineHashesIsDeterministic() throws HashingException {
        Hash h1 = hasher.hash("left".getBytes());
        Hash h2 = hasher.hash("right".getBytes());

        Hash combined1 = hasher.combineHashes(h1, h2);
        Hash combined2 = hasher.combineHashes(h1, h2);

        assertEquals(combined1, combined2, "Combining same hashes should be deterministic");
    }

    @Test
    void combineHashesOrderMatters() throws HashingException {
        Hash h1 = hasher.hash("left".getBytes());
        Hash h2 = hasher.hash("right".getBytes());

        Hash combined1 = hasher.combineHashes(h1, h2);
        Hash combined2 = hasher.combineHashes(h2, h1);

        assertNotEquals(combined1, combined2, "Hash order should matter");
    }

    @Test
    void hashAllProcessesAllChunks() throws HashingException {
        List<Chunk> chunks = List.of(
                new Chunk(0, "chunk1".getBytes()),
                new Chunk(1, "chunk2".getBytes()),
                new Chunk(2, "chunk3".getBytes())
        );

        List<Hash> hashes = hasher.hashLeaves(chunks);

        assertEquals(3, hashes.size());
    }

    @Test
    void throwsExceptionForNullData() {
        assertThrows(HashingException.class, () -> hasher.hash(null));
    }

    @Test
    void throwsExceptionForNullHashes() throws HashingException {
        Hash h1 = hasher.hash("test".getBytes());

        assertThrows(HashingException.class, () -> hasher.combineHashes(null, h1));
        assertThrows(HashingException.class, () -> hasher.combineHashes(h1, null));
    }

    @Test
    void emptyDataProducesHash() throws HashingException {
        byte[] empty = new byte[0];
        Hash hash = hasher.hash(empty);

        assertNotNull(hash);
        assertEquals(32, hash.value().length);
    }
}