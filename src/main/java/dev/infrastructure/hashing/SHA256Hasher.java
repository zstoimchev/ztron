package dev.infrastructure.hashing;

import dev.core.exceptions.HashingException;
import dev.core.models.Chunk;
import dev.core.models.Hash;
import dev.core.services.HashingService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SHA256Hasher implements HashingService {

    @Override
    public Hash hash(byte[] data) throws HashingException {
        if (data == null) throw new HashingException("Data to hash cannot be null");

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return new Hash(md.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new HashingException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    public Hash combineHashes(Hash left, Hash right) throws HashingException {
        if (left == null || right == null) throw new HashingException("Hashes to combine cannot be null");

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(left.value());
            digest.update(right.value());
            return new Hash(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new HashingException("SHA-256 algorithm not available", e);
        }
    }

    public List<Hash> hashLeaves(List<Chunk> chunks) throws HashingException {
        if (chunks == null) throw new HashingException("Chunks list cannot be null");
        List<Hash> hashes = new ArrayList<>();
        for (Chunk chunk : chunks) hashes.add(this.hash(chunk.data()));
        return hashes;
    }
}