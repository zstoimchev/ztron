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

import java.util.List;

public class Main {
    private final MerkleTreeCli cli;
    private static final String VERSION = "v0.0.1";

    public Main() {
        ChunkingService chunkingService = new StreamChunker(1024 * 1024);
        HashingService hashingService = new Sha256Hasher();
        MerkleTreeService merkleTreeService = new BinaryMerkleTree(chunkingService, hashingService);

        Command buildCommand = new BuildCommand(merkleTreeService);
        Command verifyCommand = new VerifyCommand();
        Command compareCommand = new CompareCommand();
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
        System.out.println("===========================================================n");
        System.out.println("*  zTron - File integrity verification using Merkle trees  *");
        System.out.println("============================================================");

        cli.run(args);
    }
}