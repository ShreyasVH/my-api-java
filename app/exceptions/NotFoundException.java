package exceptions;

import enums.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotFoundException extends MyException
{
    static final long serialVersionUID = 2L;

    private Integer httpStatusCode = 404;

    public NotFoundException(Integer code, String description)
    {
        super(code, description);
    }

    public NotFoundException(String entity)
    {
        this(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), entity));
    }
}
