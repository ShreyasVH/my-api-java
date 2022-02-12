package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.ArtistRequest;
import services.ArtistService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public class ArtistsController extends BaseController
{
    private final ArtistService artistService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public ArtistsController
    (
        ArtistService artistService,
        HttpExecutionContext httpExecutionContext
    )
    {
        this.artistService = artistService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> addArtist(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            ArtistRequest artistRequest;
            try
            {
                artistRequest = Utils.convertObject(request.body().asJson(), ArtistRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }
            return this.artistService.add(artistRequest);
        }, this.httpExecutionContext.current())
        .thenApplyAsync(artist -> ok(Json.toJson(artist)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> get(String id)
    {
        return CompletableFuture
                .supplyAsync(() -> this.artistService.get(id), this.httpExecutionContext.current())
                .thenApplyAsync(artist -> ok(Json.toJson(artist)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> update(String id, Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            ArtistRequest artistRequest;
            try
            {
                artistRequest = Utils.convertObject(request.body().asJson(), ArtistRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }
            return this.artistService.update(id, artistRequest);
        }, this.httpExecutionContext.current())
                .thenApplyAsync(artist -> ok(Json.toJson(artist)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getAll(int offset, int count)
    {
        return CompletableFuture
                .supplyAsync(() -> this.artistService.get(offset, count), this.httpExecutionContext.current())
                .thenApplyAsync(response -> ok(Json.toJson(response)), this.httpExecutionContext.current());
    }
}
