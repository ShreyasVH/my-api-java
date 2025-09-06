package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.mvc.Http;
import requests.FilterRequest;
import requests.SongRequest;
import responses.MovieResponse;
import services.MovieService;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;
import play.mvc.Result;
import services.SongService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class SongsController extends BaseController
{
    private final SongService songsService;
    private final MovieService movieService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public SongsController(
            SongService songsService,
            MovieService movieService,
            HttpExecutionContext httpExecutionContext
    )
    {
        this.songsService = songsService;
        this.movieService = movieService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> addSong(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            SongRequest songRequest;
            try
            {
                songRequest = Utils.convertObject(request.body().asJson(), SongRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            MovieResponse movie = movieService.get(songRequest.getMovieId());
            return this.songsService.add(songRequest, movie);
        }, this.httpExecutionContext.current()).thenApplyAsync(response -> created(Json.toJson(response)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> dashboard()
    {
        return CompletableFuture
                .supplyAsync(songsService::dashboard, this.httpExecutionContext.current())
                .thenApplyAsync(dbList -> ok(Json.toJson(dbList)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> filter(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            FilterRequest filterRequest;
            try
            {
                filterRequest = Utils.convertObject(request.body().asJson(), FilterRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.songsService.filter(filterRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(response -> ok(Json.toJson(response)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture
                .supplyAsync(() -> songsService.get(id), this.httpExecutionContext.current())
                .thenApplyAsync(song -> ok(Json.toJson(song)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> editSong(Long id, Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            SongRequest songRequest;
            try
            {
                songRequest = Utils.convertObject(request.body().asJson(), SongRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            MovieResponse movie = movieService.get(songRequest.getMovieId());
            return this.songsService.edit(id, songRequest, movie);
        }, this.httpExecutionContext.current()).thenApplyAsync(response -> created(Json.toJson(response)), this.httpExecutionContext.current());
    }
}
