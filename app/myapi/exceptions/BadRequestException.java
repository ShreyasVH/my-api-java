package myapi.exceptions;

import lombok.Getter;
import myapi.models.ResponseCode;

/**
 * Created by shreyas.hande on 12/7/17.
 */
public class BadRequestException extends MyException
{
    @Getter
    private final int httpResponseCode = 400;

    public BadRequestException(ResponseCode responseCode, Throwable cause)
    {
        super(responseCode, cause);
    }

    public BadRequestException(ResponseCode responseCode)
    {
        super(responseCode);
    }
}
