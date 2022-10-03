package repositories;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Artist;
import modules.DatabaseExecutionContext;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;
import responses.FilterResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class ArtistRepository
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public ArtistRepository
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

    public Artist saveArtist(Artist artist)
    {
        try
        {
            this.db.save(artist);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return artist;
    }

    public Artist getArtistByName(String name)
    {
        Artist artist;
        try
        {
            artist = db.find(Artist.class).where().eq("name", name).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return artist;
    }

    public List<Artist> get(List<Long> ids)
    {
        List<Artist> artists = new ArrayList<>();
        try
        {
            artists = db.find(Artist.class).where().in("id", ids).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return artists;
    }

    public Artist get(Long id)
    {
        Artist artist;
        try
        {
            artist = db.find(Artist.class).where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return artist;
    }

    public FilterResponse<Artist> get(int offset, int count)
    {
        FilterResponse<Artist> response = new FilterResponse<>();
        try
        {
            response.setTotalCount(db.find(Artist.class).findCount());
            response.setList(db.find(Artist.class).where().setFirstRow(offset).setMaxRows(count).orderBy("name ASC").findList());
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return response;
    }
}
