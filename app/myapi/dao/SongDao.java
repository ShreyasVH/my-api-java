package myapi.dao;

import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import myapi.models.Song;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public class SongDao extends BaseDao
{
    private final Logger.ALogger LOGGER = Logger.of(SongDao.class);

    @Inject
    public SongDao()
    {
        super();
    }

    public List<SqlRow> dashboard()
    {
        List<SqlRow> dashboard = null;
        try
        {
            String sql = "SELECT l.name as language, count(*) as count, sum(s.size) as size FROM songs s inner join movies m on m.id = s.movie_id inner join languages_list l on l.id = m.language_id group by m.language_id order by l.id";
            SqlQuery query = db.createSqlQuery(sql);
            dashboard = query.findList();
        }
        catch(Exception ex)
        {
            LOGGER.error("[dashboard] Error : ", ex);
        }
        return dashboard;
    }

    public Song getSongById(String id)
    {
        Song song = null;
        try
        {
            song = db.find(Song.class).where().eq("id", id).findUnique();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getSongById] Error : ", ex);
        }
        return song;
    }

    public List<Song> getAllSongs()
    {
        List<Song> songs = new ArrayList<>();
        try
        {
            songs = db.find(Song.class).findList();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getAllSongs] Error : ", ex);
        }

        return songs;
    }

    public List<SqlRow> getAllYears()
    {
        List<SqlRow> years = new ArrayList<>();
        try
        {
            String sql = "SELECT m.year, count(*) as song_count FROM songs s INNER JOIN movies m ON m.id = s.movie_id GROUP BY m.year ORDER BY m.year";
            SqlQuery query = db.createSqlQuery(sql);
            years = query.findList();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getAllYears] : Error getting all years.", ex);
        }
        return years;
    }

    public Boolean saveSong(Song song)
    {
        Boolean isSuccess = true;

        try
        {
            db.save(song);
        }
        catch(Exception ex)
        {
            isSuccess = false;
            LOGGER.error("[saveSong] : Error while saving song.", ex);
        }

        return isSuccess;
    }
}
