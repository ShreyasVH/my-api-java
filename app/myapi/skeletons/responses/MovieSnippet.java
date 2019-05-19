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
@Getter
@Setter
public class MovieSnippet
{

    @Id
    public Long id;

    public String name;

    public Language language;

    public Long size;

    public MovieFormat format;

    public String quality;

    public Integer year;

    public Boolean subtitles;

    public Boolean seen_in_theatre;

    public String basename;

    public List<Artist> actors;

    public List<Artist> directors;

    public Status status;

    private String imageUrl;

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
        this.imageUrl = movie.getImageUrl();
    }

    @JsonCreator
    public MovieSnippet()
    {

    }
}
