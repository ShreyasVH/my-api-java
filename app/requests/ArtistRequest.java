package requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.MyException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shreyas.hande on 12/14/17.
 */
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ArtistRequest extends Request
{
    private String id;

    private String name;

    private String gender;

    private String imageUrl;

    public void validate() throws MyException
    {
        if(!StringUtils.hasText(name))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Name cannot be empty");
        }

        if(!StringUtils.hasText(gender))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Gender cannot be empty");
        }
    }

    public void validateForUpdate()
    {
        List<String> validGenders = Arrays.asList("M", "F");
        if(StringUtils.hasText(gender) && !validGenders.contains(gender))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid gender");
        }
    }
}
