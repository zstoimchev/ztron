package dev.core.models;

import java.util.Arrays;

public record Hash(byte[] value) {
    @Override
    public boolean equals(Object o) {
        return (o instanceof Hash(byte[] value1)) && Arrays.equals(value, value1);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}