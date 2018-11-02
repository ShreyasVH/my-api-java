package myapi.dao;

import com.google.inject.Inject;
import myapi.models.MovieActorMap;
import myapi.models.MovieDirectorMap;
import myapi.utils.Utils;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class MovieArtistMapDao extends BaseDao
{
    private static final Logger.ALogger LOGGER = Logger.of(MovieArtistMapDao.class);

    @Inject
    public MovieArtistMapDao()
    {
        super();
    }

    public List<MovieActorMap> getActorMapsForMovie(Long movieId)
    {
        List<MovieActorMap> actorMaps = new ArrayList<>();
        try
        {
            actorMaps = db.find(MovieActorMap.class).where().eq("movie_id", movieId).findList();
        }
        catch (Exception ex)
        {

        }
        return actorMaps;
    }

    public List<MovieDirectorMap> getDirectorMapsForMovie(Long movieId)
    {
        List<MovieDirectorMap> directorMaps = new ArrayList<>();
        try
        {
            directorMaps = db.find(MovieDirectorMap.class).where().eq("movie_id", movieId).findList();
        }
        catch (Exception ex)
        {

        }
        return directorMaps;
    }

    public Boolean saveActorsForMovie(Long movieId, List<String> actorIds)
    {
        Boolean isCompleteSuccess = true;
        for(String actorId : actorIds)
        {
            MovieActorMap map = new MovieActorMap();
            map.setId(Utils.generateUniqueIdByParam("ma"));
            map.setMovieId(movieId);
            map.setActorId(actorId);
            Boolean isSuccess = saveMovieActorMap(map);
            isCompleteSuccess = (isCompleteSuccess && isSuccess);
        }
        return isCompleteSuccess;
    }

    public Boolean saveMovieActorMap(MovieActorMap map)
    {
        Boolean isSuccess = true;
        try
        {
            db.save(map);
        }
        catch(Exception ex)
        {
            isSuccess = false;
        }
        return isSuccess;
    }

    public Boolean removeMovieActorMap(MovieActorMap map)
    {
        Boolean isSuccess = true;
        try
        {
            db.delete(map);
        }
        catch(Exception ex)
        {
            isSuccess = false;
        }
        return isSuccess;
    }

    public Boolean removeActorsFromMovie(Long movieId)
    {
        List<MovieActorMap> maps = new ArrayList<>();
        Boolean isSuccess = true;
        try
        {
            maps = db.find(MovieActorMap.class).where().eq("movie_id", movieId).findList();
            for(MovieActorMap map : maps)
            {
                removeMovieActorMap(map);
            }
        }
        catch(Exception ex)
        {
            isSuccess = false;
        }
        return isSuccess;
    }

    public Boolean saveDirectorsForMovie(Long movieId, List<String> directorIds)
    {
        Boolean isCompleteSuccess = true;
        for(String directorId : directorIds)
        {
            MovieDirectorMap map = new MovieDirectorMap();
            map.setId(Utils.generateUniqueIdByParam("md"));
            map.setMovieId(movieId);
            map.setDirectorId(directorId);
            Boolean isSuccess = saveMovieDirectorMap(map);
            isCompleteSuccess = (isCompleteSuccess && isSuccess);
        }
        return isCompleteSuccess;
    }

    public Boolean saveMovieDirectorMap(MovieDirectorMap map)
    {
        Boolean isSuccess = true;
        try
        {
            db.save(map);
        }
        catch(Exception ex)
        {
            isSuccess = false;
        }
        return isSuccess;
    }

    public Boolean removeMovieDirectorMap(MovieDirectorMap map)
    {
        Boolean isSuccess = true;
        try
        {
            db.delete(map);
        }
        catch(Exception ex)
        {
            isSuccess = false;
        }
        return isSuccess;
    }

    public Boolean removeDirectorsFromMovie(Long movieId)
    {
        List<MovieDirectorMap> maps = new ArrayList<>();
        Boolean isSuccess = true;
        try
        {
            maps = db.find(MovieDirectorMap.class).where().eq("movie_id", movieId).findList();
            for(MovieDirectorMap map : maps)
            {
                removeMovieDirectorMap(map);
            }
        }
        catch(Exception ex)
        {
            isSuccess = false;
        }
        return isSuccess;
    }
}
