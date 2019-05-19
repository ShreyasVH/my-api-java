package myapi.services.impl;

import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import myapi.dao.ArtistDao;
import myapi.dao.MovieArtistMapDao;
import myapi.dao.MovieDao;
import myapi.exceptions.BadRequestException;
import myapi.exceptions.MyException;
import myapi.exceptions.NotFoundException;
import myapi.models.*;
import myapi.services.FormatService;
import myapi.services.MovieService;
import myapi.skeletons.responses.ElasticResponse;
import myapi.skeletons.responses.FilterResponse;
import play.Logger;
import myapi.services.LanguageService;
import myapi.services.MovieIndexService;
import myapi.skeletons.requests.ActorsCombinationMoviesRequest;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.requests.MovieRequest;
import myapi.skeletons.requests.UpdateStatusRequest;
import myapi.skeletons.responses.MovieSnippet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class MovieServiceImpl implements MovieService {

    private final MovieDao movieDao;
    private final MovieArtistMapDao movieArtistMapDao;
    private final ArtistDao artistDao;
    private final MovieIndexService movieIndexService;
    private final LanguageService languageService;
    private final FormatService formatService;

    private static final Logger.ALogger LOGGER = Logger.of(MovieServiceImpl.class);

    @Inject
    public MovieServiceImpl(
            MovieDao movieDao,
            MovieArtistMapDao movieArtistMapDao,
            ArtistDao artistDao,
            MovieIndexService movieIndexService,
            LanguageService languageService,
            FormatService formatService
    )
    {
        this.movieDao = movieDao;
        this.movieArtistMapDao = movieArtistMapDao;
        this.artistDao = artistDao;
        this.movieIndexService = movieIndexService;
        this.languageService = languageService;
        this.formatService = formatService;
    }

    @Override
    public List<MovieSnippet> getAllMoviesFromDB() throws MyException
    {
        List<Movie> movieList = movieDao.getAllMovies();
        List<MovieSnippet> movies = new ArrayList<>();
        for(Movie movie : movieList)
        {
            MovieSnippet movieSnippet = movieSnippet(movie);
            movies.add(movieSnippet);
        }
        return movies;
    }

    @Override
    public MovieSnippet movieSnippet(Movie movie) throws MyException
    {
        MovieSnippet movieSnippet = new MovieSnippet(movie);
        List<MovieActorMap> actorMaps = movieArtistMapDao.getActorMapsForMovie(movie.getId());
        List<MovieDirectorMap> directorMaps = movieArtistMapDao.getDirectorMapsForMovie(movie.getId());
        List<Artist> actors = new ArrayList<>();
        List<Artist> directors = new ArrayList<>();
        for(MovieActorMap map : actorMaps)
        {
            Artist actor = artistDao.getArtistById(map.getActorId());
            if(null != actor)
            {
                actors.add(actor);
            }
        }
        movieSnippet.setActors(actors);

        for(MovieDirectorMap map : directorMaps)
        {
            Artist director = artistDao.getArtistById(map.getDirectorId());
            if(null != director)
            {
                directors.add(director);
            }
        }
        movieSnippet.setDirectors(directors);

        movieSnippet.setLanguage(languageService.getLanguageById(movie.getLanguageId()));

        movieSnippet.setFormat(formatService.getFormatById(movie.getFormatId()));

        return movieSnippet;
    }

    @Override
    public MovieSnippet getMovieById(Long id) throws MyException
    {
        return movieIndexService.getMovieById(id);
    }

    @Override
    public Movie getMovieFromDB(Long id) throws MyException
    {
        Movie movie = movieDao.getMovieById(id);
        if(null == movie)
        {
            throw new NotFoundException(ValidationResponse.MOVIE_NOT_FOUND);
        }
        return movie;
    }

    @Override
    public Boolean updateMovieStatus(Long id, Status status) throws MyException
    {
        Movie movie = movieDao.getMovieById(id);
        if(null == movie)
        {
            throw new NotFoundException(ValidationResponse.MOVIE_NOT_FOUND);
        }
        Boolean isSuccess;
        if(movie.getStatus().equals(status))
        {
            isSuccess = true;
        }
        else
        {
            movie.setStatus(status);
            isSuccess = movieDao.saveMovie(movie);
            if(isSuccess)
            {
                movieIndexService.indexMovieAsThread(movieSnippet(movie), true);
            }
        }
        return isSuccess;
    }

    @Override
    public Boolean updateMovieStatus(UpdateStatusRequest request) throws MyException
    {
        return updateMovieStatus(Long.valueOf((Integer) request.getId()), request.getStatus());
    }

    @Override
    public MovieSnippet editMovie(MovieRequest request) throws MyException
    {
        Movie movie = movieDao.getMovieById(request.getId());
        MovieSnippet movieSnippet = null;
        if(null == movie)
        {
            throw new NotFoundException(ValidationResponse.MOVIE_NOT_FOUND);
        }

        movie = createMovieFromMovieRequest(request, movie);
        Boolean isSuccess = movieDao.saveMovie(movie);
        if(isSuccess)
        {
            movieArtistMapDao.removeActorsFromMovie(movie.getId());
            movieArtistMapDao.saveActorsForMovie(movie.getId(), request.getActorIds());
            movieArtistMapDao.removeDirectorsFromMovie(movie.getId());
            movieArtistMapDao.saveDirectorsForMovie(movie.getId(), request.getDirectorIds());
            movieSnippet = movieSnippet(movie);
            movieIndexService.indexMovieAsThread(movieSnippet, true);
        }
        return movieSnippet;
    }

    @Override
    public Movie createMovieFromMovieRequest(MovieRequest request) {
        return createMovieFromMovieRequest(request, new Movie());
    }

    @Override
    public Movie createMovieFromMovieRequest(MovieRequest request, Movie movie)
    {
        if(null != request.getName())
        {
            movie.setName(request.getName());
        }

        if(null != request.getLanguageId())
        {
            movie.setLanguageId(request.getLanguageId());
        }

        if(null != request.getSize())
        {
            movie.setSize(request.getSize());
        }

        if(null != request.getFormatId())
        {
            movie.setFormatId(request.getFormatId());
        }

        if(null != request.getQuality())
        {
            movie.setQuality(request.getQuality());
        }

        if(null != request.getYear())
        {
            movie.setYear(request.getYear());
        }

        if(null != request.getSubtitles())
        {
            movie.setSubtitles(request.getSubtitles());
        }

        if(null != request.getSeenInTheatre())
        {
            movie.setSeenInTheatre(request.getSeenInTheatre());
        }

        if(null != request.getBasename())
        {
            movie.setBasename(request.getBasename());
        }

        return movie;
    }

    @Override
    public MovieSnippet createMovie(MovieRequest request) throws MyException
    {
        MovieSnippet movieSnippet = null;
        List<MovieSnippet> movieList = movieIndexService.getMovies(request.getName().toLowerCase(), request.getYear(), request.getLanguageId());
        if(!movieList.isEmpty())
        {
            throw new BadRequestException(ValidationResponse.DUPLICATE_ENTRY);
        }
        Movie movie = createMovieFromMovieRequest(request);
        Boolean isSuccess = movieDao.saveMovie(movie);
        if(isSuccess)
        {
            movieArtistMapDao.saveActorsForMovie(movie.getId(), request.getActorIds());
            movieArtistMapDao.saveDirectorsForMovie(movie.getId(), request.getDirectorIds());
            movieSnippet = movieSnippet(movie);
            movieIndexService.indexMovieAsThread(movieSnippet);
        }
        return movieSnippet;
    }

    @Override
    public Long getMovieCount(Long languageId)
    {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setFilters(Collections.singletonMap("language", Collections.singletonList(languageId.toString())));
        filterRequest.setCount(0);
        return movieIndexService.getMovieCountWithFilter(filterRequest);
    }

    @Override
    public List<MovieSnippet> getMoviesWithActorCombination(ActorsCombinationMoviesRequest request) throws MyException
    {
        return movieIndexService.getMoviesWithActorCombination(request.getActorIds());
    }

    @Override
    public List<SqlRow> getAllYears()
    {
        return movieDao.getAllYears();
    }

    @Override
    public List<SqlRow> getDashboard()
    {
        return movieDao.getDashboard();
    }

    @Override
    public FilterResponse getMoviesWithFilter(FilterRequest request)
    {
        ElasticResponse elasticResponse = movieIndexService.getMoviesWithFilter(request);
        FilterResponse filterResponse = new FilterResponse(elasticResponse);
        filterResponse.setOffset(Math.min((long) (request.getOffset() + request.getCount()), elasticResponse.getTotalCount()));
        return filterResponse;
    }

    @Override
    public GenericResponse reIndexMoviesFromDB() throws MyException
    {
        Boolean isSuccess = movieIndexService.reIndexMoviesFromDB();
        GenericResponse response = new GenericResponse();
        if(isSuccess)
        {
            response.setType(ResponseType.SUCCESS);
            response.setDescription(SuccessResponse.UPDATE_SUCCESS.getDescription());
            response.setCode(SuccessResponse.UPDATE_SUCCESS.getCode());
        }
        else
        {
            response.setType(ResponseType.ERROR);
            response.setCode(ErrorResponse.API_FAILED.getCode());
            response.setDescription(ErrorResponse.API_FAILED.getDescription());
        }
        return response;
    }

    @Override
    public List<MovieSnippet> getMoviesByKeyword(String keyword)
    {
        return movieIndexService.getMoviesByKeyword(keyword);
    }

    @Override
    public GenericResponse indexMovie(Long id) throws MyException {
        Boolean isSuccess = movieIndexService.indexMovie(id);
        GenericResponse response = new GenericResponse();
        if(isSuccess)
        {
            response.setType(ResponseType.SUCCESS);
            response.setDescription(SuccessResponse.UPDATE_SUCCESS.getDescription());
            response.setCode(SuccessResponse.UPDATE_SUCCESS.getCode());
        }
        else
        {
            response.setType(ResponseType.ERROR);
            response.setCode(ErrorResponse.API_FAILED.getCode());
            response.setDescription(ErrorResponse.API_FAILED.getDescription());
        }
        return response;
    }
}
