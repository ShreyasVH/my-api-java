package myapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by shreyas.hande on 12/6/17.
 */
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SuccessResponse implements ResponseCode
{
    CREATE_SUCCESS(2001, "entity created successfully"),
    DELETE_SUCCESS(2002, "entity deleted successfully"),
    UPDATE_SUCCESS(2003, "entity updated successfully");

    @Getter
    private final ResponseType type = ResponseType.SUCCESS;

    @Getter
    private final Integer code;

    @Getter
    private final String description;
}
