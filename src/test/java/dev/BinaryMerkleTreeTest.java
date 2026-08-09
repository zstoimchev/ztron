package dev;

import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Hash;
import dev.core.models.MerkleNode;
import dev.core.services.ChunkingService;
import dev.core.services.HashingService;
import dev.infrastructure.chunking.StreamChunker;
import dev.infrastructure.hashing.Sha256Hasher;
import dev.infrastructure.merkle.BinaryMerkleTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinaryMerkleTreeTest {

    @TempDir
    Path tempDir;

    private HashingService hasher;
    private BinaryMerkleTree tree;

    @BeforeEach
    void setUp() {
        ChunkingService chunker = new StreamChunker(4);
        hasher = new Sha256Hasher();
        tree = new BinaryMerkleTree(chunker, hasher);
    }

    @Test
    void sameFileProducesSameRoot() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello, World. Hello, Zhivko!");

        Hash root1 = tree.buildTreeHash(file);
        Hash root2 = tree.buildTreeHash(file);

        assertEquals(root1, root2, "Merkle root must be deterministic");
    }

    @Test
    void differentFilesProduceDifferentRoots() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file1, "Hello, World.");
        Files.writeString(file2, "Hello, Zhivko!");

        Hash root1 = tree.buildTreeHash(file1);
        Hash root2 = tree.buildTreeHash(file2);

        assertNotEquals(root1, root2, "Different files should have different roots");
    }

    @Test
    void emptyFileProducesHash() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        Hash root = tree.buildTreeHash(file);

        assertNotNull(root, "Root hash should not be null for empty file");
    }

    @Test
    void differentChunkSizesProduceDifferentRoots() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "This is a test file to check chunk sizes.");

        ChunkingService smallChunker = new StreamChunker(4);
        ChunkingService largeChunker = new StreamChunker(8);

        BinaryMerkleTree smallTree = new BinaryMerkleTree(smallChunker, hasher);
        BinaryMerkleTree largeTree = new BinaryMerkleTree(largeChunker, hasher);

        Hash rootSmall = smallTree.buildTreeHash(file);
        Hash rootLarge = largeTree.buildTreeHash(file);

        assertNotEquals(rootSmall, rootLarge, "Different chunk sizes should produce different roots");
    }

    @Test
    void buildTreeReturnsCorrectStructure() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "12345678");  // 2 chunks of 4 bytes

        MerkleNode root = tree.buildTree(file);

        assertNotNull(root);
        assertNotNull(root.hash());
        assertNotNull(root.left());
        assertNotNull(root.right());
        assertNull(root.left().left());  // Leaf nodes have no children
        assertNull(root.left().right());
    }

    @Test
    void buildTreeFromHashesWithSingleHash() throws HashingException, MerkleTreeException {
        Hash singleHash = hasher.hash("test".getBytes());

        MerkleNode root = tree.buildTreeFromHashes(List.of(singleHash));

        assertNotNull(root);
        assertEquals(singleHash, root.hash());
        assertNull(root.left());
        assertNull(root.right());
    }

    @Test
    void buildTreeFromHashesWithMultipleHashes() throws HashingException, MerkleTreeException {
        List<Hash> hashes = List.of(
                hasher.hash("chunk1".getBytes()),
                hasher.hash("chunk2".getBytes()),
                hasher.hash("chunk3".getBytes())
        );

        MerkleNode root = tree.buildTreeFromHashes(hashes);

        assertNotNull(root);
        assertNotNull(root.left());
        assertNotNull(root.right());
    }

    @Test
    void throwsExceptionForEmptyHashList() {
        assertThrows(MerkleTreeException.class, () -> tree.buildTreeFromHashes(List.of()));
    }

    @Test
    void throwsExceptionForNullHashList() {
        assertThrows(MerkleTreeException.class, () -> tree.buildTreeFromHashes(null));
    }

    @Test
    void throwsExceptionForNullPath() {
        assertThrows(MerkleTreeException.class, () -> tree.buildTreeHash(null));
    }

    @Test
    void largeFileDoesNotCrash() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("large.bin");
        byte[] data = new byte[1024 * 1024]; // 1 MB
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        Files.write(file, data);

        ChunkingService largeChunker = new StreamChunker(1024);
        BinaryMerkleTree largeTree = new BinaryMerkleTree(largeChunker, hasher);

        Hash root = largeTree.buildTreeHash(file);

        assertNotNull(root, "Root hash should exist for large file");
    }

    @Test
    void constructorShouldRejectZeroChunkSize() {
        assertThrows(IllegalArgumentException.class, () -> new StreamChunker(0));
    }
}