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

class VerifyCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void printsVerifiedWhenHashMatches() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello zTron");

        FakeMerkleTreeService service = new FakeMerkleTreeService(true);

        VerifyCommand command = new VerifyCommand(service);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            command.execute(new String[]{file.toString(), "010203ff"});
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("VERIFIED", output.toString().trim());
    }

    @Test
    void printsFailedWhenHashDoesNotMatch() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello zTron");

        FakeMerkleTreeService service = new FakeMerkleTreeService(false);

        VerifyCommand command = new VerifyCommand(service);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            command.execute(new String[]{file.toString(), "010203ff"});
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("FAILED", output.toString().trim());
    }

    @Test
    void passesFileAndHashToMerkleTreeService() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello zTron");

        FakeMerkleTreeService service = new FakeMerkleTreeService(true);

        VerifyCommand command = new VerifyCommand(service);

        command.execute(new String[]{file.toString(), "010203ff"});

        assertEquals(file, service.receivedFile);

        assertEquals(Hash.fromHex("010203ff"), service.receivedHash);
    }

    @Test
    void throwsWhenArgumentsAreMissing() {
        VerifyCommand command = new VerifyCommand(new FakeMerkleTreeService(true));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[0]));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{"file.txt"}));
    }

    @Test
    void throwsWhenFileDoesNotExist() {
        VerifyCommand command = new VerifyCommand(new FakeMerkleTreeService(true));

        Path missing = tempDir.resolve("missing.txt");

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{missing.toString(), "010203ff"}));
    }

    @Test
    void throwsWhenHashIsMalformed() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello zTron");

        VerifyCommand command = new VerifyCommand(new FakeMerkleTreeService(true));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{file.toString(), "not-a-hash"}));
    }

    private static class FakeMerkleTreeService implements MerkleTreeService {

        private final boolean verificationResult;

        private Path receivedFile;
        private Hash receivedHash;

        FakeMerkleTreeService(boolean verificationResult) {
            this.verificationResult = verificationResult;
        }

        @Override
        public boolean verify(Path file, Hash expectedHash) {
            receivedFile = file;
            receivedHash = expectedHash;

            return verificationResult;
        }

        @Override
        public Hash buildTreeHash(Path path) {
            throw new UnsupportedOperationException();
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
        public VerificationResult verifyDetailed(Path file, Hash expectedHash) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean compare(Path file1, Path file2) {
            throw new UnsupportedOperationException();
        }
    }
}