package dev.cli.commands;

import dev.cli.Command;
import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.services.MerkleTreeService;

import java.nio.file.Files;
import java.nio.file.Path;

public class CompareCommand implements Command {

    private final MerkleTreeService merkleTreeService;

    public CompareCommand(MerkleTreeService merkleTreeService) {
        this.merkleTreeService = merkleTreeService;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("Usage: " + getUsage());

        Path firstFile = Path.of(args[0]);
        Path secondFile = Path.of(args[1]);

        if (!Files.isRegularFile(firstFile)) throw new IllegalArgumentException("File does not exist: " + firstFile);

        if (!Files.isRegularFile(secondFile)) throw new IllegalArgumentException("File does not exist: " + secondFile);

        boolean same = false;
        try {
            same = merkleTreeService.compare(firstFile, secondFile);
        } catch (MerkleTreeException | ChunkingException | HashingException e) {
            throw new RuntimeException(e);
        }

        if (same) System.out.println("MATCH");
        else System.out.println("DIFFERENT");
    }

    @Override
    public String getName() {
        return "compare";
    }

    @Override
    public String getUsage() {
        return "ztron compare <file1> <file2>";
    }

    @Override
    public String getDescription() {
        return "Compare two files using their Merkle root hashes";
    }
}
