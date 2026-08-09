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

class CompareCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void printsMatchWhenFilesMatch() throws Exception {
        Path firstFile = tempDir.resolve("first.txt");
        Path secondFile = tempDir.resolve("second.txt");

        Files.writeString(firstFile, "Hello");
        Files.writeString(secondFile, "Hello");

        FakeMerkleTreeService service = new FakeMerkleTreeService(true);

        CompareCommand command = new CompareCommand(service);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            command.execute(new String[]{firstFile.toString(), secondFile.toString()});
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("MATCH", output.toString().trim());
    }

    @Test
    void printsDifferentWhenFilesDoNotMatch() throws Exception {
        Path firstFile = tempDir.resolve("first.txt");
        Path secondFile = tempDir.resolve("second.txt");

        Files.writeString(firstFile, "Hello");
        Files.writeString(secondFile, "World");

        FakeMerkleTreeService service = new FakeMerkleTreeService(false);

        CompareCommand command = new CompareCommand(service);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            command.execute(new String[]{firstFile.toString(), secondFile.toString()});
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("DIFFERENT", output.toString().trim());
    }

    @Test
    void passesBothFilesToMerkleTreeService() throws Exception {
        Path firstFile = tempDir.resolve("first.txt");
        Path secondFile = tempDir.resolve("second.txt");

        Files.writeString(firstFile, "Hello");
        Files.writeString(secondFile, "World");

        FakeMerkleTreeService service = new FakeMerkleTreeService(true);

        CompareCommand command = new CompareCommand(service);

        command.execute(new String[]{firstFile.toString(), secondFile.toString()});

        assertEquals(firstFile, service.receivedFirstFile);

        assertEquals(secondFile, service.receivedSecondFile);
    }

    @Test
    void throwsWhenArgumentsAreMissing() {
        CompareCommand command = new CompareCommand(new FakeMerkleTreeService(true));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[0]));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{"first.txt"}));
    }

    @Test
    void throwsWhenTooManyArgumentsAreProvided() {
        CompareCommand command = new CompareCommand(new FakeMerkleTreeService(true));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{"first.txt", "second.txt", "third.txt"}));
    }

    @Test
    void throwsWhenFirstFileDoesNotExist() throws Exception {
        Path firstFile = tempDir.resolve("missing.txt");

        Path secondFile = tempDir.resolve("second.txt");

        Files.writeString(secondFile, "Hello");

        CompareCommand command = new CompareCommand(new FakeMerkleTreeService(true));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{firstFile.toString(), secondFile.toString()}));
    }

    @Test
    void throwsWhenSecondFileDoesNotExist() throws Exception {
        Path firstFile = tempDir.resolve("first.txt");

        Path secondFile = tempDir.resolve("missing.txt");

        Files.writeString(firstFile, "Hello");

        CompareCommand command = new CompareCommand(new FakeMerkleTreeService(true));

        assertThrows(IllegalArgumentException.class, () -> command.execute(new String[]{firstFile.toString(), secondFile.toString()}));
    }

    private static class FakeMerkleTreeService implements MerkleTreeService {

        private final boolean comparisonResult;

        private Path receivedFirstFile;
        private Path receivedSecondFile;

        FakeMerkleTreeService(boolean comparisonResult) {
            this.comparisonResult = comparisonResult;
        }

        @Override
        public boolean compare(Path file1, Path file2) {
            receivedFirstFile = file1;
            receivedSecondFile = file2;

            return comparisonResult;
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
        public boolean verify(Path file, Hash expectedHash) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VerificationResult verifyDetailed(Path file, Hash expectedHash) {
            throw new UnsupportedOperationException();
        }
    }
}