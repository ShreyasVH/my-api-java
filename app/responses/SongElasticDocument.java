package responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongElasticDocument
{
    public Long id;
    public String name;
    public Long size;
    public Long movieId;
    public String movieName;
    public Integer movieYear;
    public Long movieReleaseDate;
    private String movieImageUrl;
    private List<Long> actorIds;
    private List<String> actorNames;
    private List<String> actorImageUrls;
    private List<String> actorGenders;
    private List<Long> directorIds;
    private List<String> directorNames;
    private List<String> directorImageUrls;
    private List<String> directorGenders;
    private List<Long> singerIds;
    private List<String> singerNames;
    private List<String> singerImageUrls;
    private List<String> singerGenders;
    private List<Long> composerIds;
    private List<String> composerNames;
    private List<String> composerImageUrls;
    private List<String> composerGenders;
    private List<Long> lyricistIds;
    private List<String> lyricistNames;
    private List<String> lyricistImageUrls;
    private List<String> lyricistGenders;
    private Boolean active;
    private boolean obtained;
    private Long movieLanguageId;
    private String movieLanguageName;

    public SongElasticDocument(Song song)
    {
        this.id = song.getId();
        this.name = song.getName();
        this.size = song.getSize();
        this.movieId = song.getMovieId();
        this.active = song.getActive();
        this.obtained = song.isObtained();
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

    public void setSingers(List<Artist> singers)
    {
        this.singerIds = singers.stream().map(Artist::getId).collect(Collectors.toList());
        this.singerNames = singers.stream().map(Artist::getName).collect(Collectors.toList());
        this.singerImageUrls = singers.stream().map(Artist::getImageUrl).collect(Collectors.toList());
        this.singerGenders = singers.stream().map(Artist::getGender).collect(Collectors.toList());
    }

    public void setComposers(List<Artist> composers)
    {
        this.composerIds = composers.stream().map(Artist::getId).collect(Collectors.toList());
        this.composerNames = composers.stream().map(Artist::getName).collect(Collectors.toList());
        this.composerImageUrls = composers.stream().map(Artist::getImageUrl).collect(Collectors.toList());
        this.composerGenders = composers.stream().map(Artist::getGender).collect(Collectors.toList());
    }

    public void setLyricists(List<Artist> lyricists)
    {
        this.lyricistIds = lyricists.stream().map(Artist::getId).collect(Collectors.toList());
        this.lyricistNames = lyricists.stream().map(Artist::getName).collect(Collectors.toList());
        this.lyricistImageUrls = lyricists.stream().map(Artist::getImageUrl).collect(Collectors.toList());
        this.lyricistGenders = lyricists.stream().map(Artist::getGender).collect(Collectors.toList());
    }

    public void setLanguage(Language language)
    {
        this.movieLanguageId = language.getId();
        this.movieLanguageName = language.getName();
    }

    public void setMovie(MovieResponse movieResponse)
    {
        this.movieName = movieResponse.getName();
        this.movieReleaseDate = movieResponse.getReleaseDate();
        this.movieImageUrl = movieResponse.getImageUrl();
        this.setActors(movieResponse.getActors());
        this.setDirectors(movieResponse.getDirectors());
        this.setLanguage(movieResponse.getLanguage());
    }
}
