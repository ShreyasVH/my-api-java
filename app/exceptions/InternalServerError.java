package exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InternalServerError extends MyException
{
    static final long serialVersionUID = 3L;

    private Integer httpStatusCode = 500;

    public InternalServerError(Integer code, String description)
    {
        super(code, description);
    }
}
