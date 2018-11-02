package myapi.controllers;

import com.google.inject.Inject;
import myapi.exceptions.BadRequestException;
import myapi.models.Artist;
import myapi.models.ValidationResponse;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import myapi.services.ArtistService;
import myapi.skeletons.requests.AddArtistRequest;
import myapi.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public class ArtistsController extends Controller
{
    private final ArtistService artistService;

    @Inject
    public ArtistsController(ArtistService artistService)
    {
        this.artistService = artistService;
    }

    public F.Promise<Result> getAllActors()
    {
        return F.Promise.promise(() -> {
            Map<String, Object> hashMap = new HashMap<>();
            List<Artist> actors = artistService.getAllActors();
            hashMap.put("actors", actors);
            return ok(Json.toJson(hashMap));
        });
    }

    public F.Promise<Result> getAllDirectors()
    {
        return F.Promise.promise(() -> {
            Map<String, Object> hashMap = new HashMap<>();
            List<Artist> directors = artistService.getAllDirectors();
            hashMap.put("directors", directors);
            return ok(Json.toJson(hashMap));
        });
    }

    public F.Promise<Result> addArtist()
    {
        return F.Promise.promise(new F.Function0<Result>() {
            AddArtistRequest addArtistRequest = null;
            @Override
            public Result apply() throws Exception
            {
                try
                {
                    addArtistRequest = Utils.convertObject(request().body().asJson(), AddArtistRequest.class);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ValidationResponse.INVALID_REQUEST, ex);
                }
                Artist artist = artistService.addArtist(addArtistRequest);
                return ok(Json.toJson(artist));
            }
        });
    }

    public F.Promise<Result> getArtistById(String id)
    {
        return F.Promise.promise(() -> {
            Artist artist = artistService.getArtistById(id);
            return ok(Json.toJson(artist));
        });
    }

    public F.Promise<Result> getArtistsByKeyword(String keyword)
    {
        return F.Promise.promise(() -> {
            List<Artist> artists = artistService.getArtistsByKeyword(keyword);
            return ok(Json.toJson(artists));
        });
    }

    public F.Promise<Result> getAllSingers()
    {
        return F.Promise.promise(() -> {
            List<Artist> singers = artistService.getAllSingers();
            Map<String, List<Artist>> returnMap = new HashMap<>();
            returnMap.put("singers", singers);
            return ok(Json.toJson(returnMap));
        });
    }

    public F.Promise<Result> getAllComposers()
    {
        return F.Promise.promise(() -> {
            Map<String, List<Artist>> returnMap = new HashMap<>();
            List<Artist> composers = artistService.getAllComposers();
            returnMap.put("composers", composers);
            return ok(Json.toJson(returnMap));
        });
    }

    public F.Promise<Result> getAllLyricists()
    {
        return F.Promise.promise(() -> {
            Map<String, List<Artist>> returnMap = new HashMap<>();
            List<Artist> lyricists = artistService.getAllLyricists();
            returnMap.put("lyricists", lyricists);
            return ok(Json.toJson(returnMap));
        });
    }
}
