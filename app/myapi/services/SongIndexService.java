package myapi.services;

import myapi.exceptions.MyException;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.responses.ElasticResponse;
import myapi.skeletons.responses.SongSnippet;

import java.util.List;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public interface SongIndexService
{
    Boolean reIndexSongsFromDB() throws MyException;

    Boolean indexSong(String id) throws MyException;

    Boolean indexSong(SongSnippet songSnippet);

    Boolean indexSong(SongSnippet songSnippet, Boolean isUpdateRequired);

    void indexSongAsThread(SongSnippet songSnippet);

    void indexSongAsThread(SongSnippet songSnippet, Boolean isUpdateRequired);

    ElasticResponse getSongElasticResponse(FilterRequest filterRequest);

    List<SongSnippet> getSongsWithFilter(FilterRequest filterRequest);

    SongSnippet getSongById(String songId) throws MyException;

    Long getSongCountWithFilter(FilterRequest filterRequest);
}
