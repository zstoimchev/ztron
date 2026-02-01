package dev.infrastructure.merkle;

import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Chunk;
import dev.core.models.Hash;
import dev.core.models.MerkleNode;
import dev.core.models.VerificationResult;
import dev.core.services.ChunkingService;
import dev.core.services.HashingService;
import dev.core.services.MerkleTreeService;

import java.nio.file.Files;
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

    @Override
    public boolean verify(Path file, Hash expectedHash) throws MerkleTreeException, ChunkingException, HashingException {
        if (file == null) throw new MerkleTreeException("File path cannot be null");
        if (expectedHash == null) throw new MerkleTreeException("Expected hash cannot be null");
        if (!Files.exists(file)) throw new MerkleTreeException("File does not exist: " + file);

        Hash actualHash = buildTreeHash(file);
        return actualHash.equals(expectedHash);
    }

    @Override
    public VerificationResult verifyDetailed(Path file, Hash expectedHash) throws MerkleTreeException, ChunkingException, HashingException {
        if (file == null) throw new MerkleTreeException("File path cannot be null");
        if (expectedHash == null) throw new MerkleTreeException("Expected hash cannot be null");
        if (!Files.exists(file)) throw new MerkleTreeException("File does not exist: " + file);

        Hash actualHash = buildTreeHash(file);
        if (actualHash.equals(expectedHash)) return VerificationResult.success(actualHash);
        else return VerificationResult.failure(expectedHash, actualHash);
    }

    @Override
    public boolean compare(Path file1, Path file2) throws MerkleTreeException, ChunkingException, HashingException {
        if (file1 == null || file2 == null) {
            throw new MerkleTreeException("File paths cannot be null");
        }
        if (!Files.exists(file1)) throw new MerkleTreeException("File does not exist: " + file1);
        if (!Files.exists(file2)) throw new MerkleTreeException("File does not exist: " + file2);

        Hash hash1 = buildTreeHash(file1);
        Hash hash2 = buildTreeHash(file2);
        return hash1.equals(hash2);
    }
}