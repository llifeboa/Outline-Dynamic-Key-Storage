package ru.llifeboa.outline_dynamic_key_storage.core.service;

import ru.llifeboa.outline_dynamic_key_storage.core.exception.KeyAlreadyExistsException;
import ru.llifeboa.outline_dynamic_key_storage.core.exception.KeyDoesNotExistException;
import ru.llifeboa.outline_dynamic_key_storage.core.exception.StorageConnectionException;
import ru.llifeboa.outline_dynamic_key_storage.core.model.DynamicKey;

/**
 * Service for working with outline dynamic key storage
 */
public interface StorageService {

    /**
     * Get key from storage by user id
     *
     * @param userId user id
     * @return dynamic key
     * @throws StorageConnectionException connection exception
     * @throws KeyDoesNotExistException   key does not exist
     */
    DynamicKey getKey(String userId) throws StorageConnectionException, KeyDoesNotExistException;

    /**
     * Create key in storage
     *
     * @param userId user id
     * @param key    outline key
     * @return dynamic key
     * @throws StorageConnectionException connection exception
     * @throws KeyAlreadyExistsException  key already exist for the user
     */
    DynamicKey createKey(String userId, String key) throws StorageConnectionException, KeyAlreadyExistsException;

    /**
     * Update key in storage
     *
     * @param userId user id
     * @param key    outline key
     * @throws StorageConnectionException connection exception
     * @throws KeyDoesNotExistException   key does not exist for the user
     */
    void updateKey(String userId, String key) throws StorageConnectionException, KeyDoesNotExistException;

    /**
     * Delete key from storage by user id
     *
     * @param userId user id
     * @throws StorageConnectionException connection exception
     * @throws KeyDoesNotExistException   key does not exist for the user
     */
    void deleteKey(String userId) throws StorageConnectionException, KeyDoesNotExistException;

}
