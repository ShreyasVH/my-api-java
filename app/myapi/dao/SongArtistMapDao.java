package myapi.dao;

import com.google.inject.Inject;
import myapi.models.SongComposerMap;
import myapi.models.SongLyricistMap;
import myapi.models.SongSingerMap;
import myapi.utils.Utils;
import myapi.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import play.libs.Json;

/**
 * Created by shreyas.hande on 1/12/18.
 */
public class SongArtistMapDao extends BaseDao
{
    @Inject
    public SongArtistMapDao()
    {
        super();
    }

    public List<SongSingerMap> getSingerMapsForSong(String songId)
    {
        List<SongSingerMap> singerMaps = new ArrayList<>();
        try
        {
            singerMaps = db.find(SongSingerMap.class).where().eq("song_id", songId).findList();
        }
        catch(Exception ex)
        {
            Logger.error("Error while getting singer maps by song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while getting composer maps by song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while getting lyricist maps by song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while saving singers for song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while saving singer maps for song. Payload: " + Json.toJson(songSingerMap) + ". Exception: " + ex);
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
            Logger.error("Error while saving composers for song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while saving composer maps for song. Payload: " + Json.toJson(songComposerMap) + ". Exception: " + ex);
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
            Logger.error("Error while saving lyricists for song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while saving lyricists for song. Payload: " + Json.toJson(songLyricistMap) + ". Exception: " + ex);
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
            Logger.error("Error while removing singers for song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while removing singers for song. Payload: " + Json.toJson(songSingerMap) + ". Exception: " + ex);
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
            Logger.error("Error while removing composers for song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while removing composer maps for song. Payload: " + Json.toJson(songComposerMap) + ". Exception: " + ex);
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
            Logger.error("Error while removing lyricists for song. id: " + songId + ". Exception: " + ex);
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
            Logger.error("Error while removing lyricist maps for song. Payload: " + Json.toJson(songLyricistMap) + ". Exception: " + ex);
        }
        return isSuccess;
    }
}
