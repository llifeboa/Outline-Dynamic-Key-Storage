package ru.llifeboa.outline_dynamic_key_storage.core.exception;

public class StorageException extends RuntimeException {


    public StorageException(String message, Throwable throwable) {
        super(message, throwable);
    }

    StorageException(String message) {
        super(message);
    }

}
