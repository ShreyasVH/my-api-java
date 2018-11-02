package myapi.dao;

import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import myapi.models.Artist;
import myapi.utils.Utils;
import play.Logger;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class ArtistDao extends BaseDao
{
    private static final Logger.ALogger LOGGER = Logger.of(ArtistDao.class);

    @Inject
    public ArtistDao()
    {
        super();
    }

    public Artist getArtistById(String id)
    {
        Artist artist = null;
        try
        {
            artist = db.find(Artist.class).where().eq("id", id).findUnique();
        }
        catch (Exception ex)
        {

        }
        return artist;
    }

    public List<Artist> getAllActors()
    {
        List<Artist> actors = new ArrayList<>();
        try
        {
            String sql = "select a.* from artists_list a inner join movie_actor_map mam on mam.actor_id = a.id GROUP BY a.id ORDER BY a.name ASC";
            SqlQuery query = db.createSqlQuery(sql);
            List<SqlRow> actorList = query.findList();
            actors = Utils.convertObjectList(actorList, Artist.class);
        }
        catch(Exception ex)
        {

        }
        return actors;
    }

    public List<Artist> getAllDirectors()
    {
        List<Artist> directors = new ArrayList<>();
        try
        {
            String sql = "select a.* from artists_list a inner join movie_director_map mdm on mdm.director_id = a.id GROUP BY a.id ORDER BY a.name ASC";
            SqlQuery query = db.createSqlQuery(sql);
            List<SqlRow> actorList = query.findList();
            directors = Utils.convertObjectList(actorList, Artist.class);
        }
        catch(Exception ex)
        {

        }
        return directors;
    }

    public Boolean saveArtist(Artist artist)
    {
        Boolean isSuccess = false;
        try
        {
            db.save(artist);
            isSuccess = true;
        }
        catch(Exception ex)
        {

        }
        return isSuccess;
    }

    public Artist getArtistByName(String name)
    {
        Artist artist = null;
        try
        {
            artist = db.find(Artist.class).where().eq("name", name).findUnique();
        }
        catch(Exception ex)
        {

        }
        return artist;
    }

    public List<Artist> getArtistsByKeyword(String keyword)
    {
        keyword = URLDecoder.decode(keyword);
        List<Artist> artists = new ArrayList<>();
        try
        {
            artists = db.find(Artist.class).where().icontains("name", keyword).orderBy("name ASC").findList();
        }
        catch(Exception ex)
        {

        }
        return artists;
    }

    public List<Artist> getAllSingers()
    {
        List<Artist> singers = new ArrayList<>();
        try
        {
            String sql = "select a.* from artists_list a inner join song_singer_map ssm on ssm.singer_id = a.id GROUP BY a.id ORDER BY a.name ASC";
            SqlQuery query = db.createSqlQuery(sql);
            List<SqlRow> singerList = query.findList();
            singers = Utils.convertObjectList(singerList, Artist.class);
        }
        catch(Exception ex)
        {
            LOGGER.error("[getAllSingers] Error : ", ex);
        }
        return singers;
    }

    public List<Artist> getAllComposers()
    {
        List<Artist> composers = new ArrayList<>();
        try
        {
            String sql = "select a.* from artists_list a inner join song_composer_map scm on scm.composer_id = a.id GROUP BY a.id ORDER BY a.name ASC";
            SqlQuery query = db.createSqlQuery(sql);
            List<SqlRow> composerList = query.findList();
            composers = Utils.convertObjectList(composerList, Artist.class);
        }
        catch(Exception ex)
        {
            LOGGER.error("[getAllComposers] Error : ", ex);
        }
        return composers;
    }

    public List<Artist> getAllLyricists()
    {
        List<Artist> lyricists = new ArrayList<>();
        try
        {
            String sql = "select a.* from artists_list a inner join song_lyricist_map slm on slm.lyricist_id = a.id GROUP BY a.id ORDER BY a.name ASC";
            SqlQuery query = db.createSqlQuery(sql);
            List<SqlRow> lyricistList = query.findList();
            lyricists = Utils.convertObjectList(lyricistList, Artist.class);
        }
        catch(Exception ex)
        {
            LOGGER.error("[getAllLyricists] Error : ", ex);
        }
        return lyricists;
    }
}
