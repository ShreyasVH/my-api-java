package myapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by shreyas.hande on 12/6/17.
 */
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorResponse implements ResponseCode
{
    API_FAILED(4000, "API Failed"),
    ITEM_NOT_FOUND(4001, "%s not found");

    @Getter
    private final ResponseType type = ResponseType.ERROR;

    @Getter
    private final Integer code;

    @Getter
    private final String description;
}
