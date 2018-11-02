import myapi.binding.ServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.ConfigFactory;
import myapi.exceptions.MyException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import myapi.skeletons.responses.Response;

import java.io.File;
import java.util.Collection;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class Global extends GlobalSettings
{
    private Injector injector;

    private final Logger.ALogger LOGGER = Logger.of(Global.class);

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {
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
