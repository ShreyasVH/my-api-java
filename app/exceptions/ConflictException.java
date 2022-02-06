package exceptions;

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
}
