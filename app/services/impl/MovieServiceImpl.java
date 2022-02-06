package services.impl;

import exceptions.NotFoundException;
import io.ebean.SqlRow;
import com.google.inject.Inject;
import models.Language;
import models.Movie;
import models.MovieActorMap;
import models.MovieDirectorMap;
import repositories.MovieRepository;
import requests.FilterRequest;
import responses.FilterResponse;
import responses.MovieResponse;
import services.ArtistService;
import services.FormatService;
import services.LanguageService;
import services.MovieService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final LanguageService languageService;
    private final FormatService formatService;
    private final ArtistService artistService;

    @Inject
    public MovieServiceImpl
    (
        MovieRepository movieRepository,

        LanguageService languageService,
        FormatService formatService,
        ArtistService artistService
    )
    {
        this.movieRepository = movieRepository;

        this.languageService = languageService;
        this.formatService = formatService;
        this.artistService = artistService;
    }

    @Override
    public List<SqlRow> getDashboard()
    {
        return movieRepository.getDashboard();
    }

    @Override
    public FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest)
    {
        return movieRepository.filter(filterRequest);
    }

    private MovieResponse movieResponse(Movie movie, List<String> actorIds, List<String> directorIds)
    {
        MovieResponse movieResponse = new MovieResponse(movie);
        movieResponse.setLanguage(this.languageService.get(movie.getLanguageId()));
        movieResponse.setFormat(this.formatService.get(movie.getFormatId()));
        movieResponse.setActors(this.artistService.get(actorIds));
        movieResponse.setDirectors(this.artistService.get(directorIds));

        return movieResponse;
    }

    @Override
    public MovieResponse get(Long id)
    {
        Movie movie = this.movieRepository.get(id);
        if(null == movie)
        {
            throw new NotFoundException("Movie");
        }

        List<MovieActorMap> actorMaps = this.movieRepository.getActorMaps(id);
        List<String> actorIds = actorMaps.stream().map(MovieActorMap::getActorId).collect(Collectors.toList());

        List<MovieDirectorMap> directorMaps = this.movieRepository.getDirectorMaps(id);
        List<String> directorIds = directorMaps.stream().map(MovieDirectorMap::getDirectorId).collect(Collectors.toList());

        return movieResponse(movie, actorIds, directorIds);
    }
}
