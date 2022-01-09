package controllers;

import com.google.inject.Inject;
import services.MovieService;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;
import play.mvc.Result;

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
                .supplyAsync(() -> movieService.getDashboard())
                .thenApplyAsync(dbList -> ok(Json.toJson(dbList)), this.httpExecutionContext.current());
    }
}
