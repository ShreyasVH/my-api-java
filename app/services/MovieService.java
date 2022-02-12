package services;

import io.ebean.SqlRow;
import models.Movie;
import requests.FilterRequest;
import requests.MovieRequest;
import responses.FilterResponse;
import responses.MovieResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public interface MovieService
{
    List<SqlRow> getDashboard();

    FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest);

    MovieResponse get(Long id);

    MovieResponse update(Long id, MovieRequest request);

    MovieResponse add(MovieRequest request);
}
