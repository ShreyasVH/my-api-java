package myapi.dao;

import myapi.models.SongComposerMap;
import myapi.models.SongLyricistMap;
import myapi.models.SongSingerMap;
import myapi.utils.Utils;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 1/12/18.
 */
public class SongArtistMapDao extends BaseDao
{
    private static final Logger.ALogger LOGGER = Logger.of(SongArtistMapDao.class);

    public List<SongSingerMap> getSingerMapsForSong(String songId)
    {
        List<SongSingerMap> singerMaps = new ArrayList<>();
        try
        {
            singerMaps = db.find(SongSingerMap.class).where().eq("song_id", songId).findList();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getSingerMapsForSong] Error while getting singer maps for song.", ex);
        }
        return singerMaps;
    }

    public List<SongComposerMap> getComposerMapsForSong(String songId)
    {
        List<SongComposerMap> composerMaps = new ArrayList<>();
        try
        {
            composerMaps = db.find(SongComposerMap.class).where().eq("song_id", songId).findList();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getComposerMapsForSong] Error while getting composer maps for song.", ex);
        }
        return composerMaps;
    }

    public List<SongLyricistMap> getLyricisMapsForSong(String songId)
    {
        List<SongLyricistMap> lyricistMaps = new ArrayList<>();
        try
        {
            lyricistMaps = db.find(SongLyricistMap.class).where().eq("song_id", songId).findList();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getLyricisMapsForSong] Error while getting lyricist maps for song.", ex);
        }
        return lyricistMaps;
    }

    public Boolean saveSingersForSong(String songId, List<String> singerIds)
    {
        Boolean isCompleteSuccess = true;
        try
        {
            for(String singerId : singerIds)
            {
                SongSingerMap map = new SongSingerMap();
                map.setId(Utils.generateUniqueIdByParam("ss"));
                map.setSongId(songId);
                map.setSingerId(singerId);
                Boolean isSuccess = saveSongSingerMap(map);
                isCompleteSuccess = (isCompleteSuccess && isSuccess);
            }
        }
        catch(Exception ex)
        {
            LOGGER.error("[saveSingersForSong] : Error while saving singers for song.", ex);
            isCompleteSuccess = false;
        }
        return isCompleteSuccess;
    }

    public Boolean saveSongSingerMap(SongSingerMap songSingerMap)
    {
        Boolean isSuccess = true;
        try
        {
            db.save(songSingerMap);
        }
        catch(Exception ex)
        {
            isSuccess = false;
            LOGGER.error("[saveSongSingerMap] : Error saving singer for song.", ex);
        }
        return isSuccess;
    }

    public Boolean saveComposersForSong(String songId, List<String> composerIds)
    {
        Boolean isCompleteSuccess = true;
        try
        {
            for(String composerId : composerIds)
            {
                SongComposerMap map = new SongComposerMap();
                map.setId(Utils.generateUniqueIdByParam("sc"));
                map.setSongId(songId);
                map.setComposerId(composerId);
                Boolean isSuccess = saveSongComposerMap(map);
                isCompleteSuccess = (isCompleteSuccess && isSuccess);
            }
        }
        catch(Exception ex)
        {
            LOGGER.error("[saveComposersForSong] : Error saving composers for song.", ex);
            isCompleteSuccess = false;
        }
        return isCompleteSuccess;
    }

    public Boolean saveSongComposerMap(SongComposerMap songComposerMap)
    {
        Boolean isSuccess = true;

        try
        {
            db.save(songComposerMap);
        }
        catch(Exception ex)
        {
            isSuccess = false;
            LOGGER.error("[saveSongComposerMap] : Error saving composer for song.", ex);
        }

        return isSuccess;
    }

    public Boolean saveLyricistsForSong(String songId, List<String> lyricistIds)
    {
        Boolean isCompleteSuccess = true;

        try
        {
            for(String lyricistId : lyricistIds)
            {
                SongLyricistMap map = new SongLyricistMap();
                map.setId(Utils.generateUniqueIdByParam("sl"));
                map.setSongId(songId);
                map.setLyricistId(lyricistId);
                Boolean isSuccess = saveSongLyricistMap(map);
                isCompleteSuccess = (isCompleteSuccess && isSuccess);
            }
        }
        catch(Exception ex)
        {
            LOGGER.error("[saveLyricistsForSong] : Error saving lyricists for song.", ex);
        }

        return isCompleteSuccess;
    }

    public Boolean saveSongLyricistMap(SongLyricistMap songLyricistMap)
    {
        Boolean isSuccess = true;

        try
        {
            db.save(songLyricistMap);
        }
        catch(Exception ex)
        {
            isSuccess = false;
            LOGGER.error("[saveSongLyricistMap] : Error saving lyricist for song.", ex);
        }

        return isSuccess;
    }

    public Boolean removeSingersFromSong(String songId)
    {
        Boolean isCompeteSuccess = true;
        try
        {
            List<SongSingerMap> songSingerMaps = db.find(SongSingerMap.class).where().eq("song_id", songId).findList();
            for(SongSingerMap songSingerMap : songSingerMaps)
            {
                Boolean isSuccess = removeSongSingerMap(songSingerMap);
                isCompeteSuccess = (isCompeteSuccess && isSuccess);
            }
        }
        catch(Exception ex)
        {
            LOGGER.error("[removeSingersFromSong] : Error while removing singers for Song - " + songId, ex);
        }
        return isCompeteSuccess;
    }

    public Boolean removeSongSingerMap(SongSingerMap songSingerMap)
    {
        Boolean isSuccess = true;
        try
        {
            db.delete(songSingerMap);
        }
        catch(Exception ex)
        {
            isSuccess = false;
            LOGGER.error("[removeSongSingerMap] : Error while deleting songSingerMap.", ex);
        }
        return isSuccess;
    }

    public Boolean removeComposersFromSong(String songId)
    {
        Boolean isCompeteSuccess = true;
        try
        {
            List<SongComposerMap> songComposerMaps = db.find(SongComposerMap.class).where().eq("song_id", songId).findList();
            for(SongComposerMap songComposerMap : songComposerMaps)
            {
                Boolean isSuccess = removeSongComposerMap(songComposerMap);
                isCompeteSuccess = (isCompeteSuccess && isSuccess);
            }
        }
        catch(Exception ex)
        {
            LOGGER.error("[removeComposersFromSong] : Error while removing composers for Song - " + songId, ex);
        }
        return isCompeteSuccess;
    }

    public Boolean removeSongComposerMap(SongComposerMap songComposerMap)
    {
        Boolean isSuccess = true;
        try
        {
            db.delete(songComposerMap);
        }
        catch(Exception ex)
        {
            isSuccess = false;
            LOGGER.error("[removeSongComposerMap] : Error while deleting songComposerMap.", ex);
        }
        return isSuccess;
    }

    public Boolean removeLyricistsFromSong(String songId)
    {
        Boolean isCompeteSuccess = true;
        try
        {
            List<SongLyricistMap> songLyricistMaps = db.find(SongLyricistMap.class).where().eq("song_id", songId).findList();
            for(SongLyricistMap songLyricistMap : songLyricistMaps)
            {
                Boolean isSuccess = removeSongLyricistMap(songLyricistMap);
                isCompeteSuccess = (isCompeteSuccess && isSuccess);
            }
        }
        catch(Exception ex)
        {
            LOGGER.error("[removeLyricistsFromSong] : Error while removing lyricists for Song - " + songId, ex);
        }
        return isCompeteSuccess;
    }

    public Boolean removeSongLyricistMap(SongLyricistMap songLyricistMap)
    {
        Boolean isSuccess = true;
        try
        {
            db.delete(songLyricistMap);
        }
        catch(Exception ex)
        {
            isSuccess = false;
            LOGGER.error("[removeSongLyricistMap] : Error while deleting songLyricistMap.", ex);
        }
        return isSuccess;
    }
}
