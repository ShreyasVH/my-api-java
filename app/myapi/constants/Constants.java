package myapi.constants;

import play.Configuration;
import play.Play;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class Constants {
    protected static final Configuration _CONFIGURATION = Play.application().configuration();

    public static final Integer DEFAULT_ELASTIC_OFFSET = 0;

    public static final Integer DEFAULT_ELASTIC_COUNT = 2000;

    public static final String FIELD_TYPE_NORMAL = "normal";

    public static final String FIELD_TYPE_NESTED = "nested";

    public static final String FIELD_TYPE_RANGE = "range";

    public static final String PLAY_ACTOR_CONTEXT = "play.akka.actor.contexts.play-actor-context";

    public static final String SORT_FIELD = "sort";

    public static final String SORT_KEY_ELASTIC = "." + SORT_FIELD;

    public static final Long DEFAULT_OFFSET = 0L;

}
