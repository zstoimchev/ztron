package dev;

import dev.cli.MerkleTreeCli;

public class Main {
    public static void main(String[] args) {
        final MerkleTreeCli cli = new MerkleTreeCli();

        try {
            cli.run(args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}