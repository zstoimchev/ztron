package dev.core.models;

public record MerkleNode(Hash hash, MerkleNode left, MerkleNode right) {
}
