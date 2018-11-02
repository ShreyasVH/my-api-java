package myapi.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import myapi.constants.Constants;
import myapi.exceptions.MyException;
import myapi.exceptions.NotFoundException;
import myapi.models.MovieAttribute;
import myapi.models.Status;
import myapi.models.ValidationResponse;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.Logger;
import myapi.services.MovieIndexService;
import myapi.services.MovieService;
import myapi.services.elastic.ElasticService;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.responses.ElasticResponse;
import myapi.skeletons.responses.MovieSnippet;
import myapi.utils.Utils;

import java.net.URLDecoder;
import java.util.*;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class MovieIndexServiceImpl implements MovieIndexService {
    private final MovieService movieService;
    private final ElasticService elasticService;
    private final Logger.ALogger LOGGER = Logger.of(MovieIndexServiceImpl.class);

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

    public ElasticResponse getMovieElasticResponse(FilterRequest filterRequest)
    {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        Iterator filterIterator = filterRequest.getFilters().entrySet().iterator();
        List<SortBuilder> sortMapList = new ArrayList<>();
        while(filterIterator.hasNext())
        {
            Map.Entry entry = (Map.Entry) filterIterator.next();
            MovieAttribute movieAttribute = MovieAttribute.getMovieAttributeByName(entry.getKey().toString());
            if(Constants.FIELD_TYPE_NORMAL.equals(movieAttribute.getType()))
            {
                query.must(QueryBuilders.termsQuery(entry.getKey().toString(), (List) entry.getValue()));
            }
            else if(Constants.FIELD_TYPE_NESTED.equals(movieAttribute.getType()))
            {
                QueryBuilder nestedQuery = QueryBuilders.nestedQuery(movieAttribute.getNestedLevel(), QueryBuilders.termsQuery(movieAttribute.getNestedTerm(), (List) entry.getValue()),  ScoreMode.None);
                query.must(nestedQuery);
            }
            else if(Constants.FIELD_TYPE_RANGE.equals(movieAttribute.getType()))
            {
                List valueArray = (List) entry.getValue();
                if(valueArray.size() == 1)
                {
                    valueArray.add(valueArray.get(0));
                }

                Collections.sort(valueArray);
                query.must(QueryBuilders.rangeQuery(movieAttribute.getFieldName()).from(valueArray.get(0), true).to(valueArray.get(1), true));
            }
        }

        for (Object o : filterRequest.getSortMap().entrySet())
        {
            Map.Entry entry = (Map.Entry) o;
            MovieAttribute movieAttribute = MovieAttribute.getMovieAttributeByName(entry.getKey().toString());
            FieldSortBuilder sortMap = null;
            String sortKey = Constants.SORT_KEY_ELASTIC;
            if (Constants.FIELD_TYPE_NORMAL.equals(movieAttribute.getType()) || Constants.FIELD_TYPE_RANGE.equals(movieAttribute.getType())) {
                sortKey = movieAttribute.getFieldName() + sortKey;
                sortMap = SortBuilders.fieldSort(sortKey);
            } else if (Constants.FIELD_TYPE_NESTED.equals(movieAttribute.getType())) {
                sortKey = movieAttribute.getNestedTerm() + sortKey;
                sortMap = SortBuilders.fieldSort(sortKey);
                sortMap.setNestedPath(movieAttribute.getNestedLevel());
            }
            sortMap.order((SortOrder) entry.getValue());
            sortMapList.add(sortMap);
        }

        if(!filterRequest.getIncludeDeleted())
        {
            query.mustNot(QueryBuilders.termQuery(MovieAttribute.STATUS.getFieldName(), Status.DELETED.toString()));
        }
        return elasticService.executeElasticRequest("movie", "movie", query, false, filterRequest.getOffset(), filterRequest.getCount(), sortMapList, MovieSnippet.class);
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
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        for(String actorId : actorIds)
        {
            QueryBuilder nestedQuery = QueryBuilders.nestedQuery(MovieAttribute.ACTORS.getNestedLevel(), QueryBuilders.termQuery(MovieAttribute.ACTORS.getNestedTerm(), actorId), ScoreMode.Min);
            finalQuery.must(nestedQuery);
        }
        finalQuery.mustNot(QueryBuilders.termQuery(MovieAttribute.STATUS.getFieldName(), Status.DELETED.toString()));
        List<SortBuilder> sortMapList = new ArrayList<>();
        sortMapList.add(SortBuilders.fieldSort(MovieAttribute.YEAR.getFieldName() + Constants.SORT_KEY_ELASTIC).order(SortOrder.DESC));
        sortMapList.add(SortBuilders.fieldSort(MovieAttribute.ID.getFieldName() + Constants.SORT_KEY_ELASTIC).order(SortOrder.DESC));
        return elasticService.executeElasticRequest("movie", "movie", finalQuery, false, Constants.DEFAULT_ELASTIC_OFFSET, Constants.DEFAULT_ELASTIC_COUNT, sortMapList, MovieSnippet.class).getDocList();
    }

    @Override
    public List<MovieSnippet> getMoviesByKeyword(String keyword)
    {
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
        List<SortBuilder> sortMapList = new ArrayList<>();
        sortMapList.add(SortBuilders.fieldSort(MovieAttribute.NAME.getFieldName() + Constants.SORT_KEY_ELASTIC).order(SortOrder.ASC));
        sortMapList.add(SortBuilders.fieldSort(MovieAttribute.ID.getFieldName() + Constants.SORT_KEY_ELASTIC).order(SortOrder.ASC));
        return elasticService.executeElasticRequest("movie", "movie", finalQuery, false, Constants.DEFAULT_ELASTIC_OFFSET, Constants.DEFAULT_ELASTIC_COUNT, sortMapList, MovieSnippet.class).getDocList();
    }

    @Override
    public Boolean reIndexMoviesFromDB() throws MyException
    {
        List<MovieSnippet> movies = movieService.getAllMoviesFromDB();
        Boolean isCompleteSuccess = true;
        LOGGER.debug("Reindexing " + movies.size() + " movies");
        Long counter = 1L;
        for(MovieSnippet movieSnippet : movies)
        {
            LOGGER.debug("Indexing movie " + counter.toString() + "/" + movies.size());
            Boolean isSuccess = indexMovie(movieSnippet);
            if(!isSuccess)
            {
                LOGGER.error("[reIndexMoviesFromDB] : Failed to index movie. Id : " + movieSnippet.id);
            }
            isCompleteSuccess = (isCompleteSuccess && isSuccess);
            counter++;
        }
        return isCompleteSuccess;
    }

    @Override
    public Boolean indexMovie(MovieSnippet movieSnippet)
    {
        return indexMovie(movieSnippet, false);
    }

    public Boolean indexMovie(MovieSnippet movieSnippet, Boolean isUpdateRequired)
    {
        Boolean isSuccess;
        if(isUpdateRequired)
        {
            isSuccess = elasticService.update("movie", "movie", movieSnippet.getId().toString(), Utils.convertObject(movieSnippet, JsonNode.class).toString());
        }
        else
        {
            isSuccess = elasticService.create("movie", "movie", movieSnippet.getId().toString(), Utils.convertObject(movieSnippet, JsonNode.class).toString());
        }
        return isSuccess;
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
                LOGGER.error("[indexMovie] Error while indexing movie.", ex);
            }
        });
    }
}
