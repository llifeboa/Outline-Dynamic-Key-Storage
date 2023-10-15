package ru.llifeboa.outline_dynamic_key_storage.google_drive.mapper;

import com.google.api.services.drive.model.File;
import ru.llifeboa.outline_dynamic_key_storage.core.model.DynamicKey;

public class DynamicKeyMapper {

    public static DynamicKey mapToDynamicKey(File file, String userId) {
        return DynamicKey.builder()
                .userId(userId)
                .link(file.getWebContentLink())
                .externalId(file.getId())
                .build();
    }

}
