package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import services.FormatService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FormatController extends BaseController
{
    private final FormatService formatService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public FormatController
    (
        FormatService formatService,
        HttpExecutionContext httpExecutionContext
    )
    {
        this.formatService = formatService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> getAll()
    {
        return CompletableFuture
                .supplyAsync(this.formatService::getAll, this.httpExecutionContext.current())
                .thenApplyAsync(formats -> ok(Json.toJson(formats)), this.httpExecutionContext.current());
    }
}
