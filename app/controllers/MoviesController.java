package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.mvc.Http;
import requests.FilterRequest;
import services.MovieService;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;
import play.mvc.Result;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * Created by shreyas.hande on 7/22/17.
 */
public class MoviesController extends BaseController
{
    private final MovieService movieService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public MoviesController(
        MovieService movieService,
        HttpExecutionContext httpExecutionContext
    )
    {
        this.movieService = movieService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> dashboard()
    {
        return CompletableFuture
                .supplyAsync(() -> movieService.getDashboard(), this.httpExecutionContext.current())
                .thenApplyAsync(dbList -> ok(Json.toJson(dbList)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> filter(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            FilterRequest filterRequest = null;
            try
            {
                filterRequest = Utils.convertObject(request.body().asJson(), FilterRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.movieService.filter(filterRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(response -> ok(Json.toJson(response)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture
                .supplyAsync(() -> movieService.get(id), this.httpExecutionContext.current())
                .thenApplyAsync(movie -> ok(Json.toJson(movie)), this.httpExecutionContext.current());
    }
}
