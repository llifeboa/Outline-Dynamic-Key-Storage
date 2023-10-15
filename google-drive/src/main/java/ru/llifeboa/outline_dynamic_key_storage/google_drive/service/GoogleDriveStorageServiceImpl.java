package ru.llifeboa.outline_dynamic_key_storage.google_drive.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.llifeboa.outline_dynamic_key_storage.core.exception.KeyAlreadyExistsException;
import ru.llifeboa.outline_dynamic_key_storage.core.exception.KeyDoesNotExistException;
import ru.llifeboa.outline_dynamic_key_storage.core.exception.StorageConnectionException;
import ru.llifeboa.outline_dynamic_key_storage.core.model.DynamicKey;
import ru.llifeboa.outline_dynamic_key_storage.core.service.StorageService;
import ru.llifeboa.outline_dynamic_key_storage.google_drive.mapper.DynamicKeyMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
class GoogleDriveStorageServiceImpl implements StorageService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static final String CSV_MIME_TYPE = "text/csv";

    private final String credentialsFilePath;

    @Getter
    @Setter
    private String applicationName = "GoogleDriveStorageService";
    private HttpCredentialsAdapter credential;
    private Drive service;

    @Override
    public DynamicKey getKey(String userId) throws StorageConnectionException, KeyDoesNotExistException {
        FileList fileList;
        try {
            var driveService = getDriveService();
            fileList = driveService.files().list()
                    .setQ("name = '%s'".formatted(userId + ".csv"))
                    .setFields("nextPageToken, files(id, webContentLink)")
                    .execute();
        } catch (Exception e) {
            throw new StorageConnectionException(userId, e);
        }
        if (fileList.getFiles().isEmpty()) throw new KeyDoesNotExistException(userId);
        return DynamicKeyMapper.mapToDynamicKey(fileList.getFiles().get(0), userId);
    }

    @Override
    public DynamicKey createKey(String userId, String key) throws StorageConnectionException, KeyAlreadyExistsException {
        if (isFileExist(userId)) throw new KeyAlreadyExistsException(userId);

        var file = new com.google.api.services.drive.model.File();
        file.setName(userId + ".csv");
        var content = new ByteArrayContent(GoogleDriveStorageServiceImpl.CSV_MIME_TYPE, key.getBytes());

        try {
            var driveService = getDriveService();
            var fileId = driveService.files().create(file, content).setFields("id").execute().getId();

            var permission = new Permission()
                    .setType("anyone")
                    .setRole("reader");
            driveService.permissions().create(fileId, permission).execute();

            var serverFile = driveService.files()
                    .get(fileId)
                    .setFields("id, webContentLink")
                    .execute();

            return DynamicKeyMapper.mapToDynamicKey(serverFile, userId);
        } catch (Exception e) {
            throw new StorageConnectionException(userId, e);
        }
    }

    @Override
    public void updateKey(String userId, String key) throws StorageConnectionException, KeyDoesNotExistException {
        var existKey = getKey(userId);
        var file = new com.google.api.services.drive.model.File();
        file.setName(userId + ".csv");
        var content = new ByteArrayContent(GoogleDriveStorageServiceImpl.CSV_MIME_TYPE, key.getBytes());

        try {
            var driveService = getDriveService();
            driveService.files().update(existKey.getExternalId(), file, content).execute();
        } catch (Exception e) {
            throw new StorageConnectionException(userId, e);
        }
    }

    @Override
    public void deleteKey(String userId) throws StorageConnectionException, KeyDoesNotExistException {
        var existKey = getKey(userId);

        try {
            var driveService = getDriveService();
            driveService.files().delete(existKey.getExternalId()).execute();
        } catch (Exception e) {
            throw new StorageConnectionException(userId, e);
        }
    }

    private boolean isFileExist(String userId) {
        try {
            getKey(userId);
        } catch (KeyDoesNotExistException ignore) {
            return false;
        }
        return true;
    }


    /**
     * Get drive service
     *
     * @return Instance of drive service
     */
    private Drive getDriveService() throws GeneralSecurityException, IOException {

        if (service != null) return service;
        var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, GoogleDriveStorageServiceImpl.JSON_FACTORY, getCredentials())
                .setApplicationName(applicationName)
                .build();
        return service;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private HttpCredentialsAdapter getCredentials()
            throws IOException {

        if (credential != null) {
            credential.getCredentials().refresh();
            return credential;
        }

        var googleCredentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))
                .createScoped(GoogleDriveStorageServiceImpl.SCOPES);
        googleCredentials.refreshIfExpired();
        return credential = new HttpCredentialsAdapter(googleCredentials);
    }
}
