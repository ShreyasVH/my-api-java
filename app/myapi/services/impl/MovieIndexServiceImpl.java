package myapi.services.impl;

import com.google.inject.Inject;
import myapi.constants.Constants;
import myapi.exceptions.MyException;
import myapi.exceptions.NotFoundException;
import myapi.models.Movie;
import myapi.models.MovieAttribute;
import myapi.models.Status;
import myapi.models.ValidationResponse;
import myapi.utils.Logger;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import myapi.services.MovieIndexService;
import myapi.services.MovieService;
import myapi.services.elastic.ElasticService;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.responses.ElasticResponse;
import myapi.skeletons.responses.MovieSnippet;
import myapi.utils.Utils;
import play.libs.Json;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class MovieIndexServiceImpl implements MovieIndexService {
    private final MovieService movieService;
    private final ElasticService elasticService;

    @Inject
    public MovieIndexServiceImpl(MovieService movieService, ElasticService elasticService)
    {
        this.movieService = movieService;
        this.elasticService = elasticService;
    }

    public MovieSnippet getMovieById(Long movieId) throws MyException
    {
        MovieSnippet movieSnippet;
        FilterRequest request = new FilterRequest();
        request.setFilters(Collections.singletonMap("id", Collections.singletonList(movieId.toString())));
        List<MovieSnippet> movieList = getMoviesWithFilter(request).getDocList();
        if(movieList.isEmpty())
        {
            throw new NotFoundException(ValidationResponse.MOVIE_NOT_FOUND);
        }
        else
        {
            movieSnippet = movieList.get(0);
        }
        return movieSnippet;
    }

    @Override
    public List<MovieSnippet> getMovies(String name, Integer year, Long languageId)
    {
        FilterRequest filterRequest = new FilterRequest();
        Map<String, List<String>> filters = new HashMap<>();
        filters.put(MovieAttribute.NAME.getFieldName(), Collections.singletonList(name));
        List<String> yearList = new ArrayList<>();
        yearList.add(year.toString());
        filters.put(MovieAttribute.YEAR.getFieldName(), yearList);
        filters.put(MovieAttribute.LANGUAGE.getFieldName(), Collections.singletonList(languageId.toString()));
        filterRequest.setFilters(filters);
        return getMoviesWithFilter(filterRequest).getDocList();
    }

    private ElasticResponse getMovieElasticResponse(FilterRequest filterRequest)
    {
        SearchRequest request = new SearchRequest("movies");

        Map<String, List<String>> filters = filterRequest.getFilters();

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(filterRequest.getOffset());
        builder.size(filterRequest.getCount());

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        if(!filters.isEmpty())
        {
            for(Map.Entry<String, List<String>> entry: filters.entrySet())
            {
                String key = entry.getKey();
                List<String> valueList = entry.getValue();
                if(!valueList.isEmpty())
                {
                    MovieAttribute movieAttribute = MovieAttribute.getMovieAttributeByName(key);
                    if(null != movieAttribute)
                    {
                        if(Constants.FIELD_TYPE_NORMAL.equals(movieAttribute.getType()))
                        {
                            query.must(QueryBuilders.termsQuery(key, valueList));
                        }
                        else if(Constants.FIELD_TYPE_NESTED.equals(movieAttribute.getType()))
                        {
                            QueryBuilder nestedQuery = QueryBuilders.nestedQuery(movieAttribute.getNestedLevel(), QueryBuilders.termsQuery(movieAttribute.getNestedTerm(), valueList),  ScoreMode.None);
                            query.must(nestedQuery);
                        }
                        else if(Constants.FIELD_TYPE_RANGE.equals(movieAttribute.getType()))
                        {

                            if(valueList.size() == 1)
                            {
                                valueList.add(valueList.get(0));
                            }

                            Collections.sort(valueList);
                            query.must(QueryBuilders.rangeQuery(movieAttribute.getFieldName()).from(valueList.get(0), true).to(valueList.get(1), true));
                        }
                    }
                }
            }
        }

        if(!filterRequest.getIncludeDeleted())
        {
            query.must(QueryBuilders.termQuery("status", Status.ENABLED.name()));
        }

        builder.query(query);

        Map<String, SortOrder> sortMap = filterRequest.getSortMap();
        for(Map.Entry<String, SortOrder> sortField: sortMap.entrySet())
        {
            String key = sortField.getKey();
            SortOrder order = sortField.getValue();

            MovieAttribute movieAttribute = MovieAttribute.getMovieAttributeByName(key);
            if(null != movieAttribute)
            {
                String sortKey = (key + ".sort");
                builder.sort(sortKey, order);
            }
        }

        if(!sortMap.containsKey("name"))
        {
            builder.sort("name.sort", SortOrder.ASC);
        }

        request.source(builder);
        return elasticService.search(request, MovieSnippet.class);
    }

    public ElasticResponse getMoviesWithFilter(FilterRequest filterRequest)
    {
        return getMovieElasticResponse(filterRequest);
    }

    public Long getMovieCountWithFilter(FilterRequest filterRequest)
    {
        return getMovieElasticResponse(filterRequest).getTotalCount();
    }

    @Override
    public List<MovieSnippet> getMoviesWithActorCombination(List<String> actorIds)
    {
        SearchRequest request = new SearchRequest("movies");

        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        for(String actorId : actorIds)
        {
            QueryBuilder nestedQuery = QueryBuilders.nestedQuery(MovieAttribute.ACTORS.getNestedLevel(), QueryBuilders.termQuery(MovieAttribute.ACTORS.getNestedTerm(), actorId), ScoreMode.Min);
            finalQuery.must(nestedQuery);
        }
        finalQuery.mustNot(QueryBuilders.termQuery(MovieAttribute.STATUS.getFieldName(), Status.DELETED.toString()));

        builder.query(finalQuery);

        builder.sort(MovieAttribute.YEAR.getFieldName() + Constants.SORT_KEY_ELASTIC, SortOrder.ASC);
        builder.sort(MovieAttribute.ID.getFieldName() + Constants.SORT_KEY_ELASTIC, SortOrder.ASC);

        request.source(builder);
        return elasticService.search(request, MovieSnippet.class).getDocList();
    }

    @Override
    public List<MovieSnippet> getMoviesByKeyword(String keyword)
    {
        SearchRequest request = new SearchRequest("movies");

        SearchSourceBuilder builder = new SearchSourceBuilder();

        keyword = URLDecoder.decode(keyword);
        String[] words = keyword.split(" ");
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        for(String word : words)
        {
            word = word.toLowerCase();
            if(word.length() >= 2)
            {
                finalQuery.must(QueryBuilders.termQuery(MovieAttribute.NAME.getFieldName(), word));
            }
        }
        finalQuery.mustNot(QueryBuilders.termQuery(MovieAttribute.STATUS.getFieldName(), Status.DELETED.toString()));

        builder.query(finalQuery);

        builder.sort(MovieAttribute.NAME.getFieldName() + Constants.SORT_KEY_ELASTIC, SortOrder.ASC);
        builder.sort(MovieAttribute.ID.getFieldName() + Constants.SORT_KEY_ELASTIC, SortOrder.ASC);

        request.source(builder);
        return elasticService.search(request, MovieSnippet.class).getDocList();
    }

    @Override
    public Boolean reIndexMoviesFromDB() throws MyException
    {
        List<MovieSnippet> movies = movieService.getAllMoviesFromDB();
        Boolean isCompleteSuccess = true;
        Logger.debug("Reindexing " + movies.size() + " movies");
        Long counter = 1L;
        for(MovieSnippet movieSnippet : movies)
        {
            Logger.debug("Indexing movie " + counter.toString() + "/" + movies.size());
            Boolean isSuccess = indexMovie(movieSnippet);
            if(!isSuccess)
            {
                Logger.error("[reIndexMoviesFromDB] : Failed to index movie. Id : " + movieSnippet.id);
            }
            isCompleteSuccess = (isCompleteSuccess && isSuccess);
            counter++;
        }
        return isCompleteSuccess;
    }

    @Override
    public Boolean indexMovie(Long id) throws MyException
    {
        Movie movie = movieService.getMovieFromDB(id);
        MovieSnippet movieSnippet = movieService.movieSnippet(movie);
        return indexMovie(movieSnippet);
    }

    @Override
    public Boolean indexMovie(MovieSnippet movieSnippet)
    {
        return indexMovie(movieSnippet, false);
    }

    public Boolean indexMovie(MovieSnippet movieSnippet, Boolean isUpdateRequired)
    {
        return elasticService.index("movies", movieSnippet.getId().toString(), movieSnippet);
    }

    public void indexMovieAsThread(MovieSnippet movieSnippet)
    {
        indexMovieAsThread(movieSnippet, false);
    }

    public void indexMovieAsThread(MovieSnippet movieSnippet, Boolean isUpdateRequired)
    {
        Utils.scheduleOnce(() -> {
            try
            {
                indexMovie(movieSnippet, isUpdateRequired);
            }
            catch(Exception ex)
            {
                Logger.error("[indexMovie] Error while indexing movie. Message: " + ex.getMessage() + ". Cause: " + ex.getCause() + ". Trace: " + Json.toJson(ex.getStackTrace()));
            }
        });
    }
}
