package myapi.services;

import com.avaje.ebean.SqlRow;
import myapi.exceptions.MyException;
import myapi.models.GenericResponse;
import myapi.models.Movie;
import myapi.models.Status;
import myapi.skeletons.requests.ActorsCombinationMoviesRequest;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.requests.MovieRequest;
import myapi.skeletons.requests.UpdateStatusRequest;
import myapi.skeletons.responses.FilterResponse;
import myapi.skeletons.responses.MovieSnippet;

import java.util.List;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public interface MovieService
{
    List<MovieSnippet> getAllMoviesFromDB() throws MyException;

    MovieSnippet movieSnippet(Movie movie) throws MyException;

    MovieSnippet getMovieById(Long id) throws MyException;

    Boolean updateMovieStatus(Long id, Status status) throws MyException;

    Boolean updateMovieStatus(UpdateStatusRequest request) throws MyException;

    MovieSnippet editMovie(MovieRequest request) throws MyException;

    Movie createMovieFromMovieRequest(MovieRequest request);

    Movie createMovieFromMovieRequest(MovieRequest request, Movie movie);

    MovieSnippet createMovie(MovieRequest request) throws MyException;

    Long getMovieCount(Long languageId);

    List<MovieSnippet> getMoviesWithActorCombination(ActorsCombinationMoviesRequest request) throws MyException;

    List<SqlRow> getAllYears();

    List<SqlRow> getDashboard();

    FilterResponse getMoviesWithFilter(FilterRequest request);

    GenericResponse reIndexMoviesFromDB() throws MyException;

    List<MovieSnippet> getMoviesByKeyword(String keyword);
}
