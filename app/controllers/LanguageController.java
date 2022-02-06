package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import services.LanguageService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class LanguageController extends BaseController
{
    private final LanguageService languageService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public LanguageController
    (
        LanguageService languageService,
        HttpExecutionContext httpExecutionContext
    )
    {
        this.languageService = languageService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> getAll()
    {
        return CompletableFuture.supplyAsync(this.languageService::getAll, this.httpExecutionContext.current()).thenApplyAsync(languages -> ok(Json.toJson(languages)), this.httpExecutionContext.current());
    }
}
