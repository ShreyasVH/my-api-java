package myapi.controllers;

import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import myapi.exceptions.BadRequestException;
import myapi.exceptions.MyException;
import myapi.models.*;
import myapi.services.FormatService;
import myapi.services.LanguageService;
import myapi.services.MovieService;
import myapi.skeletons.requests.ActorsCombinationMoviesRequest;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.requests.MovieRequest;
import myapi.skeletons.requests.UpdateStatusRequest;
import myapi.skeletons.responses.FilterResponse;
import myapi.skeletons.responses.MovieSnippet;
import myapi.utils.Utils;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by shreyas.hande on 7/22/17.
 */
public class MoviesController extends BaseController
{
    private static final Logger.ALogger LOGGER = Logger.of(MoviesController.class);

    private final MovieService movieService;
    private final LanguageService languageService;
    private final FormatService formatService;

    @Inject
    public MoviesController(
            MovieService movieService,
            LanguageService languageService,
            FormatService formatService
    )
    {
        this.movieService = movieService;
        this.languageService = languageService;
        this.formatService = formatService;
    }

    public F.Promise<Result> getAllLanguages()
    {
        return F.Promise.promise(() -> {
            LOGGER.debug("Calling getAllLanguages");
            Map<String, Object> hashMap = new HashMap<>();
            List<Language> languageList = languageService.getAllLanguages();
            hashMap.put("languages", languageList);
            return ok(Json.toJson(hashMap));
        });
    }

    public F.Promise<Result> getLanguageById(Long id)
    {
        return F.Promise.promise(() -> {
            Language language = languageService.getLanguageById(id);
            return ok(Json.toJson(language));
        });
    }

    public F.Promise<Result> getLanguageByName(String name)
    {
        return F.Promise.promise(() -> {
            Language language = languageService.getLanguageByName(name);
            return ok(Json.toJson(language));
        });
    }

    public F.Promise<Result> getAllFormats()
    {
        return F.Promise.promise(() -> {
            Map<String, Object> hashMap = new HashMap<>();
            List<MovieFormat> formatList = formatService.getAllFormats();
            hashMap.put("formats", formatList);
            return ok(Json.toJson(hashMap));
        });
    }

    public F.Promise<Result> getFormatById(Long id)
    {
        return F.Promise.promise(() -> {
            MovieFormat format = formatService.getFormatById(id);
            return ok(Json.toJson(format));
        });
    }

    public F.Promise<Result> getFormatByName(String name)
    {
        return F.Promise.promise(() -> {
            MovieFormat format = formatService.getFormatByName(name);
            return ok(Json.toJson(format));
        });
    }

    public F.Promise<Result> getAllYears()
    {
        return F.Promise.promise(() -> {
            Map<String, Object> hashMap = new HashMap<>();
            List<SqlRow> years = movieService.getAllYears();
            hashMap.put("years", years);
            return ok(Json.toJson(hashMap));
        });
    }

    public F.Promise<Result> getMovieById(Long id)
    {
        return F.Promise.promise(() -> {
            MovieSnippet movieSnippet = movieService.getMovieById(id);
            return ok(Json.toJson(movieSnippet));
        });
    }

    public F.Promise<Result> getMoviesCountByLanguage(Long languageId)
    {
        return F.Promise.promise(() -> {
            HashMap<String, Integer> hashMap = new HashMap<>();
            Long movieCount = movieService.getMovieCount(languageId);
            hashMap.put("movie_count", movieCount.intValue());
            return ok(Json.toJson(hashMap));
        });
    }

    public F.Promise<Result> dashboard()
    {
        return F.Promise.promise(() -> {
            List<SqlRow> dbList = movieService.getDashboard();
            return ok(Json.toJson(dbList));
        });
    }

    public F.Promise<Result> getActorsCombinationMovies() throws MyException
    {
        return F.Promise.promise(new F.Function0<Result>() {
            ActorsCombinationMoviesRequest request = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), ActorsCombinationMoviesRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST, ex);
                }
                List<MovieSnippet> movies = movieService.getMoviesWithActorCombination(request);
                return ok(Json.toJson(movies));
            }
        });
    }

    public F.Promise<Result> addMovie()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            MovieRequest request = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), MovieRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST, ex);
                }
                MovieSnippet movieSnippet = movieService.createMovie(request);
                return ok(Json.toJson(movieSnippet));
            }
        });
    }

    public F.Promise<Result> editMovie()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            MovieRequest request = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), MovieRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST, ex);
                }
                MovieSnippet movieSnippet = movieService.editMovie(request);
                return ok(Json.toJson(movieSnippet));
            }
        });
    }

    public F.Promise<Result> reIndexFromDB()
    {
        return F.Promise.promise(() -> {
            GenericResponse response = movieService.reIndexMoviesFromDB();
            return ok(Json.toJson(response));
        });
    }

    public F.Promise<Result> getMoviesWithFilter()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            FilterRequest request = null;
            @Override
            public Result apply() throws Exception {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), FilterRequest.class);
                }
                catch (Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST, ex);
                }
                FilterResponse response = movieService.getMoviesWithFilter(request);
                Map finalResponse = new HashMap();
                finalResponse.put("movies", response.getDocList());
                finalResponse.put("totalCount", response.getTotalCount());
                finalResponse.put("offset", response.getOffset());
                return ok(Json.toJson(finalResponse));
            }
        });
    }

    public F.Promise<Result> updateMovieStatus()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            UpdateStatusRequest request = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), UpdateStatusRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST, ex);
                }
                Boolean isSuccess = movieService.updateMovieStatus(request);
                return ok(Json.toJson(Utils.getResponseFromBooleanFlag(isSuccess, RequestType.UPDATE)));
            }
        });
    }

    public F.Promise<Result> getMoviesByKeyword(String keyword)
    {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply()
            {
                List<MovieSnippet> movies = movieService.getMoviesByKeyword(keyword);
                return ok(Json.toJson(movies));
            }
        });
    }

    public F.Promise<Result> indexMovie(Long id)
    {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Exception
            {
                GenericResponse response = movieService.indexMovie(id);
                return ok(Json.toJson(response));
            }
        });
    }
}
