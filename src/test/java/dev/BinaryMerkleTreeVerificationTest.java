package dev;

import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Hash;
import dev.core.models.VerificationResult;
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

import static org.junit.jupiter.api.Assertions.*;

class BinaryMerkleTreeVerificationTest {

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

    // ==================== Basic Verification Tests ====================

    @Test
    void verifyReturnsTrueForMatchingHash() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Test content for verification");

        Hash originalHash = tree.buildTreeHash(file);
        boolean isValid = tree.verify(file, originalHash);

        assertTrue(isValid, "Verification should succeed for matching hash");
    }

    @Test
    void verifyReturnsFalseForDifferentHash() throws Exception, HashingException, ChunkingException, MerkleTreeException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Original content");

        // Build hash for different content
        Hash wrongHash = hasher.hash("Different content".getBytes());
        boolean isValid = tree.verify(file, wrongHash);

        assertFalse(isValid, "Verification should fail for different hash");
    }

    @Test
    void verifyReturnsFalseWhenFileIsModified() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Original content");

        Hash originalHash = tree.buildTreeHash(file);

        // Modify the file
        Files.writeString(file, "Modified content");

        boolean isValid = tree.verify(file, originalHash);

        assertFalse(isValid, "Verification should fail after file modification");
    }

    @Test
    void verifyWorksForEmptyFile() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        Hash emptyHash = tree.buildTreeHash(file);
        boolean isValid = tree.verify(file, emptyHash);

        assertTrue(isValid, "Verification should work for empty files");
    }

    @Test
    void verifyWorksForLargeFile() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("large.bin");
        byte[] data = new byte[10000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        Files.write(file, data);

        Hash largeHash = tree.buildTreeHash(file);
        boolean isValid = tree.verify(file, largeHash);

        assertTrue(isValid, "Verification should work for large files");
    }

    // ==================== Detailed Verification Tests ====================

    @Test
    void verifyDetailedReturnsSuccessForMatchingHash() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Test content");

        Hash expectedHash = tree.buildTreeHash(file);
        VerificationResult result = tree.verifyDetailed(file, expectedHash);

        assertTrue(result.isValid());
        assertEquals(expectedHash, result.expectedHash());
        assertEquals(expectedHash, result.actualHash());
        assertNotNull(result.message());
        assertTrue(result.message().contains("successful"));
    }

    @Test
    void verifyDetailedReturnsFailureForDifferentHash() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Test content");

        Hash actualHash = tree.buildTreeHash(file);
        Hash wrongHash = hasher.hash("wrong".getBytes());

        VerificationResult result = tree.verifyDetailed(file, wrongHash);

        assertFalse(result.isValid());
        assertEquals(wrongHash, result.expectedHash());
        assertEquals(actualHash, result.actualHash());
        assertNotEquals(result.expectedHash(), result.actualHash());
        assertNotNull(result.message());
        assertTrue(result.message().contains("failed"));
    }

    @Test
    void verifyDetailedProvidesActualAndExpectedHashes() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Original");

        Hash originalHash = tree.buildTreeHash(file);

        Files.writeString(file, "Modified");

        VerificationResult result = tree.verifyDetailed(file, originalHash);

        assertFalse(result.isValid());
        assertEquals(originalHash, result.expectedHash());
        assertNotEquals(originalHash, result.actualHash());
    }

    // ==================== Compare Tests ====================

    @Test
    void compareReturnsTrueForIdenticalFiles() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        String content = "Identical content";
        Files.writeString(file1, content);
        Files.writeString(file2, content);

        boolean areEqual = tree.compare(file1, file2);

        assertTrue(areEqual, "Identical files should have matching hashes");
    }

    @Test
    void compareReturnsFalseForDifferentFiles() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        Files.writeString(file1, "Content A");
        Files.writeString(file2, "Content B");

        boolean areEqual = tree.compare(file1, file2);

        assertFalse(areEqual, "Different files should have different hashes");
    }

    @Test
    void compareWorksWithDifferentFileSizes() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file1 = tempDir.resolve("small.txt");
        Path file2 = tempDir.resolve("large.txt");

        Files.writeString(file1, "Small");
        Files.writeString(file2, "Much larger content here");

        boolean areEqual = tree.compare(file1, file2);

        assertFalse(areEqual, "Files with different sizes should not match");
    }

    @Test
    void compareWorksWithEmptyFiles() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file1 = tempDir.resolve("empty1.txt");
        Path file2 = tempDir.resolve("empty2.txt");

        Files.writeString(file1, "");
        Files.writeString(file2, "");

        boolean areEqual = tree.compare(file1, file2);

        assertTrue(areEqual, "Two empty files should match");
    }

    @Test
    void compareIsSensitiveToSingleByteChange() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        Files.writeString(file1, "The quick brown fox");
        Files.writeString(file2, "The quick brown fax");  // Changed 'o' to 'a'

        boolean areEqual = tree.compare(file1, file2);

        assertFalse(areEqual, "Single byte difference should be detected");
    }

    // ==================== Exception Tests ====================

    @Test
    void verifyThrowsExceptionForNullFile() {
        Hash hash = new Hash(new byte[32]);

        assertThrows(MerkleTreeException.class, () -> tree.verify(null, hash));
    }

    @Test
    void verifyThrowsExceptionForNullHash() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "content");

        assertThrows(MerkleTreeException.class, () -> tree.verify(file, null));
    }

    @Test
    void verifyThrowsExceptionForNonExistentFile() {
        Path nonExistent = tempDir.resolve("does-not-exist.txt");
        Hash hash = new Hash(new byte[32]);

        assertThrows(MerkleTreeException.class, () -> tree.verify(nonExistent, hash));
    }

    @Test
    void verifyDetailedThrowsExceptionForNullFile() {
        Hash hash = new Hash(new byte[32]);

        assertThrows(MerkleTreeException.class, () -> tree.verifyDetailed(null, hash));
    }

    @Test
    void verifyDetailedThrowsExceptionForNullHash() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "content");

        assertThrows(MerkleTreeException.class, () -> tree.verifyDetailed(file, null));
    }

    @Test
    void compareThrowsExceptionForNullFiles() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "content");

        assertThrows(MerkleTreeException.class, () -> tree.compare(null, file));

        assertThrows(MerkleTreeException.class, () -> tree.compare(file, null));

        assertThrows(MerkleTreeException.class, () -> tree.compare(null, null));
    }

    @Test
    void compareThrowsExceptionForNonExistentFiles() throws Exception {
        Path existing = tempDir.resolve("exists.txt");
        Path nonExistent = tempDir.resolve("does-not-exist.txt");
        Files.writeString(existing, "content");

        assertThrows(MerkleTreeException.class, () -> tree.compare(existing, nonExistent));

        assertThrows(MerkleTreeException.class, () -> tree.compare(nonExistent, existing));
    }

    // ==================== Edge Case Tests ====================

    @Test
    void verificationIsDeterministic() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Deterministic test");

        Hash hash = tree.buildTreeHash(file);

        // Verify multiple times
        assertTrue(tree.verify(file, hash));
        assertTrue(tree.verify(file, hash));
        assertTrue(tree.verify(file, hash));
    }

    @Test
    void comparisonIsDeterministic() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        Files.writeString(file1, "Same content");
        Files.writeString(file2, "Same content");

        // Compare multiple times
        assertTrue(tree.compare(file1, file2));
        assertTrue(tree.compare(file1, file2));
        assertTrue(tree.compare(file1, file2));
    }

    @Test
    void verificationWorksWithDifferentChunkSizes() throws Exception, ChunkingException, MerkleTreeException, HashingException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Test with different chunk sizes");

        // Build hash with current chunker (size 4)
        Hash hash4 = tree.buildTreeHash(file);

        // Create new tree with different chunk size
        ChunkingService chunker8 = new StreamChunker(8);
        BinaryMerkleTree tree8 = new BinaryMerkleTree(chunker8, hasher);
        Hash hash8 = tree8.buildTreeHash(file);

        // Hashes should be different with different chunk sizes
        assertNotEquals(hash4, hash8);

        // But each should verify correctly with their own tree
        assertTrue(tree.verify(file, hash4));
        assertTrue(tree8.verify(file, hash8));
    }
}