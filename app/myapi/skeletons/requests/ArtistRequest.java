package myapi.skeletons.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import myapi.exceptions.BadRequestException;
import myapi.exceptions.MyException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import myapi.models.ValidationResponse;

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
        if((null == name) || (name.equals("")))
        {
            throw new BadRequestException(ValidationResponse.INVALID_REQUEST);
        }

        if((null == gender) || (gender.equals("")))
        {
            throw new BadRequestException(ValidationResponse.INVALID_REQUEST);
        }
    }

    public void validateUpdation() throws MyException
    {
        if(null == id)
        {
            throw new BadRequestException(ValidationResponse.INVALID_REQUEST);
        }
    }
}
