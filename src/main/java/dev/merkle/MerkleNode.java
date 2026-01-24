package dev.merkle;

import dev.hash.Hash;

public record MerkleNode(Hash hash, MerkleNode left, MerkleNode right) {
}
