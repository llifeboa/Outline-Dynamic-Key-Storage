package ru.llifeboa.outline_dynamic_key_storage.core.exception;

public class KeyAlreadyExistsException extends StorageException {

    public KeyAlreadyExistsException(String userId) {
        super("Key already exists! userId: %s".formatted(userId));
    }

}
