package requests;

import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import utils.Utils;

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
    private String releaseDate;
    private Boolean subtitles;
    private Boolean seenInTheatre;
    private String basename;
    private String imageUrl;
    private List<Long> actors;
    private List<Long> directors;
    private boolean obtained;

    public void validate()
    {
        if(!StringUtils.hasText(name))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Name cannot be empty");
        }

        if(null == languageId || languageId <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Language cannot be empty");
        }

        if(!StringUtils.hasText(releaseDate))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Release Date cannot be empty");
        }

        if(Utils.parseDateString(releaseDate) == null)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Release Date");
        }

        if(null == actors || actors.isEmpty())
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Actors cannot be empty");
        }

        if(null == directors || directors.isEmpty())
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Directors cannot be empty");
        }
    }

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

        if(StringUtils.hasText(releaseDate) && Utils.parseDateString(releaseDate) == null)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Release Date");
        }

        if(null != size && size <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid size");
        }

        List<String> validQualities = Arrays.asList("good", "normal", null);
        if(!validQualities.contains(quality))
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
