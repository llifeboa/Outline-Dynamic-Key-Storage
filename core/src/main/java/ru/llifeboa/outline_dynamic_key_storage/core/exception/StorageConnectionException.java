package ru.llifeboa.outline_dynamic_key_storage.core.exception;

public class StorageConnectionException extends StorageException {


    public StorageConnectionException(String userId, Exception e) {
        super("An error occurred while connecting to server! userId: %s".formatted(userId), e);
    }

}
