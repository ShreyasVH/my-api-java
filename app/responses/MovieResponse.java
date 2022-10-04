package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Artist;
import models.Format;
import models.Language;
import models.Movie;

import java.util.ArrayList;
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
    private Boolean active;

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
        this.active = movie.getActive();
    }

    public MovieResponse(MovieElasticDocument movieElasticDocument)
    {
        this.id = movieElasticDocument.getId();
        this.name = movieElasticDocument.getName();
        this.size = movieElasticDocument.getSize();
        this.quality = movieElasticDocument.getQuality();
        this.year = movieElasticDocument.getYear();
        this.subtitles = movieElasticDocument.getSubtitles();
        this.seenInTheatre = movieElasticDocument.getSeenInTheatre();
        this.basename = movieElasticDocument.getBasename();
        this.imageUrl = movieElasticDocument.getImageUrl();
        this.language = new Language(movieElasticDocument.languageId, movieElasticDocument.languageName);
        this.format = new Format(movieElasticDocument.formatId, movieElasticDocument.formatName);
        this.active = movieElasticDocument.getActive();

        List<Artist> actors = new ArrayList<>();
        for(int i = 0; i < movieElasticDocument.getActorIds().size(); i++)
        {
            Artist actor = new Artist(
                movieElasticDocument.getActorIds().get(i),
                movieElasticDocument.getActorNames().get(i),
                movieElasticDocument.getActorGenders().get(i),
                movieElasticDocument.getActorImageUrls().get(i)
            );
            actors.add(actor);
        }
        this.actors = actors;

        List<Artist> directors = new ArrayList<>();
        for(int i = 0; i < movieElasticDocument.getDirectorIds().size(); i++)
        {
            Artist director = new Artist(
                movieElasticDocument.getDirectorIds().get(i),
                movieElasticDocument.getDirectorNames().get(i),
                movieElasticDocument.getDirectorGenders().get(i),
                movieElasticDocument.getDirectorImageUrls().get(i)
            );
            directors.add(director);
        }
        this.directors = directors;
    }
}
