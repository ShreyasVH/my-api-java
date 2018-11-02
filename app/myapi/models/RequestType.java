package myapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by shreyas.hande on 12/10/17.
 */
@AllArgsConstructor
public enum RequestType
{
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete");

    @Getter
    private final String name;
}
