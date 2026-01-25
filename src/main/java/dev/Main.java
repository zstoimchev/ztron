package dev;

import dev.hash.Hash;
import dev.merkle.MerkleTree;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: merkle <chunkSize> <file>");
            System.exit(1);
        }

        int chunkSize = Integer.parseInt(args[0]);
        Path file = Path.of(args[1]);

        MerkleTree tree = new MerkleTree(chunkSize);
        Hash root = tree.buildTree(file);

        System.out.println("Merkle root: " + root);
    }
}