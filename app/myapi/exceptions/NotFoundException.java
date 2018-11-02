package myapi.exceptions;

import lombok.Getter;
import myapi.models.ResponseCode;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class NotFoundException extends MyException
{
    @Getter
    private final int httpResponseCode = 404;

    public NotFoundException(ResponseCode responseCode, Throwable cause)
    {
        super(responseCode, cause);
    }

    public NotFoundException(ResponseCode responseCode)
    {
        super(responseCode);
    }
}
