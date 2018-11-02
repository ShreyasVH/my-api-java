package myapi.models;

import lombok.Getter;
import myapi.constants.Constants;

/**
 * Created by shreyas.hande on 1/12/18.
 */
public enum SongAttribute
{
    ID("id", Constants.FIELD_TYPE_NORMAL),
    NAME("name", Constants.FIELD_TYPE_NORMAL),
    MOVIE("movie", Constants.FIELD_TYPE_NESTED, "movie.id", "movie"),
    SINGERS("singers", Constants.FIELD_TYPE_NESTED, "singers.id", "singers"),
    COMPOSERS("composers", Constants.FIELD_TYPE_NESTED, "composers.id", "composers"),
    LYRICISTS("lyricists", Constants.FIELD_TYPE_NESTED, "lyricists.id", "lyricists"),
    SIZE("size", Constants.FIELD_TYPE_NORMAL),
    YEAR("year", Constants.FIELD_TYPE_NESTED, "movie.year", "movie"),
    LANGUAGE("language", Constants.FIELD_TYPE_NESTED, "movie.language.id", "movie.language");
    @Getter
    private String fieldName;

    @Getter
    private String type;

    @Getter
    private String nestedTerm;

    @Getter
    private String nestedLevel;

    SongAttribute(String fieldName, String type)
    {
        this.fieldName = fieldName;
        this.type = type;
    }

    SongAttribute(String fieldName, String type, String nestedTerm, String nestedLevel)
    {
        this.fieldName = fieldName;
        this.type = type;
        this.nestedTerm = nestedTerm;
        this.nestedLevel = nestedLevel;
    }

    public static SongAttribute getSongAttributeByName(String name)
    {
        SongAttribute songAttribute = null;
        for(SongAttribute attribute : SongAttribute.values())
        {
            if(attribute.fieldName.equals(name))
            {
                songAttribute = attribute;
                break;
            }
        }
        return songAttribute;
    }

}
