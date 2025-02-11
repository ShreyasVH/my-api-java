package constants;

public class Constants
{
    public static final String SORT_FIELD = "sort";

    public static final String SORT_KEY_ELASTIC = "." + SORT_FIELD;

    public static final String INDEX_NAME_MOVIES = System.getenv("ELASTIC_INDEX_MOVIES");

    public static final String INDEX_NAME_SONGS = System.getenv("ELASTIC_INDEX_SONGS");
}
