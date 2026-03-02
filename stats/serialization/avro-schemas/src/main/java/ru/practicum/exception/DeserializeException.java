package ru.practicum.exception;

import org.slf4j.Logger;

public class DeserializeException extends RuntimeException {
    public DeserializeException(String message) {
        super(message);
    }

    public DeserializeException(String message, Logger logger) {
        this(message);
        logger.error(message);
    }
}
