package dev.core.services;

import dev.core.exceptions.ChunkingException;
import dev.core.exceptions.HashingException;
import dev.core.exceptions.MerkleTreeException;
import dev.core.models.Hash;
import dev.core.models.MerkleNode;

import java.nio.file.Path;
import java.util.List;

public interface MerkleTreeService {
    Hash buildTreeHash(Path path) throws MerkleTreeException, ChunkingException, HashingException;

    MerkleNode buildTree(Path path) throws MerkleTreeException, ChunkingException, HashingException;

    MerkleNode buildTreeFromHashes(List<Hash> hashes) throws MerkleTreeException, HashingException;
}
