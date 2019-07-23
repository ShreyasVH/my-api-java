package myapi.controllers;

import myapi.utils.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by shreyasvh on 7/29/17.
 */
public class TestController extends Controller {

    public static Result notFoundA()
    {
        return notFound();
    }

    public static Result notFoundHTML()
    {
        return notFound("<h1>Page Not Found</h1>").as("text/html");
    }

    public static Result redirectPage()
    {
        return redirect("/");
    }

    public static Result temporaryRedirectPage()
    {
        return temporaryRedirect("/");
    }

    public static F.Promise<Result> test()
    {
        return Logger.error("abcde").map(response -> ok(Json.toJson(response)));
    }
}
