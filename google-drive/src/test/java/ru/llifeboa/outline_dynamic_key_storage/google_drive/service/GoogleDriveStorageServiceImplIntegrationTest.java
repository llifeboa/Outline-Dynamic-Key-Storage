package ru.llifeboa.outline_dynamic_key_storage.google_drive.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.llifeboa.outline_dynamic_key_storage.core.exception.KeyDoesNotExistException;
import ru.llifeboa.outline_dynamic_key_storage.core.model.DynamicKey;
import ru.llifeboa.outline_dynamic_key_storage.core.service.StorageService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Google Drive storage service integration test")
class GoogleDriveStorageServiceImplIntegrationTest {

    private static final String USER_ID = "192345324";
    private static final String OUTLINE_KEY = "ss://test_server";
    private static final String OUTLINE_KEY_2 = "ss://test_server2";
    private StorageService service;

    private static void assertKeys(DynamicKey expected, DynamicKey actual, String expectedContent) throws IOException {
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getLink(), actual.getLink());
        assertEquals(expectedContent, GoogleDriveStorageServiceImplIntegrationTest.getFileContentFromServer(actual.getLink()));
    }

    private static String getFileContentFromServer(String link) throws IOException {
        var url = new URL(link);

        var content = "";

        try (var bis = new BufferedInputStream(url.openStream());
             var fis = new ByteArrayOutputStream()) {
            var buffer = new byte[1024];
            var count = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) fis.write(buffer, 0, count);
            content = fis.toString();
        }

        return content;
    }


    @DisplayName("Create -> get -> update -> get -> delete -> get key")
    @Test
    void createGetUpdateGetDelete() throws IOException {
        // Create key
        var key = getService().createKey(GoogleDriveStorageServiceImplIntegrationTest.USER_ID, GoogleDriveStorageServiceImplIntegrationTest.OUTLINE_KEY);
        assertEquals(key.getUserId(), GoogleDriveStorageServiceImplIntegrationTest.USER_ID);
        assertNotNull(key.getLink());
        assertNotNull(key.getExternalId());

        // Get created key
        var savedKey = getService().getKey(GoogleDriveStorageServiceImplIntegrationTest.USER_ID);
        GoogleDriveStorageServiceImplIntegrationTest.assertKeys(key, savedKey, GoogleDriveStorageServiceImplIntegrationTest.OUTLINE_KEY);

        // Update key
        service.updateKey(GoogleDriveStorageServiceImplIntegrationTest.USER_ID, GoogleDriveStorageServiceImplIntegrationTest.OUTLINE_KEY_2);

        // Get updated key
        savedKey = getService().getKey(GoogleDriveStorageServiceImplIntegrationTest.USER_ID);
        GoogleDriveStorageServiceImplIntegrationTest.assertKeys(key, savedKey, GoogleDriveStorageServiceImplIntegrationTest.OUTLINE_KEY_2);

        // Delete key
        service.deleteKey(GoogleDriveStorageServiceImplIntegrationTest.USER_ID);

        // Check deleted key
        assertThrows(KeyDoesNotExistException.class,
                () -> getService().getKey(GoogleDriveStorageServiceImplIntegrationTest.USER_ID));
    }

    private StorageService getService() {

        if (service != null) return service;

        return service = new GoogleDriveStorageServiceImpl(
                System.getenv("GOOGLE_DRIVE_CREDENTIALS_FILE"));
    }

}
