package dev.merkle;

import dev.chunk.Chunker;
import dev.hash.Hash;
import dev.hash.Hasher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MerkleTreeTest {
    @Test
    void sameFileProducesSameRoot() throws IOException {
        Path tempFile = Files.createTempFile("merkleTest", ".txt");
        Files.writeString(tempFile, "Hello, World. Hello, Zhivko!");

        MerkleTree tree = new MerkleTree(4);

        Hash root1 = tree.buildTree(tempFile);
        Hash root2 = tree.buildTree(tempFile);

        assertEquals(root1, root2, "Merkle root must be deterministic");
    }

    @Test
    void differentFilesProduceDifferentRoots() throws IOException {
        MerkleTree tree = new MerkleTree(4);
        Path tempFile = Files.createTempFile("merkleTest", ".txt");

        Files.writeString(tempFile, "Hello, World.");
        Hash root1 = tree.buildTree(tempFile);

        Files.writeString(tempFile, "Hello, Zhivko!");
        Hash root2 = tree.buildTree(tempFile);

        assertNotEquals(root1, root2, "Different files should have different roots");
    }

    @Test
    void emptyFileProducesHash() throws IOException {
        Path tempFile = Files.createTempFile("merkleTestEmpty", ".txt");
        Files.writeString(tempFile, "");

        MerkleTree tree = new MerkleTree(4);
        Hash root = tree.buildTree(tempFile);

        assertNotNull(root, "Root hash should not be null for empty file");
    }

    @Test
    void singleChunkFile() throws IOException {
        Path tempFile = Files.createTempFile("merkleTestSingleChunk", ".txt");
        Files.writeString(tempFile, "Short");

        MerkleTree tree = new MerkleTree(1024);

        Chunker chunker = new Chunker(1024);
        assertEquals(1, chunker.chunk(tempFile).size(), "File should produce exactly 1 chunk");

        Hash root = tree.buildTree(tempFile);
        assertNotNull(root);
    }

    @Test
    void oddNumberOfChunks() throws IOException {
        Path tempFile = Files.createTempFile("merkleTestOddChunks", ".txt");
        Files.writeString(tempFile, "twoThree".repeat(7) // 7*8=56 bytes
        );

        MerkleTree tree = new MerkleTree(10); // chunk size 10 → 6 chunks total (last duplicated?)
        Hash root = tree.buildTree(tempFile);

        assertNotNull(root, "Root should exist for file with odd number of chunks");
    }

    @Test
    void identicalChunksHaveSameHash() {
        Hasher hasher = new Hasher();
        byte[] data = "Hello".getBytes();

        Hash h1 = hasher.hash(data);
        Hash h2 = hasher.hash(data);

        assertEquals(h1, h2, "Identical data should produce identical hashes");
    }

    @Test
    void differentChunkSizesProduceDifferentRoots() throws IOException {
        Path tempFile = Files.createTempFile("merkleTestChunkSize", ".txt");
        Files.writeString(tempFile, "This is a test file to check chunk sizes.");

        MerkleTree treeSmall = new MerkleTree(4);
        MerkleTree treeLarge = new MerkleTree(8);

        Hash rootSmall = treeSmall.buildTree(tempFile);
        Hash rootLarge = treeLarge.buildTree(tempFile);

        assertNotEquals(rootSmall, rootLarge, "Different chunk sizes should produce different roots");
    }

    @Test
    void largeFileDoesNotCrash() throws IOException {
        Path tempFile = Files.createTempFile("merkleTestLarge", ".bin");
        byte[] data = new byte[1024 * 1024]; // 1 MB
        for (int i = 0; i < data.length; i++) data[i] = (byte) (i % 256);
        Files.write(tempFile, data);

        MerkleTree tree = new MerkleTree(1024);
        Hash root = tree.buildTree(tempFile);

        assertNotNull(root, "Root hash should exist for large file");
    }
}
