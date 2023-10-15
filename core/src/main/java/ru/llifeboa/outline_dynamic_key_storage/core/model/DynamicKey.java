package ru.llifeboa.outline_dynamic_key_storage.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamicKey {

    /**
     * User id
     */
    String userId;

    /**
     * Web link to dynamic key
     */
    String link;

    /**
     * Key external id
     */
    String externalId;

}
