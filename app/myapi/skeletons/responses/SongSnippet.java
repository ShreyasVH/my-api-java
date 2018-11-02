package myapi.skeletons.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import myapi.models.Artist;
import myapi.models.Song;

import java.util.List;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public class SongSnippet extends Response
{
    @Getter
    @Setter
    public String id;

    @Getter
    @Setter
    public String name;

    @Getter
    @Setter
    public MovieSnippet movie;

    @Getter
    @Setter
    public Long size;

    @Getter
    @Setter
    public List<Artist> singers;

    @Getter
    @Setter
    public List<Artist> composers;

    @Getter
    @Setter
    public List<Artist> lyricists;

    @JsonCreator
    public SongSnippet()
    {

    }

    public SongSnippet(Song song)
    {
        this.id = song.getId();
        this.name = song.getName();
        this.size = song.getSize();
    }
}
