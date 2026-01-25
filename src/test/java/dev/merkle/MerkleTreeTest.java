package dev.merkle;

import dev.hash.Hash;
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

        assertEquals(root1, root2);
    }
}