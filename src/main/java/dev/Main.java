package dev;

import dev.cli.Command;
import dev.cli.MerkleTreeCli;
import dev.cli.commands.*;
import dev.core.services.ChunkingService;
import dev.core.services.HashingService;
import dev.core.services.MerkleTreeService;
import dev.infrastructure.chunking.StreamChunker;
import dev.infrastructure.hashing.Sha256Hasher;
import dev.infrastructure.merkle.BinaryMerkleTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Main {
    private final MerkleTreeCli cli;

    public Main() throws IOException {
        Properties properties = new Properties();

        String VERSION;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("version.properties")) {
            if (input == null) throw new IllegalStateException("Could not load version information");
            properties.load(input);
            VERSION = properties.getProperty("version", "unknown");
        }

        ChunkingService chunkingService = new StreamChunker(1024 * 1024);
        HashingService hashingService = new Sha256Hasher();
        MerkleTreeService merkleTreeService = new BinaryMerkleTree(chunkingService, hashingService);

        Command buildCommand = new BuildCommand(merkleTreeService);
        Command verifyCommand = new VerifyCommand(merkleTreeService);
        Command compareCommand = new CompareCommand(merkleTreeService);
        Command versionCommand = new VersionCommand(VERSION);

        List<Command> applicationCommands = List.of(buildCommand, verifyCommand, compareCommand);
        Command helpCommand = new HelpCommand(applicationCommands);

        List<Command> commands = List.of(buildCommand, verifyCommand, compareCommand, helpCommand, versionCommand);

        cli = new MerkleTreeCli(commands);
    }

    public static void main(String[] args) {
        try {
            new Main().run(args);
            System.exit(0);
        } catch (Exception e) {
            System.err.println("zTron caught an unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void run(String[] args) throws Exception {
//        System.out.println("zTron - File integrity verification using Merkle trees\n");
        cli.run(args);
    }
}