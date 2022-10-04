package responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Artist;
import models.Format;
import models.Language;
import models.Movie;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieElasticDocument
{
    public Long id;
    public String name;
    public Long languageId;
    public String languageName;
    public Long size;
    public Long formatId;
    public String formatName;
    public String quality;
    public Integer year;
    public Boolean subtitles;
    public Boolean seenInTheatre;
    public String basename;
    private String imageUrl;
    private List<Long> actorIds;
    private List<String> actorNames;
    private List<String> actorImageUrls;
    private List<String> actorGenders;
    private List<Long> directorIds;
    private List<String> directorNames;
    private List<String> directorImageUrls;
    private List<String> directorGenders;
    private Boolean active;

    public MovieElasticDocument(Movie movie)
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

    public void setLanguage(Language language)
    {
        this.languageId = language.getId();
        this.languageName = language.getName();
    }

    public void setFormat(Format format)
    {
        this.formatId = format.getId();
        this.formatName = format.getName();
    }

    public void setActors(List<Artist> actors)
    {
        this.actorIds = actors.stream().map(Artist::getId).collect(Collectors.toList());
        this.actorNames = actors.stream().map(Artist::getName).collect(Collectors.toList());
        this.actorImageUrls = actors.stream().map(Artist::getImageUrl).collect(Collectors.toList());
        this.actorGenders = actors.stream().map(Artist::getGender).collect(Collectors.toList());
    }

    public void setDirectors(List<Artist> directors)
    {
        this.directorIds = directors.stream().map(Artist::getId).collect(Collectors.toList());
        this.directorNames = directors.stream().map(Artist::getName).collect(Collectors.toList());
        this.directorImageUrls = directors.stream().map(Artist::getImageUrl).collect(Collectors.toList());
        this.directorGenders = directors.stream().map(Artist::getGender).collect(Collectors.toList());
    }
}
