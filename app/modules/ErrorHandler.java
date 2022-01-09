package modules;

import exceptions.MyException;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;

@Singleton
public class ErrorHandler implements HttpErrorHandler {
    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {
        return CompletableFuture.completedFuture(Results.status(statusCode, "A client error occurred: " + message));
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable exception) {
        Integer httpsStatusCode = 500;
        String content = exception.getMessage();
        Integer errorCode = 5000;

        if((exception instanceof MyException) || (exception.getCause() instanceof MyException))
        {
            MyException myException;
            if(exception instanceof MyException)
            {
                myException = (MyException) exception;
            }
            else
            {
                myException = (MyException) exception.getCause();
            }

            httpsStatusCode = myException.getHttpStatusCode();
            content = myException.getDescription();
            errorCode = myException.getCode();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", errorCode);
        response.put("description", content);

        return CompletableFuture.completedFuture(Results.status(httpsStatusCode, Json.toJson(response)));
    }
}
