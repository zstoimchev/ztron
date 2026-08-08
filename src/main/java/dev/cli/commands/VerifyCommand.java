package dev.cli.commands;

import dev.cli.Command;
import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Hash;
import dev.core.services.MerkleTreeService;

import java.nio.file.Files;
import java.nio.file.Path;

public class VerifyCommand implements Command {
    private final MerkleTreeService merkleTreeService;

    public VerifyCommand(MerkleTreeService merkleTreeService) {
        this.merkleTreeService = merkleTreeService;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("Usage: " + getUsage());

        Path file = Path.of(args[0]);
        if (!Files.isRegularFile(file)) throw new IllegalArgumentException("File does not exist: " + file);

        Hash expectedHash = Hash.fromHex(args[1]);
        boolean valid;
        try {
            valid = merkleTreeService.verify(file, expectedHash);
        } catch (MerkleTreeException | ChunkingException | HashingException e) {
            throw new RuntimeException(e);
        }

        if (valid) System.out.println("VERIFIED");
        else System.out.println("FAILED");
    }

    @Override
    public String getName() {
        return "verify";
    }

    @Override
    public String getUsage() {
        return "verify <file> <hash>";
    }

    @Override
    public String getDescription() {
        return "Verify the integrity of a file against a given hash";
    }
}
