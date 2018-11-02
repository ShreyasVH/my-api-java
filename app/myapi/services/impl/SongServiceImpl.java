package myapi.services.impl;

import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import myapi.dao.SongArtistMapDao;
import myapi.dao.SongDao;
import myapi.exceptions.MyException;
import myapi.exceptions.NotFoundException;
import myapi.models.*;
import myapi.services.ArtistService;
import myapi.services.MovieService;
import myapi.services.SongIndexService;
import myapi.services.SongService;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.requests.SongRequest;
import myapi.skeletons.responses.SongSnippet;
import myapi.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public class SongServiceImpl implements SongService
{

    private final SongDao songDao;
    private final SongArtistMapDao songArtistMapDao;

    private final MovieService movieService;
    private final SongIndexService songIndexService;
    private final ArtistService artistService;

    @Inject
    public SongServiceImpl(
            SongDao songDao,
            SongArtistMapDao songArtistMapDao,
            MovieService movieService,
            SongIndexService songIndexService,
            ArtistService artistService
    )
    {
        this.songDao = songDao;
        this.songArtistMapDao = songArtistMapDao;
        this.movieService = movieService;
        this.songIndexService = songIndexService;
        this.artistService = artistService;
    }

    @Override
    public List<SqlRow> dashboard()
    {
        return songDao.dashboard();
    }

    @Override
    public SongSnippet songSnippet(Song song) throws MyException
    {
        SongSnippet songSnippet = new SongSnippet(song);
        songSnippet.setMovie(movieService.getMovieById(song.getMovieId()));

        List<Artist> singers = new ArrayList<>();
        List<SongSingerMap> singerMaps = songArtistMapDao.getSingerMapsForSong(songSnippet.getId());
        for(SongSingerMap singerMap : singerMaps)
        {
            Artist singer = artistService.getArtistById(singerMap.getSingerId());
            singers.add(singer);
        }
        songSnippet.setSingers(singers);

        List<Artist> composers = new ArrayList<>();
        List<SongComposerMap> composerMaps = songArtistMapDao.getComposerMapsForSong(songSnippet.getId());
        for(SongComposerMap composerMap : composerMaps)
        {
            Artist composer = artistService.getArtistById(composerMap.getComposerId());
            composers.add(composer);
        }
        songSnippet.setComposers(composers);

        List<Artist> lyricists = new ArrayList<>();
        List<SongLyricistMap> lyricistMaps = songArtistMapDao.getLyricisMapsForSong(songSnippet.getId());
        for(SongLyricistMap lyricistMap : lyricistMaps)
        {
            Artist lyricist = artistService.getArtistById(lyricistMap.getLyricistId());
            lyricists.add(lyricist);
        }
        songSnippet.setLyricists(lyricists);

        return songSnippet;
    }

    @Override
    public SongSnippet getSongById(String id) throws MyException
    {
        return songIndexService.getSongById(id);
    }

    @Override
    public List<SongSnippet> getAllSongsFromDB() throws MyException
    {
        List<Song> songList = songDao.getAllSongs();
        List<SongSnippet> songs = new ArrayList<>();
        for(Song song : songList)
        {
            SongSnippet songSnippet = songSnippet(song);
            songs.add(songSnippet);
        }
        return songs;
    }

    @Override
    public GenericResponse reIndexSongsFromDB() throws MyException
    {
        Boolean isSuccess = songIndexService.reIndexSongsFromDB();
        GenericResponse response = new GenericResponse();
        if(isSuccess)
        {
            response.setType(ResponseType.SUCCESS);
            response.setDescription(SuccessResponse.UPDATE_SUCCESS.getDescription());
            response.setCode(SuccessResponse.UPDATE_SUCCESS.getCode());
        }
        else
        {
            response.setType(ResponseType.ERROR);
            response.setCode(ErrorResponse.API_FAILED.getCode());
            response.setDescription(ErrorResponse.API_FAILED.getDescription());
        }
        return response;
    }

    @Override
    public List<SongSnippet> getSongsWithFilter(FilterRequest filterRequest)
    {
        return songIndexService.getSongsWithFilter(filterRequest);
    }

    @Override
    public Long getSongCount(Long languageId)
    {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setFilters(Collections.singletonMap("language", Collections.singletonList(languageId.toString())));
        return songIndexService.getSongCountWithFilter(filterRequest);
    }

    @Override
    public List<SqlRow> getAllYears()
    {
        return songDao.getAllYears();
    }

    @Override
    public Song createSongFromRequest(SongRequest request)
    {
        return createSongFromRequest(request, new Song());
    }

    @Override
    public Song createSongFromRequest(SongRequest request, Song song)
    {
        if(null != request.getName())
        {
            song.setName(request.getName());
        }

        if(null != request.getSize())
        {
            song.setSize(request.getSize());
        }

        if(null != request.getMovie_id())
        {
            song.setMovieId(request.getMovie_id());
        }
        return song;
    }

    @Override
    public SongSnippet createSong(SongRequest request) throws MyException
    {
        SongSnippet songSnippet = null;

        Song song = new Song();
        song.setId(Utils.generateUniqueIdByParam("s"));

        song = createSongFromRequest(request, song);
        Boolean isSuccess = songDao.saveSong(song);
        if(isSuccess)
        {
            songArtistMapDao.saveSingersForSong(song.getId(), request.getSinger_ids());
            songArtistMapDao.saveComposersForSong(song.getId(), request.getComposer_ids());
            songArtistMapDao.saveLyricistsForSong(song.getId(), request.getLyricist_ids());
            songSnippet = songSnippet(song);
            songIndexService.indexSongAsThread(songSnippet);
        }

        return songSnippet;
    }

    @Override
    public SongSnippet editSong(SongRequest request) throws MyException
    {
        SongSnippet songSnippet = null;

        Song song = songDao.getSongById(request.getId());
        if(null == song)
        {
            throw new NotFoundException(ValidationResponse.SONG_NOT_FOUND);
        }

        song = createSongFromRequest(request, song);
        Boolean isSuccess = songDao.saveSong(song);
        if(isSuccess)
        {
            songArtistMapDao.removeSingersFromSong(song.getId());
            songArtistMapDao.saveSingersForSong(song.getId(), request.getSinger_ids());

            songArtistMapDao.removeComposersFromSong(song.getId());
            songArtistMapDao.saveComposersForSong(song.getId(), request.getComposer_ids());

            songArtistMapDao.removeLyricistsFromSong(song.getId());
            songArtistMapDao.saveLyricistsForSong(song.getId(), request.getLyricist_ids());

            songSnippet = songSnippet(song);
            songIndexService.indexSongAsThread(songSnippet, true);
        }
        return songSnippet;
    }
}
