package myapi.services;

import com.avaje.ebean.SqlRow;
import myapi.exceptions.MyException;
import myapi.models.GenericResponse;
import myapi.models.Song;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.requests.SongRequest;
import myapi.skeletons.responses.SongSnippet;

import java.util.List;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public interface SongService
{
    List<SqlRow> dashboard();

    SongSnippet songSnippet(Song song) throws MyException;

    SongSnippet getSongById(String id) throws MyException;

    List<SongSnippet> getAllSongsFromDB() throws MyException;

    GenericResponse reIndexSongsFromDB() throws MyException;

    List<SongSnippet> getSongsWithFilter(FilterRequest filterRequest);

    Long getSongCount(Long languageId);

    List<SqlRow> getAllYears();

    Song createSongFromRequest(SongRequest request);

    Song createSongFromRequest(SongRequest request, Song song);

    SongSnippet createSong(SongRequest request) throws Exception;

    SongSnippet editSong(SongRequest request) throws Exception;
}
