package myapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by shreyas.hande on 12/6/17.
 */
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ValidationResponse implements ResponseCode
{
    MOVIE_NOT_FOUND(5001, "No such movie found"),
    INVALID_REQUEST(5002, "Invalid Request"),
    DUPLICATE_ENTRY(5003, "Duplicate Entry"),
    LANGUAGE_NOT_FOUND(5004, "No such language found"),
    FORMAT_NOT_FOUND(5005, "No such format found"),
    ARTIST_NOT_FOUND(5006, "No such artist found"),
    SONG_NOT_FOUND(5007, "No such song found");

    @Getter
    private final ResponseType type = ResponseType.VALIDATION;

    @Getter
    private final Integer code;

    @Getter
    private final String description;
}
