package dev.core.models;

import java.util.Arrays;
import java.util.HexFormat;

public record Hash(byte[] value) {
    @Override
    public boolean equals(Object o) {
        return (o instanceof Hash(byte[] value1)) && Arrays.equals(value, value1);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    public String toHex() {
        return HexFormat.of().formatHex(value);
    }
}