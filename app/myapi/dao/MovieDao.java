package myapi.dao;

import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import myapi.models.Movie;
import myapi.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import play.libs.Json;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class MovieDao extends BaseDao
{
    @Inject
    public MovieDao()
    {
        super();
    }

    public List<Movie> getAllMovies()
    {
        List<Movie> movies = new ArrayList<>();
        try
        {
            movies = db.find(Movie.class).where().findList();
        }
        catch(Exception ex)
        {
            Logger.error("Error while getting all movies. Exception: " + ex);
        }
        return movies;
    }

    public Movie getMovieById(Long id)
    {
        Movie movie = null;
        try
        {
            movie = db.find(Movie.class).where().eq("id", id).findUnique();
        }
        catch(Exception ex)
        {
            Logger.error("Error while getting movie by id. id: " + id + ". Exception: " + ex);
        }
        return movie;
    }

    public Boolean saveMovie(Movie movie)
    {
        Boolean isSuccess = false;
        try
        {
            db.save(movie);
            isSuccess = true;
        }
        catch(Exception ex)
        {
            Logger.error("Error while saving movie. payload: " + Json.toJson(movie) + ". Exception: " + ex);
        }
        return isSuccess;
    }

    public List<Movie> getMoviesWithActorCombination(List<String> actorIds)
    {
        List<Movie> movies = new ArrayList<>();
        Integer size = actorIds.size();
        Integer i;
        try
        {
            String sql = "select movie_id from movie_actor_map where actor_id = '" + actorIds.get(size - 1) + "'";
            for (i = (size - 2); i > 0; i = i - 1)
            {
                sql = "select movie_id from movie_actor_map where actor_id = '" + actorIds.get(i) + "' and movie_id in (" + sql + ")";
            }
            sql = "select m.* from movies m inner join movie_actor_map mam on mam.movie_id = m.id where mam.actor_id = '" + actorIds.get(0) + "' and mam.movie_id in (" + sql + ") and m.status = 1 order by m.year desc, m.id desc";
            SqlQuery query = db.createSqlQuery(sql);
            List<SqlRow> movieList = query.findList();
            for(SqlRow movie : movieList)
            {
                Movie m = new Movie(movie);
                movies.add(m);
            }
        }
        catch(Exception ex)
        {
            Logger.error("Error while getting movie actor combinations. actorsIds: " + Json.toJson(actorIds) + ". Exception: " + ex);
        }
        return movies;
    }

    public List<SqlRow> getAllYears()
    {
        List<SqlRow> years = new ArrayList<>();
        try
        {
            String sql = "select year, count(*) as movie_count from movies where status = 1 group by year order by year";
            SqlQuery query = db.createSqlQuery(sql);
            years = query.findList();
        }
        catch(Exception ex)
        {
            Logger.error("Error while getting all years. Exception: " + ex);
        }
        return years;
    }

    public List<SqlRow> getDashboard()
    {
        List<SqlRow> dbList = new ArrayList<>();
        try
        {
            String sql = "select l.id as language_id, l.name as language, count(*) as count, sum(m.size) as size from movies m inner join languages_list l on m.language_id = l.id where m.status = 1 group by m.language_id";
            SqlQuery query = db.createSqlQuery(sql);
            dbList = query.findList();
        }
        catch(Exception ex)
        {
            Logger.error("Error while getting movie dashboard. Exception: " + ex);
        }
        return dbList;
    }
}
