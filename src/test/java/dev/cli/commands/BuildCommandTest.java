package dev.cli.commands;

import dev.core.models.Hash;
import dev.core.models.MerkleNode;
import dev.core.models.VerificationResult;
import dev.core.services.MerkleTreeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void buildsMerkleTreeAndPrintsRootHash() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello zTron");
        Hash expectedHash = new Hash(new byte[]{0x01, 0x02, 0x03, (byte) 0xff});
        FakeMerkleTreeService service = new FakeMerkleTreeService(expectedHash);
        BuildCommand command = new BuildCommand(service);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            command.execute(new String[]{file.toString()});
        } finally {
            System.setOut(originalOut);
        }

        assertEquals(file, service.receivedPath);
        assertEquals("010203ff", output.toString().trim());
    }

    @Test
    void throwsWhenFileArgumentIsMissing() {
        BuildCommand command = new BuildCommand(new FakeMerkleTreeService(new Hash(new byte[]{0x01})));
        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[0]));
    }

    @Test
    void throwsWhenFileDoesNotExist() {
        BuildCommand command = new BuildCommand(new FakeMerkleTreeService(new Hash(new byte[]{0x01})));
        Path missingFile = tempDir.resolve("missing.txt");
        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{missingFile.toString()}));
    }

    private static class FakeMerkleTreeService implements MerkleTreeService {
        private final Hash result;
        private Path receivedPath;

        FakeMerkleTreeService(Hash result) {
            this.result = result;
        }

        @Override
        public Hash buildTreeHash(Path path) {
            receivedPath = path;
            return result;
        }

        @Override
        public MerkleNode buildTree(Path path) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MerkleNode buildTreeFromHashes(List<Hash> hashes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean verify(Path file, Hash expectedHash) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VerificationResult verifyDetailed(Path file, Hash expectedHash) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean compare(Path file1, Path file2) {
            throw new UnsupportedOperationException();
        }
    }
}