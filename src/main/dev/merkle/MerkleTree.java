package dev.merkle;

import dev.chunk.Chunk;
import dev.chunk.Chunker;
import dev.hash.Hash;
import dev.hash.Hasher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private final Chunker chunker;
    private final Hasher hasher;

    public MerkleTree(int chunkSize) {
        this.chunker = new Chunker(chunkSize);
        this.hasher = new Hasher();
    }

    public Hash buildTree(Path path) throws IOException {
        List<Chunk> chunks = chunker.chunk(path);
        List<Hash> leafHashes = hasher.hashLeaves(chunks);
        MerkleNode root = buildTreeFromHashes(leafHashes);
        return root.hash();
    }

    private MerkleNode buildTreeFromHashes(List<Hash> hashes) {
        if (hashes.isEmpty()) throw new IllegalArgumentException("Cannot build tree from empty hash list");

        List<MerkleNode> nodes = new ArrayList<>();
        for (Hash h : hashes) {
            nodes.add(new MerkleNode(h, null, null));
        }

        while (nodes.size() > 1) {
            List<MerkleNode> parentLevel = new ArrayList<>();

            for (int i = 0; i < nodes.size(); i += 2) {
                MerkleNode left = nodes.get(i);
                MerkleNode right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : left;

                Hash combinedHash = hasher.combineHashes(left.hash(), right.hash());
                MerkleNode parent = new MerkleNode(combinedHash, left, right);
                parentLevel.add(parent);
            }

            nodes = parentLevel;
        }

        return nodes.getFirst();
    }
}