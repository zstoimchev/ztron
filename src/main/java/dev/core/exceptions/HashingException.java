package dev.core.exceptions;

import java.security.NoSuchAlgorithmException;

public class HashingException extends Throwable {
    public HashingException(String message, NoSuchAlgorithmException error) {
        super(message, error);
    }

    public HashingException(String message) {
        super(message);
    }
}
