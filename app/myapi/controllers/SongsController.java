package myapi.controllers;

import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import myapi.exceptions.BadRequestException;
import myapi.models.GenericResponse;
import myapi.models.ValidationResponse;
import myapi.services.SongService;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.requests.SongRequest;
import myapi.skeletons.responses.SongSnippet;
import myapi.utils.Utils;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public class SongsController extends Controller
{
    private final SongService songService;

    @Inject
    public SongsController(
            SongService songService
    )
    {
        this.songService = songService;
    }

    public F.Promise<Result> dashboard()
    {
        return F.Promise.promise(() -> {
            List<SqlRow> dashboard = songService.dashboard();
            return ok(Json.toJson(dashboard));
        });
    }

    public F.Promise<Result> getSongById(String id)
    {
        return F.Promise.promise(() -> {
            SongSnippet songSnippet = songService.getSongById(id);
            return ok(Json.toJson(songSnippet));
        });
    }

    public F.Promise<Result> reIndexFromDB()
    {
        return F.Promise.promise(() -> {
            GenericResponse response = songService.reIndexSongsFromDB();
            return ok(Json.toJson(response));
        });
    }

    public F.Promise<Result> getSongsWithFilter()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            FilterRequest request = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), FilterRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST, ex);
                }
                List<SongSnippet> songs = songService.getSongsWithFilter(request);
                return ok(Json.toJson(songs));
            }
        });
    }

    public F.Promise<Result> getSongsCountByLanguage(Long languageId)
    {
        return F.Promise.promise(() -> {
            Long songCount = songService.getSongCount(languageId);
            Map<String, Long> returnMap = new HashMap<>();
            returnMap.put("song_count", songCount);
            return ok(Json.toJson(returnMap));
        });
    }

    public F.Promise<Result> getAllYears()
    {
        return F.Promise.promise(() -> {
            List<SqlRow> years = songService.getAllYears();
            return ok(Json.toJson(Collections.singletonMap("years", years)));
        });
    }

    public F.Promise<Result> addSong()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            SongRequest request = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), SongRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST);
                }
                SongSnippet songSnippet = songService.createSong(request);
                return ok(Json.toJson(songSnippet));
            }
        });
    }

    public F.Promise<Result> editSong()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            SongRequest request = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    request = Utils.convertObject(request().body().asJson(), SongRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST);
                }
                SongSnippet songSnippet = songService.editSong(request);
                return ok(Json.toJson(songSnippet));
            }
        });
    }
}
