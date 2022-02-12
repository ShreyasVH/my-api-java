package services.impl;

import enums.ErrorCode;
import exceptions.ConflictException;
import exceptions.NotFoundException;
import io.ebean.SqlRow;
import com.google.inject.Inject;
import models.Language;
import models.Movie;
import models.MovieActorMap;
import models.MovieDirectorMap;
import org.springframework.util.StringUtils;
import repositories.MovieRepository;
import requests.FilterRequest;
import requests.MovieRequest;
import responses.FilterResponse;
import responses.MovieResponse;
import services.ArtistService;
import services.FormatService;
import services.LanguageService;
import services.MovieService;
import utils.Utils;

import java.util.ArrayList;
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

        if(null == actorIds)
        {
            actorIds = this.movieRepository.getActorMaps(movie.getId()).stream().map(MovieActorMap::getActorId).collect(Collectors.toList());
        }
        movieResponse.setActors(this.artistService.get(actorIds));

        if(null == directorIds)
        {
            directorIds = this.movieRepository.getDirectorMaps(movie.getId()).stream().map(MovieDirectorMap::getDirectorId).collect(Collectors.toList());
        }
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

    @Override
    public MovieResponse update(Long id, MovieRequest request)
    {
        Movie existingMovie = this.movieRepository.get(id);
        if(null == existingMovie)
        {
            throw new NotFoundException("Movie");
        }

        if((StringUtils.hasText(request.getName()) && !request.getName().equals(existingMovie.getName())) ||
                (null != request.getYear() && !request.getYear().equals(existingMovie.getYear())) ||
                (null != request.getLanguageId() && !request.getLanguageId().equals(existingMovie.getLanguageId())))
        {
            String name = ((StringUtils.hasText(request.getName())) ? request.getName() : existingMovie.getName());
            Long languageId = ((null != request.getLanguageId()) ? request.getLanguageId() : existingMovie.getLanguageId());
            Integer year = ((null != request.getYear()) ? request.getYear() : existingMovie.getYear());

            Movie duplicateMovie = this.movieRepository.get(name, languageId, year);
            if(null != duplicateMovie)
            {
                throw new ConflictException("Movie");
            }
        }

        boolean isUpdateRequired = false;
        if(StringUtils.hasText(request.getName()) && !request.getName().equals(existingMovie.getName()))
        {
            isUpdateRequired = true;
            existingMovie.setName(request.getName());
        }

        if(null != request.getSize() && !request.getSize().equals(existingMovie.getSize()))
        {
            isUpdateRequired = true;
            existingMovie.setSize(request.getSize());
        }

        if(null != request.getLanguageId() && !request.getLanguageId().equals(existingMovie.getLanguageId()))
        {
            isUpdateRequired = true;
            existingMovie.setLanguageId(request.getLanguageId());
        }

        if(null != request.getFormatId() && !request.getFormatId().equals(existingMovie.getFormatId()))
        {
            isUpdateRequired = true;
            existingMovie.setFormatId(request.getFormatId());
        }

        if(null != request.getSubtitles() && !request.getSubtitles().equals(existingMovie.getSubtitles()))
        {
            isUpdateRequired = true;
            existingMovie.setSubtitles(request.getSubtitles());
        }

        if(null != request.getSeenInTheatre() && !request.getSeenInTheatre().equals(existingMovie.getSeenInTheatre()))
        {
            isUpdateRequired = true;
            existingMovie.setSeenInTheatre(request.getSeenInTheatre());
        }

        if(StringUtils.hasText(request.getBasename()) && !request.getBasename().equals(existingMovie.getBasename()))
        {
            isUpdateRequired = true;
            existingMovie.setBasename(request.getBasename());
        }

        if(null != request.getYear() && !request.getYear().equals(existingMovie.getYear()))
        {
            isUpdateRequired = true;
            existingMovie.setYear(request.getYear());
        }

        if(StringUtils.hasText(request.getQuality()) && !request.getQuality().equals(existingMovie.getQuality()))
        {
            isUpdateRequired = true;
            existingMovie.setQuality(request.getQuality());
        }

        if(StringUtils.hasText(request.getImageUrl()) && !request.getImageUrl().equals(existingMovie.getImageUrl()))
        {
            isUpdateRequired = true;
            existingMovie.setImageUrl(request.getImageUrl());
        }

        if(null != request.getActors())
        {
            List<MovieActorMap> actorsToAdd = new ArrayList<>();
            List<MovieActorMap> actorsToDelete = new ArrayList<>();

            List<MovieActorMap> existingActorMaps = this.movieRepository.getActorMaps(id);
            List<String> existingActorIds = existingActorMaps.stream().map(MovieActorMap::getActorId).collect(Collectors.toList());

            for(String actorId: request.getActors())
            {
                if(!existingActorIds.contains(actorId))
                {
                    MovieActorMap movieActorMap = new MovieActorMap();
                    movieActorMap.setId(Utils.generateUniqueIdByParam("ma"));
                    movieActorMap.setMovieId(id);
                    movieActorMap.setActorId(actorId);

                    actorsToAdd.add(movieActorMap);
                }
            }

            for(MovieActorMap actorMap: existingActorMaps)
            {
                if(!request.getActors().contains(actorMap.getActorId()))
                {
                    actorsToDelete.add(actorMap);
                }
            }

            this.movieRepository.saveActorMaps(actorsToAdd);
            this.movieRepository.removeActorMaps(actorsToDelete);
        }

        if(null != request.getDirectors())
        {
            List<MovieDirectorMap> directorsToAdd = new ArrayList<>();
            List<MovieDirectorMap> directorsToDelete = new ArrayList<>();

            List<MovieDirectorMap> existingDirectorMaps = this.movieRepository.getDirectorMaps(id);
            List<String> existingDirectorIds = existingDirectorMaps.stream().map(MovieDirectorMap::getDirectorId).collect(Collectors.toList());

            for(String directorId: request.getDirectors())
            {
                if(!existingDirectorIds.contains(directorId))
                {
                    MovieDirectorMap movieDirectorMap = new MovieDirectorMap();
                    movieDirectorMap.setId(Utils.generateUniqueIdByParam("md"));
                    movieDirectorMap.setMovieId(id);
                    movieDirectorMap.setDirectorId(directorId);

                    directorsToAdd.add(movieDirectorMap);
                }
            }

            for(MovieDirectorMap directorMap: existingDirectorMaps)
            {
                if(!request.getDirectors().contains(directorMap.getDirectorId()))
                {
                    directorsToDelete.add(directorMap);
                }
            }

            this.movieRepository.saveDirectorMaps(directorsToAdd);
            this.movieRepository.removeDirectorMaps(directorsToDelete);
        }

        if(isUpdateRequired)
        {
            existingMovie = this.movieRepository.save(existingMovie);
        }

        return movieResponse(existingMovie, null, null);
    }
}
