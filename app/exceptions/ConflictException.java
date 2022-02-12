package exceptions;

import enums.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConflictException extends MyException
{
    static final long serialVersionUID = 6L;

    private Integer httpStatusCode = 409;

    public ConflictException(Integer code, String description)
    {
        super(code, description);
    }

    public ConflictException(String entity)
    {
        super(ErrorCode.ALREADY_EXISTS.getCode(), entity + " already exists");
    }
}
