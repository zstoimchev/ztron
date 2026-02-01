package dev.core.services;

import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Hash;
import dev.core.models.MerkleNode;
import dev.core.models.VerificationResult;

import java.nio.file.Path;
import java.util.List;

public interface MerkleTreeService {
    Hash buildTreeHash(Path path) throws MerkleTreeException, ChunkingException, HashingException;

    MerkleNode buildTree(Path path) throws MerkleTreeException, ChunkingException, HashingException;

    MerkleNode buildTreeFromHashes(List<Hash> hashes) throws MerkleTreeException, HashingException;

    boolean verify(Path file, Hash expectedHash) throws MerkleTreeException, ChunkingException, HashingException;

    VerificationResult verifyDetailed(Path file, Hash expectedHash) throws MerkleTreeException, ChunkingException, HashingException;

    boolean compare(Path file1, Path file2) throws MerkleTreeException, ChunkingException, HashingException;
}
