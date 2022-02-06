package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Artist;
import models.Format;
import models.Language;
import models.Movie;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieResponse
{
    public Long id;
    public String name;
    public Language language;
    public Long size;
    public Format format;
    public String quality;
    public Integer year;
    public Boolean subtitles;
    public Boolean seenInTheatre;
    public String basename;
    private String imageUrl;
    private List<Artist> actors;
    private List<Artist> directors;

    public MovieResponse(Movie movie)
    {
        this.id = movie.getId();
        this.name = movie.getName();
        this.size = movie.getSize();
        this.quality = movie.getQuality();
        this.year = movie.getYear();
        this.subtitles = movie.getSubtitles();
        this.seenInTheatre = movie.getSeenInTheatre();
        this.basename = movie.getBasename();
        this.imageUrl = movie.getImageUrl();
    }
}
