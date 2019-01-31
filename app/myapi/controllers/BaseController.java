package myapi.controllers;

import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public class BaseController extends Controller
{
    public F.Promise<Result> preflight(String all)
    {
        return F.Promise.promise(() -> {
            response().setHeader("Access-Control-Allow-Origin", "*");
            response().setHeader("Allow", "*");
            response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
            response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, Access-Control-Allow-Origin");
            return ok();
        });
    }
}
