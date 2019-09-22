import myapi.binding.ServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import myapi.exceptions.MyException;
import myapi.utils.Logger;
import play.Application;
import play.GlobalSettings;
import play.libs.F;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import myapi.skeletons.responses.Response;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class Global extends GlobalSettings
{
    private Injector injector;

    private class ActionWrapper extends Action.Simple {
        public ActionWrapper(Action<?> action)
        {
            this.delegate = action;
        }

        @Override
        public F.Promise<Result> call(Http.Context ctx) throws java.lang.Throwable
        {
            F.Promise<Result> result = this.delegate.call(ctx);
            Http.Response response = ctx.response();
            response.setHeader("Access-Control-Allow-Origin", "http://my-site-react.herokuapp.com");
            return result;
        }
    }

    @Override
    public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod)
    {
        return new ActionWrapper(super.onRequest(request, actionMethod));
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t)
    {
        Logger.error("[onError]: Exception thrown. Message: " + t.getMessage() + ". Cause: " + t.getCause() + ". Trace: " + Json.toJson(t.getStackTrace()));
        Throwable cause = t.getCause();
        Result response = Results.internalServerError(Json.toJson(Response.getErrorResponse(cause)));
        if(cause instanceof MyException) {
            MyException ex = (MyException) t.getCause();
            response = Results.status(ex.getHttpResponseCode(), Json.toJson(cause).get("responseCode"));
        }
        return F.Promise.<Result>pure(response);
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return injector.getInstance(controllerClass);
    }

    @Override
    public void onStart(Application application)
    {
        injector = Guice.createInjector(new ServiceModule());
    }
}
