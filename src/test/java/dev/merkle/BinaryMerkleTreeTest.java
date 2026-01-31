package dev.merkle;

import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.services.ChunkingService;
import dev.core.services.HashingService;
import dev.infrastructure.chunking.StreamChunker;
import dev.infrastructure.merkle.BinaryMerkleTree;
import dev.core.models.Hash;
import dev.infrastructure.hashing.SHA256Hasher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BinaryMerkleTreeTest {
    @Test
    void sameFileProducesSameRoot() throws IOException, ChunkingException, MerkleTreeException, HashingException {
        Path tempFile = Files.createTempFile("merkleTest", ".txt");
        Files.writeString(tempFile, "Hello, World. Hello, Zhivko!");

        ChunkingService chunker = new StreamChunker(4);
        HashingService hasher = new SHA256Hasher();
        BinaryMerkleTree tree = new BinaryMerkleTree(chunker, hasher);

        Hash root1 = tree.buildTreeHash(tempFile);
        Hash root2 = tree.buildTreeHash(tempFile);

        assertEquals(root1, root2, "Merkle root must be deterministic");
    }

    @Test
    void differentFilesProduceDifferentRoots() throws IOException, ChunkingException, MerkleTreeException, HashingException {
        Path tempFile1 = Files.createTempFile("merkleTest1", ".txt");
        Path tempFile2 = Files.createTempFile("merkleTest2", ".txt");
        Files.writeString(tempFile1, "Hello, World.");
        Files.writeString(tempFile2, "Hello, Zhivko!");

        ChunkingService chunker = new StreamChunker(4);
        HashingService hasher = new SHA256Hasher();
        BinaryMerkleTree tree = new BinaryMerkleTree(chunker, hasher);

        Hash root1 = tree.buildTreeHash(tempFile1);
        Hash root2 = tree.buildTreeHash(tempFile2);

        assertNotEquals(root1, root2, "Different files should have different roots");
    }

    @Test
    void emptyFileProducesHash() throws IOException, ChunkingException, MerkleTreeException, HashingException {
        Path tempFile = Files.createTempFile("merkleTestEmpty", ".txt");
        Files.writeString(tempFile, "");

        ChunkingService chunker = new StreamChunker(4);
        HashingService hasher = new SHA256Hasher();
        BinaryMerkleTree tree = new BinaryMerkleTree(chunker, hasher);

        Hash root = tree.buildTreeHash(tempFile);

        assertNotNull(root, "Root hash should not be null for empty file");
    }

    @Test
    void singleChunkFile() throws IOException, ChunkingException, MerkleTreeException, HashingException {
        Path tempFile = Files.createTempFile("merkleTestSingleChunk", ".txt");
        Files.writeString(tempFile, "Short");

        ChunkingService chunker = new StreamChunker(1024);
        HashingService hasher = new SHA256Hasher();
        BinaryMerkleTree tree = new BinaryMerkleTree(chunker, hasher);

        StreamChunker streamChunker = new StreamChunker(1024);
        assertEquals(1, streamChunker.chunk(tempFile).size(), "File should produce exactly 1 chunk");

        Hash root = tree.buildTreeHash(tempFile);
        assertNotNull(root);
    }

    @Test
    void oddNumberOfChunks() throws IOException, ChunkingException, MerkleTreeException, HashingException {
        Path tempFile = Files.createTempFile("merkleTestOddChunks", ".txt");
        Files.writeString(tempFile, "twoThree".repeat(7)); // 7 * 8 = 56 bytes

        ChunkingService chunker = new StreamChunker(10);
        HashingService hasher = new SHA256Hasher();
        BinaryMerkleTree tree = new BinaryMerkleTree(chunker, hasher);

        Hash root = tree.buildTreeHash(tempFile);

        assertNotNull(root, "Root should exist for file with odd number of chunks");
    }

    @Test
    void identicalChunksHaveSameHash() throws HashingException {
        SHA256Hasher hasher = new SHA256Hasher();
        byte[] data = "Hello".getBytes();

        Hash h1 = hasher.hash(data);
        Hash h2 = hasher.hash(data);

        assertEquals(h1, h2, "Identical data should produce identical hashes");
    }

    @Test
    void differentChunkSizesProduceDifferentRoots() throws IOException, ChunkingException, MerkleTreeException, HashingException {
        Path tempFile = Files.createTempFile("merkleTestChunkSize", ".txt");
        Files.writeString(tempFile, "This is a test file to check chunk sizes.");

        ChunkingService smallChunker = new StreamChunker(4);
        ChunkingService largeChunker = new StreamChunker(8);
        HashingService hasher = new SHA256Hasher();
        BinaryMerkleTree smallTree = new BinaryMerkleTree(smallChunker, hasher);
        BinaryMerkleTree largeTree = new BinaryMerkleTree(largeChunker, hasher);

        Hash rootSmall = smallTree.buildTreeHash(tempFile);
        Hash rootLarge = largeTree.buildTreeHash(tempFile);

        assertNotEquals(rootSmall, rootLarge, "Different chunk sizes should produce different roots");
    }

    @Test
    void largeFileDoesNotCrash() throws IOException, ChunkingException, MerkleTreeException, HashingException {
        Path tempFile = Files.createTempFile("merkleTestLarge", ".bin");
        byte[] data = new byte[1024 * 1024]; // 1 MB
        for (int i = 0; i < data.length; i++) data[i] = (byte) (i % 256);
        Files.write(tempFile, data);

        ChunkingService chunker = new StreamChunker(1024);
        HashingService hasher = new SHA256Hasher();
        BinaryMerkleTree tree = new BinaryMerkleTree(chunker, hasher);

        Hash root = tree.buildTreeHash(tempFile);

        assertNotNull(root, "Root hash should exist for large file");
    }
}
