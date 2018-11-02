package myapi.skeletons.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import myapi.models.*;

import javax.persistence.Id;
import java.util.List;

/**
 * Created by shreyasvh on 7/30/17.
 */
//@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSnippet
{

    @Id
    @Getter
    @Setter
    public Long id;

    @Getter
    @Setter
    public String name;

    @Getter
    @Setter
    public Language language;

    @Getter
    @Setter
    public Long size;

    @Getter
    @Setter
    public MovieFormat format;

    @Getter
    @Setter
    public String quality;

    @Getter
    @Setter
    public Integer year;

    @Getter
    @Setter
    public Boolean subtitles;

    @Getter
    @Setter
    public Boolean seen_in_theatre;

    @Getter
    @Setter
    public String basename;

    @Getter
    @Setter
    public List<Artist> actors;

    @Getter
    @Setter
    public List<Artist> directors;

    @Getter
    @Setter
    public Status status;

    public MovieSnippet(Movie movie)
    {
        this.id = movie.getId();
        this.name = movie.getName();
        this.size = movie.getSize();
        this.quality = movie.getQuality();
        this.year = movie.getYear();
        this.subtitles = movie.getSubtitles();
        this.seen_in_theatre = movie.getSeenInTheatre();
        this.basename = movie.getBasename();
        this.status = movie.getStatus();
    }

    @JsonCreator
    public MovieSnippet()
    {

    }
}
