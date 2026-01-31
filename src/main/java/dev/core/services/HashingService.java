package dev.core.services;

import dev.core.exceptions.HashingException;
import dev.core.models.Chunk;
import dev.core.models.Hash;

import java.util.List;

public interface HashingService {
    Hash hash(byte[] data) throws HashingException;

    Hash combineHashes(Hash left, Hash right) throws HashingException;

    List<Hash> hashLeaves(List<Chunk> chunks) throws HashingException;
}
