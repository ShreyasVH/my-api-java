package services.impl;

import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.ConflictException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import com.google.inject.Inject;
import io.ebean.Transaction;
import models.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.StringUtils;
import repositories.MovieRepository;
import requests.FilterRequest;
import requests.MovieRequest;
import responses.FilterResponse;
import responses.MovieElasticDocument;
import responses.MovieResponse;
import services.*;
import utils.Utils;
import constants.Constants;

import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final LanguageService languageService;
    private final FormatService formatService;
    private final ArtistService artistService;
    private final ElasticService elasticService;

    @Inject
    public MovieServiceImpl
    (
        MovieRepository movieRepository,

        LanguageService languageService,
        FormatService formatService,
        ArtistService artistService,
        ElasticService elasticService
    )
    {
        this.movieRepository = movieRepository;

        this.languageService = languageService;
        this.formatService = formatService;
        this.artistService = artistService;
        this.elasticService = elasticService;
    }

    @Override
    public List<SqlRow> getDashboard()
    {
        return movieRepository.getDashboard();
    }

    @Override
    public FilterResponse<MovieElasticDocument> filter(FilterRequest request) {
        FilterResponse<MovieElasticDocument> response = this.elasticService.search(getElasticRequest(request), MovieElasticDocument.class);
        response.setOffset(request.getOffset());

        return response;
    }

    private SearchRequest getElasticRequest(FilterRequest filterRequest)
    {
        SearchRequest request = new SearchRequest(Constants.INDEX_NAME_MOVIES);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(filterRequest.getOffset());
        builder.size(filterRequest.getCount());

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        for(Map.Entry<String, List<String>> entry: filterRequest.getAndFilters().entrySet())
        {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();
            if (!valueList.isEmpty())
            {
                query.must(QueryBuilders.termsQuery(key, valueList));
            }
        }

        for(Map.Entry<String, Boolean> entry: filterRequest.getBooleanFilters().entrySet())
        {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            query.must(QueryBuilders.termQuery(key, value));
        }

        builder.query(query);

        Map<String, SortOrder> sortMap = filterRequest.getSortMap();
        for(Map.Entry<String, SortOrder> sortField: sortMap.entrySet())
        {
            String key = sortField.getKey();
            SortOrder order = sortField.getValue();

            String sortKey = (key + ".sort");
            builder.sort(sortKey, order);
        }

        if(!sortMap.containsKey("id"))
        {
            builder.sort("id.sort", SortOrder.ASC);
        }

        request.source(builder);
        return request;
    }

    private MovieElasticDocument movieElasticDocument(Movie movie, List<Long> actorIds, List<Long> directorIds)
    {
        MovieElasticDocument movieElasticDocument = new MovieElasticDocument(movie);
        movieElasticDocument.setLanguage(this.languageService.get(movie.getLanguageId()));
        if (movie.getFormatId() != null) {
            movieElasticDocument.setFormat(this.formatService.get(movie.getFormatId()));
        }

        List<Artist> actors = new ArrayList<>();
        if(null == actorIds)
        {
            actorIds = this.movieRepository.getActorMaps(movie.getId()).stream().map(MovieActorMap::getActorId).collect(Collectors.toList());
        }
        Map<Long, Artist> actorMap = this.artistService.get(actorIds).stream().collect(Collectors.toMap(artist -> artist.getId(), artist -> artist));
        for(Long actorId: actorIds) {
            if(actorMap.containsKey(actorId)) {
                actors.add(actorMap.get(actorId));
            }
        }

        movieElasticDocument.setActors(actors);

        if(null == directorIds)
        {
            directorIds = this.movieRepository.getDirectorMaps(movie.getId()).stream().map(MovieDirectorMap::getDirectorId).collect(Collectors.toList());
        }
        movieElasticDocument.setDirectors(this.artistService.get(directorIds));

        return movieElasticDocument;
    }

    @Override
    public MovieResponse get(Long id)
    {
        return new MovieResponse(this.elasticService.get(Constants.INDEX_NAME_MOVIES, id, MovieElasticDocument.class));
    }

    @Override
    public MovieResponse update(Long id, MovieRequest request)
    {
        request.validateForUpdate();

        Movie existingMovie = this.movieRepository.get(id);
        if(null == existingMovie)
        {
            throw new NotFoundException("Movie");
        }

        if(
            (StringUtils.hasText(request.getName()) && !request.getName().equals(existingMovie.getName())) ||
            (StringUtils.hasText(request.getReleaseDate()) && !Utils.parseDateString(request.getReleaseDate()).equals(existingMovie.getReleaseDate())) ||
            (null != request.getLanguageId() && !request.getLanguageId().equals(existingMovie.getLanguageId())))
        {
            String name = ((StringUtils.hasText(request.getName())) ? request.getName() : existingMovie.getName());
            Long languageId = ((null != request.getLanguageId()) ? request.getLanguageId() : existingMovie.getLanguageId());
            String releaseDate = ((StringUtils.hasText(request.getReleaseDate())) ? request.getReleaseDate() : Utils.formatDate(existingMovie.getReleaseDate()));

            Movie duplicateMovie = this.movieRepository.get(name, languageId, releaseDate);
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

        if(!Objects.equals(request.getSize(), existingMovie.getSize()))
        {
            isUpdateRequired = true;
            existingMovie.setSize(request.getSize());
        }

        if(null != request.getLanguageId() && !request.getLanguageId().equals(existingMovie.getLanguageId()))
        {
            isUpdateRequired = true;
            Language language = this.languageService.get(request.getLanguageId());
            if(null == language)
            {
                throw new NotFoundException("Language");
            }

            existingMovie.setLanguageId(request.getLanguageId());
        }

        if(!Objects.equals(request.getFormatId(), existingMovie.getFormatId()))
        {
            isUpdateRequired = true;
            if (null != request.getFormatId()) {
                Format format = this.formatService.get(request.getFormatId());
                if(null == format)
                {
                    throw new NotFoundException("Format");
                }
            }

            existingMovie.setFormatId(request.getFormatId());
        }

        if(!Objects.equals(request.getSubtitles(), existingMovie.getSubtitles()))
        {
            isUpdateRequired = true;
            existingMovie.setSubtitles(request.getSubtitles());
        }

        if(null != request.getSeenInTheatre() && !request.getSeenInTheatre().equals(existingMovie.getSeenInTheatre()))
        {
            isUpdateRequired = true;
            existingMovie.setSeenInTheatre(request.getSeenInTheatre());
        }

        if(!Objects.equals(request.getBasename(), existingMovie.getBasename()))
        {
            isUpdateRequired = true;
            existingMovie.setBasename(request.getBasename());
        }

        if(StringUtils.hasText(request.getReleaseDate()))
        {
            isUpdateRequired = true;
            existingMovie.setReleaseDate(Utils.parseDateString(request.getReleaseDate()));
        }

        if(!Objects.equals(request.getQuality(), existingMovie.getQuality()))
        {
            isUpdateRequired = true;
            existingMovie.setQuality(request.getQuality());
        }

        if(StringUtils.hasText(request.getImageUrl()) && !request.getImageUrl().equals(existingMovie.getImageUrl()))
        {
            isUpdateRequired = true;
            existingMovie.setImageUrl(request.getImageUrl());
        }

        if (request.isObtained() != existingMovie.isObtained()) {
            isUpdateRequired = true;
            existingMovie.setObtained(request.isObtained());
        }

        if(null != request.getActors())
        {
            List<Artist> actors = this.artistService.get(request.getActors());
            if(actors.size() != request.getActors().size())
            {
                throw new NotFoundException("Actor");
            }

            List<MovieActorMap> actorsToAdd = new ArrayList<>();
            List<MovieActorMap> actorsToDelete = new ArrayList<>();

            List<MovieActorMap> existingActorMaps = this.movieRepository.getActorMaps(id);
            List<Long> existingActorIds = existingActorMaps.stream().map(MovieActorMap::getActorId).collect(Collectors.toList());

            for(Long actorId: request.getActors())
            {
                if(!existingActorIds.contains(actorId))
                {
                    MovieActorMap movieActorMap = new MovieActorMap();
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
            List<Artist> directors = this.artistService.get(request.getDirectors());
            if(directors.size() != request.getDirectors().size())
            {
                throw new NotFoundException("Director");
            }

            List<MovieDirectorMap> directorsToAdd = new ArrayList<>();
            List<MovieDirectorMap> directorsToDelete = new ArrayList<>();

            List<MovieDirectorMap> existingDirectorMaps = this.movieRepository.getDirectorMaps(id);
            List<Long> existingDirectorIds = existingDirectorMaps.stream().map(MovieDirectorMap::getDirectorId).collect(Collectors.toList());

            for(Long directorId: request.getDirectors())
            {
                if(!existingDirectorIds.contains(directorId))
                {
                    MovieDirectorMap movieDirectorMap = new MovieDirectorMap();
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

        MovieElasticDocument movieElasticDocument = this.movieElasticDocument(existingMovie, null, null);
        if(isUpdateRequired)
        {
            existingMovie = this.movieRepository.save(existingMovie);
            CompletableFuture.supplyAsync(() -> this.elasticService.index(Constants.INDEX_NAME_MOVIES, id, movieElasticDocument));
        }

        return new MovieResponse(movieElasticDocument);
    }

    @Override
    public MovieResponse add(MovieRequest request)
    {
        request.validate();

        Movie existingMovie = this.movieRepository.get(request.getName(), request.getLanguageId(), request.getReleaseDate());
        if(null != existingMovie)
        {
            throw new ConflictException("Movie");
        }

        Movie movie = new Movie(request);
        movie.setActive(true);
        movie.setObtained(false);

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            Language language = this.languageService.get(request.getLanguageId());
            if(null == language)
            {
                throw new NotFoundException("Language");
            }

            movie = this.movieRepository.save(movie);

            List<Artist> actors = this.artistService.get(request.getActors());
            if(actors.size() != request.getActors().size())
            {
                throw new NotFoundException("Actor");
            }
            Movie finalMovie = movie;
            this.movieRepository.saveActorMaps(request.getActors().stream().map(actorId -> {
                MovieActorMap actorMap = new MovieActorMap();
                actorMap.setMovieId(finalMovie.getId());
                actorMap.setActorId(actorId);

                return actorMap;
            }).collect(Collectors.toList()));

            List<Artist> directors = this.artistService.get(request.getDirectors());
            if(directors.size() != request.getDirectors().size())
            {
                throw new NotFoundException("Director");
            }
            this.movieRepository.saveDirectorMaps(request.getDirectors().stream().map(directorId -> {
                MovieDirectorMap directorMap = new MovieDirectorMap();
                directorMap.setMovieId(finalMovie.getId());
                directorMap.setDirectorId(directorId);

                return directorMap;
            }).collect(Collectors.toList()));

            transaction.commit();

            MovieElasticDocument movieElasticDocument = this.movieElasticDocument(movie, request.getActors(), request.getDirectors());
            Long movieId = movie.getId();
            CompletableFuture.supplyAsync(() -> this.elasticService.index(Constants.INDEX_NAME_MOVIES, movieId, movieElasticDocument));
            return new MovieResponse(movieElasticDocument);
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Override
    public List<MovieResponse> getMoviesByKeyword(String keyword)
    {
        SearchRequest request = new SearchRequest(Constants.INDEX_NAME_MOVIES);

        SearchSourceBuilder builder = new SearchSourceBuilder();

        keyword = URLDecoder.decode(keyword);
        String[] words = keyword.split(" ");
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        for(String word : words)
        {
            word = word.toLowerCase();
            if(word.length() >= 2)
            {
                finalQuery.must(QueryBuilders.termQuery("name", word));
            }
        }
        finalQuery.must(QueryBuilders.termQuery("active", true));

        builder.query(finalQuery);

        builder.sort("name" + Constants.SORT_KEY_ELASTIC, SortOrder.ASC);
        builder.sort("id" + Constants.SORT_KEY_ELASTIC, SortOrder.ASC);

        request.source(builder);
        FilterResponse<MovieElasticDocument> response = elasticService.search(request, MovieElasticDocument.class);
        return response.getList().stream().map(MovieResponse::new).collect(Collectors.toList());
    }

    @Override
    public boolean indexMovie(Long id) {
        Movie existingMovie = this.movieRepository.get(id);
        MovieElasticDocument movieElasticDocument = this.movieElasticDocument(existingMovie, null, null);
        this.elasticService.index(Constants.INDEX_NAME_MOVIES, id, movieElasticDocument);

        return true;
    }


}
