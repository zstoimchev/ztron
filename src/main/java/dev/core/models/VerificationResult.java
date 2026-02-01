package dev.core.models;

public record VerificationResult(
        boolean isValid,
        Hash expectedHash,
        Hash actualHash,
        String message
) {

    public static VerificationResult success(Hash hash) {
        return new VerificationResult(
                true,
                hash,
                hash,
                "Verification successful - file matches the expected hash"
        );
    }

    public static VerificationResult failure(Hash expectedHash, Hash actualHash) {
        return new VerificationResult(
                false,
                expectedHash,
                actualHash,
                "Verification failed - file does not match the expected hash"
        );
    }

    public static VerificationResult of(boolean valid, Hash expected, Hash actual, String message) {
        return new VerificationResult(valid, expected, actual, message);
    }
}