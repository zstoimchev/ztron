package dev.cli.commands;

import dev.cli.Command;
import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Hash;
import dev.core.services.MerkleTreeService;

import java.nio.file.Files;
import java.nio.file.Path;

public class BuildCommand implements Command {
    private final MerkleTreeService merkleTreeService;

    public BuildCommand(MerkleTreeService merkleTreeService) {
        this.merkleTreeService = merkleTreeService;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Usage: " + getUsage());

        Path file = Path.of(args[0]);

        if (!Files.isRegularFile(file)) throw new IllegalArgumentException("File does not exist: " + file);

        Hash rootHash;
        try {
            rootHash = merkleTreeService.buildTreeHash(file);
        } catch (MerkleTreeException | ChunkingException | HashingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(rootHash.toHex());
    }

    @Override
    public String getName() {
        return "build";
    }

    @Override
    public String getUsage() {
        return "build <file>";
    }

    @Override
    public String getDescription() {
        return "Build a Merkle tree from a file and print the root hash";
    }
}