package ru.llifeboa.outline_dynamic_key_storage.core.exception;

public class KeyDoesNotExistException extends StorageException {

    public KeyDoesNotExistException(String userId) {
        super("Key does not exist! userId: %s".formatted(userId));
    }

}
