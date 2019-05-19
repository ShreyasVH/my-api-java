package myapi.services;

import myapi.exceptions.MyException;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.responses.ElasticResponse;
import myapi.skeletons.responses.MovieSnippet;

import java.util.List;

/**
 * Created by shreyas.hande on 12/5/17.
 */
public interface MovieIndexService
{
    MovieSnippet getMovieById(Long movieId) throws MyException;

    List<MovieSnippet> getMovies(String name, Integer year, Long languageId);

    ElasticResponse getMoviesWithFilter(FilterRequest filterRequest);

    Boolean reIndexMoviesFromDB() throws MyException;

    Boolean indexMovie(Long id) throws MyException;

    Boolean indexMovie(MovieSnippet movieSnippet);

    Boolean indexMovie(MovieSnippet movieSnippet, Boolean isUpdateRequired);

    void indexMovieAsThread(MovieSnippet movieSnippet);

    void indexMovieAsThread(MovieSnippet movieSnippet, Boolean isUpdateRequired);

    Long getMovieCountWithFilter(FilterRequest filterRequest);

    List<MovieSnippet> getMoviesWithActorCombination(List<String> actorIds);

    List<MovieSnippet> getMoviesByKeyword(String keyword);
}
