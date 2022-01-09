package exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BadRequestException extends MyException
{
    static final long serialVersionUID = 3L;

    private Integer httpStatusCode = 400;

    public BadRequestException(Integer code, String description)
    {
        super(code, description);
    }
}
