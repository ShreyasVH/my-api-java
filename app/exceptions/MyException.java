package exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyException extends RuntimeException
{
    static final long serialVersionUID = 1L;

    private Integer code;

    private Integer httpStatusCode = 500;

    private String description;

    public MyException(Integer code, String description)
    {
        this.code = code;
        this.description = description;
    }
}
