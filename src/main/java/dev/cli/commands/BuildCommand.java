package dev.cli.commands;

import dev.cli.Command;
import dev.core.services.MerkleTreeService;

public class BuildCommand implements Command {
    private final MerkleTreeService merkleTreeService;

    public BuildCommand(MerkleTreeService merkleTreeService) {
        this.merkleTreeService = merkleTreeService;
    }

    @Override
    public void execute(String[] args) throws Exception {

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