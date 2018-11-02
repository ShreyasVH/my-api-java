package myapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by shreyas.hande on 12/6/17.
 */

@AllArgsConstructor
public enum ResponseType
{
    SUCCESS("success"),
    ERROR("error"),
    VALIDATION("validation");

    @Getter
    private final String name;
}
