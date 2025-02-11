package services.impl;

import com.google.inject.Inject;
import constants.Constants;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import io.ebean.Transaction;
import models.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import repositories.SongRepository;
import requests.FilterRequest;
import requests.SongRequest;
import responses.*;
import services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final ArtistService artistService;
    private final ElasticService elasticService;

    @Inject
    public SongServiceImpl
            (
                    SongRepository songRepository,

                    ArtistService artistService,
                    ElasticService elasticService
            )
    {
        this.songRepository = songRepository;

        this.artistService = artistService;
        this.elasticService = elasticService;
    }

    private SongElasticDocument songElasticDocument(Song song, MovieResponse movieResponse, List<Long> singerIds, List<Long> composerIds, List<Long> lyricistIds)
    {
        SongElasticDocument songElasticDocument = new SongElasticDocument(song);
        songElasticDocument.setMovie(movieResponse);

        List<Artist> singers = new ArrayList<>();
        List<Artist> composers = new ArrayList<>();
        List<Artist> lyricists = new ArrayList<>();
        if(null == singerIds)
        {
            singerIds = this.songRepository.getSingerMaps(song.getId()).stream().map(SongSingerMap::getSingerId).collect(Collectors.toList());
        }
        List<Long> artistIds = new ArrayList<>(singerIds);

        if(null == composerIds)
        {
            composerIds = this.songRepository.getComposerMaps(song.getId()).stream().map(SongComposerMap::getComposerId).collect(Collectors.toList());
        }
        artistIds.addAll(composerIds);

        if(null == lyricistIds)
        {
            lyricistIds = this.songRepository.getLyricistMaps(song.getId()).stream().map(SongLyricistMap::getLyricistId).collect(Collectors.toList());
        }
        artistIds.addAll(lyricistIds);

        artistIds = artistIds.stream().distinct().collect(Collectors.toList());

        Map<Long, Artist> artistMap = this.artistService.get(artistIds).stream().collect(Collectors.toMap(Artist::getId, artist -> artist));
        for(Long singerId: singerIds) {
            if(artistMap.containsKey(singerId)) {
                singers.add(artistMap.get(singerId));
            }
        }

        for(Long composerId: composerIds) {
            if(artistMap.containsKey(composerId)) {
                composers.add(artistMap.get(composerId));
            }
        }

        for(Long lyricistId: lyricistIds) {
            if(artistMap.containsKey(lyricistId)) {
                lyricists.add(artistMap.get(lyricistId));
            }
        }

        songElasticDocument.setSingers(singers);
        songElasticDocument.setComposers(composers);
        songElasticDocument.setLyricists(lyricists);

        return songElasticDocument;
    }

    @Override
    public Song add(SongRequest request, MovieResponse movieResponse) {
        Song song = new Song(request);
        song.setActive(true);
        song.setObtained(false);

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            song = this.songRepository.save(song);

            List<Artist> singers = this.artistService.get(request.getSingerIds());
            if(singers.size() != request.getSingerIds().size())
            {
                throw new NotFoundException("Singer");
            }
            Song finalSong = song;
            this.songRepository.saveSingerMaps(request.getSingerIds().stream().map(singerId -> {
                SongSingerMap singerMap = new SongSingerMap();
                singerMap.setSongId(finalSong.getId());
                singerMap.setSingerId(singerId);

                return singerMap;
            }).collect(Collectors.toList()));

            this.songRepository.saveComposerMaps(request.getComposerIds().stream().map(composerId -> {
                SongComposerMap composerMap = new SongComposerMap();
                composerMap.setSongId(finalSong.getId());
                composerMap.setComposerId(composerId);

                return composerMap;
            }).collect(Collectors.toList()));

            this.songRepository.saveLyricistMaps(request.getLyricistIds().stream().map(lyricistIds -> {
                SongLyricistMap lyricistMap = new SongLyricistMap();
                lyricistMap.setSongId(finalSong.getId());
                lyricistMap.setLyricistId(lyricistIds);

                return lyricistMap;
            }).collect(Collectors.toList()));

            transaction.commit();

            SongElasticDocument songElasticDocument = this.songElasticDocument(song, movieResponse, request.getSingerIds(), request.getComposerIds(), request.getLyricistIds());
            this.elasticService.index(Constants.INDEX_NAME_SONGS, song.getId(), songElasticDocument);
//            return new MovieResponse(movieElasticDocument);

            return song;
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Override
    public List<SqlRow> dashboard() {
        return songRepository.getDashboard();
    }

    private SearchRequest getElasticRequest(FilterRequest filterRequest)
    {
        SearchRequest request = new SearchRequest(Constants.INDEX_NAME_SONGS);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(filterRequest.getOffset());
        builder.size(filterRequest.getCount());

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        for(Map.Entry<String, List<String>> entry: filterRequest.getAndFilters().entrySet())
        {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();
            if (!valueList.isEmpty())
            {
                query.must(QueryBuilders.termsQuery(key, valueList));
            }
        }

        for(Map.Entry<String, Boolean> entry: filterRequest.getBooleanFilters().entrySet())
        {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            query.must(QueryBuilders.termQuery(key, value));
        }

        builder.query(query);

        Map<String, SortOrder> sortMap = filterRequest.getSortMap();
        for(Map.Entry<String, SortOrder> sortField: sortMap.entrySet())
        {
            String key = sortField.getKey();
            SortOrder order = sortField.getValue();

            String sortKey = (key + ".sort");
            builder.sort(sortKey, order);
        }

        if(!sortMap.containsKey("id"))
        {
            builder.sort("id.sort", SortOrder.ASC);
        }

        request.source(builder);
        return request;
    }

    @Override
    public FilterResponse<SongElasticDocument> filter(FilterRequest request) {
        FilterResponse<SongElasticDocument> response = this.elasticService.search(getElasticRequest(request), SongElasticDocument.class);
        response.setOffset(request.getOffset());

        return response;
    }

    @Override
    public SongElasticDocument get(Long id)
    {
        return this.elasticService.get(Constants.INDEX_NAME_SONGS, id, SongElasticDocument.class);
    }
}
