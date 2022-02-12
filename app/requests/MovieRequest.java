package requests;

import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieRequest
{
    private String name;
    private Long languageId;
    private Long size;
    private Long formatId;
    private String quality;
    private Integer year;
    private Boolean subtitles;
    private Boolean seenInTheatre;
    private String basename;
    private String imageUrl;
    private List<String> actors;
    private List<String> directors;

    public void validateForUpdate()
    {
        if(null != languageId && languageId <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Language");
        }

        if(null != formatId && formatId <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Format");
        }

        if(null != size && size <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid size");
        }

        List<String> validQualities = Arrays.asList("good", "normal");
        if(null != quality && !validQualities.contains(quality))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid quality");
        }

        if(null != actors && actors.isEmpty())
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Actors cannot be empty");
        }

        if(null != directors && directors.isEmpty())
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Directors cannot be empty");
        }
    }
}
