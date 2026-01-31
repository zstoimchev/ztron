package dev.infrastructure.merkle;

import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Chunk;
import dev.core.models.Hash;
import dev.core.models.MerkleNode;
import dev.core.services.ChunkingService;
import dev.core.services.HashingService;
import dev.core.services.MerkleTreeService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BinaryMerkleTree implements MerkleTreeService {
    private final ChunkingService chunkingService;
    private final HashingService hashingService;

    public BinaryMerkleTree(ChunkingService chunkingService, HashingService hashingService) {
        if (chunkingService == null) throw new IllegalArgumentException("ChunkingService cannot be null");
        if (hashingService == null) throw new IllegalArgumentException("HashingService cannot be null");
        this.chunkingService = chunkingService;
        this.hashingService = hashingService;
    }

    @Override
    public Hash buildTreeHash(Path path) throws MerkleTreeException, ChunkingException, HashingException {
        if (path == null) throw new MerkleTreeException("Path cannot be null");

        List<Chunk> chunks = chunkingService.chunk(path);
        if (chunks.isEmpty()) return hashingService.hash(new byte[0]);

        List<Hash> leafHashes = hashingService.hashLeaves(chunks);
        MerkleNode root = buildTreeFromHashes(leafHashes);

        return root.hash();
    }

    @Override
    public MerkleNode buildTree(Path path) throws MerkleTreeException, ChunkingException, HashingException {
        if (path == null) throw new MerkleTreeException("Path cannot be null");

        List<Chunk> chunks = chunkingService.chunk(path);
        if (chunks.isEmpty()) {
            Hash emptyHash = hashingService.hash(new byte[0]);
            return new MerkleNode(emptyHash, null, null);
        }

        List<Hash> leafHashes = hashingService.hashLeaves(chunks);
        return buildTreeFromHashes(leafHashes);
    }

    @Override
    public MerkleNode buildTreeFromHashes(List<Hash> hashes) throws MerkleTreeException, HashingException {
        if (hashes == null || hashes.isEmpty()) throw new MerkleTreeException("Cannot build tree from empty hash list");

        List<MerkleNode> nodes = new ArrayList<>();
        for (Hash h : hashes) nodes.add(new MerkleNode(h, null, null));

        while (nodes.size() > 1) {
            List<MerkleNode> parentLevel = new ArrayList<>();

            for (int i = 0; i < nodes.size(); i += 2) {
                MerkleNode left = nodes.get(i);
                MerkleNode right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : left;

                Hash combinedHash = hashingService.combineHashes(left.hash(), right.hash());
                MerkleNode parent = new MerkleNode(combinedHash, left, right);
                parentLevel.add(parent);
            }

            nodes = parentLevel;
        }

        return nodes.getFirst();
    }
}