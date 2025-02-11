package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import models.*;
import play.db.ebean.EbeanConfig;

import java.util.*;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import play.db.ebean.EbeanDynamicEvolutions;

public class SongRepository
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public SongRepository
            (
                    EbeanConfig ebeanConfig,
                    EbeanDynamicEvolutions ebeanDynamicEvolutions,
                    DatabaseExecutionContext databaseExecutionContext
            )
    {
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
        this.db = Ebean.getServer(ebeanConfig.defaultServer());
        this.databaseExecutionContext = databaseExecutionContext;
    }

    public Song save(Song song)
    {
        try
        {
            this.db.save(song);
            return song;
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public List<SongSingerMap> saveSingerMaps(List<SongSingerMap> singerMaps)
    {
        try
        {
            this.db.saveAll(singerMaps);
            return singerMaps;
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public List<SongComposerMap> saveComposerMaps(List<SongComposerMap> composerMaps)
    {
        try
        {
            this.db.saveAll(composerMaps);
            return composerMaps;
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public List<SongLyricistMap> saveLyricistMaps(List<SongLyricistMap> lyricistMaps)
    {
        try
        {
            this.db.saveAll(lyricistMaps);
            return lyricistMaps;
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public List<SongSingerMap> getSingerMaps(Long songId)
    {
        List<SongSingerMap> singerMaps;

        try
        {
            singerMaps = this.db.find(SongSingerMap.class).where().eq("songId", songId).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return singerMaps;
    }

    public List<SongComposerMap> getComposerMaps(Long songId)
    {
        List<SongComposerMap> composerMaps;

        try
        {
            composerMaps = this.db.find(SongComposerMap.class).where().eq("songId", songId).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return composerMaps;
    }

    public List<SongLyricistMap> getLyricistMaps(Long songId)
    {
        List<SongLyricistMap> lyricistMaps;

        try
        {
            lyricistMaps = this.db.find(SongLyricistMap.class).where().eq("songId", songId).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return lyricistMaps;
    }

    public List<SqlRow> getDashboard()
    {
        List<SqlRow> dbList;
        try
        {
            String sql = "SELECT l.name as language, count(*) as count, sum(s.size) as size FROM songs s inner join movies m on m.id = s.movie_id inner join languages l on l.id = m.language_id group by m.language_id order by l.id";
            SqlQuery query = db.sqlQuery(sql);
            dbList = query.findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return dbList;
    }
}