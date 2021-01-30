package myapi.models;

import myapi.constants.Constants;
import lombok.Getter;

/**
 * Created by shreyas.hande on 12/7/17.
 */
public enum MovieAttribute
{
    ID("id", Constants.FIELD_TYPE_NORMAL),
    NAME("name", Constants.FIELD_TYPE_NORMAL),
    LANGUAGE("language", Constants.FIELD_TYPE_NORMAL),
    SIZE("size", Constants.FIELD_TYPE_RANGE),
    FORMAT("format", Constants.FIELD_TYPE_NORMAL),
    QUALITY("quality", Constants.FIELD_TYPE_NORMAL),
    YEAR("year", Constants.FIELD_TYPE_RANGE),
    SUBTITLES("subtitles", Constants.FIELD_TYPE_NORMAL),
    SEEN("seen_in_theatre", Constants.FIELD_TYPE_NORMAL),
    BASENAME("basename", Constants.FIELD_TYPE_NORMAL),
    ACTORS("actors", Constants.FIELD_TYPE_NORMAL),
    DIRECTORS("directors", Constants.FIELD_TYPE_NORMAL),
    STATUS("status", Constants.FIELD_TYPE_NORMAL);

    @Getter
    private String fieldName;

    @Getter
    private String type;

    @Getter
    private String nestedTerm;

    @Getter
    private String nestedLevel;

    MovieAttribute(String fieldName, String type)
    {
        this.fieldName = fieldName;
        this.type = type;
    }

    MovieAttribute(String fieldName, String type, String nestedTerm, String nestedLevel)
    {
        this.fieldName = fieldName;
        this.type = type;
        this.nestedTerm = nestedTerm;
        this.nestedLevel = nestedLevel;
    }

    public static MovieAttribute getMovieAttributeByName(String name)
    {
        MovieAttribute filterType = null;
        for(MovieAttribute type : MovieAttribute.values())
        {
            if(type.getFieldName().equals(name))
            {
                filterType = type;
                break;
            }
        }
        return filterType;
    }
}
