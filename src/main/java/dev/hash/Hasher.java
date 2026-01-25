package dev.hash;

import dev.chunk.Chunk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Hasher {

    public Hash hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return new Hash(md.digest(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Hash combineHashes(Hash left, Hash right) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(left.value());
            digest.update(right.value());
            return new Hash(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public List<Hash> hashLeaves(List<Chunk> chunks) {
        List<Hash> hashes = new ArrayList<>();
        for (Chunk chunk : chunks) {
            hashes.add(this.hash(chunk.data()));
        }
        return hashes;
    }
}