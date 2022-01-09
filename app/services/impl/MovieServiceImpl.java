package services.impl;

import io.ebean.SqlRow;
import com.google.inject.Inject;
import repositories.MovieRepository;
import requests.FilterRequest;
import responses.FilterResponse;
import services.MovieService;

import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Inject
    public MovieServiceImpl
    (
        MovieRepository movieRepository
    )
    {
        this.movieRepository = movieRepository;
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
}
