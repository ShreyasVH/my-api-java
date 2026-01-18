package services.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.google.inject.Inject;
import constants.Constants;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.*;
import play.db.jpa.JPAApi;
import repositories.SongRepository;
import requests.FilterRequest;
import requests.SongRequest;
import responses.*;
import services.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SongServiceImpl implements SongService {
    private final JPAApi jpaApi;
    private final SongRepository songRepository;

    private final ArtistService artistService;
    private final ElasticService elasticService;

    @Inject
    public SongServiceImpl
            (
                    JPAApi jpaApi,
                    SongRepository songRepository,

                    ArtistService artistService,
                    ElasticService elasticService
            )
    {
        this.jpaApi = jpaApi;
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

        List<Artist> singers = this.artistService.get(request.getSingerIds());
        if(singers.size() != request.getSingerIds().size())
        {
            throw new NotFoundException("Singer");
        }

        jpaApi.withTransaction(em -> {
            this.songRepository.save(em, song);

            this.songRepository.saveSingerMaps(em, request.getSingerIds().stream().map(singerId -> {
                SongSingerMap singerMap = new SongSingerMap();
                singerMap.setSongId(song.getId());
                singerMap.setSingerId(singerId);

                return singerMap;
            }).collect(Collectors.toList()));

            this.songRepository.saveComposerMaps(em, request.getComposerIds().stream().map(composerId -> {
                SongComposerMap composerMap = new SongComposerMap();
                composerMap.setSongId(song.getId());
                composerMap.setComposerId(composerId);

                return composerMap;
            }).collect(Collectors.toList()));

            this.songRepository.saveLyricistMaps(em, request.getLyricistIds().stream().map(lyricistIds -> {
                SongLyricistMap lyricistMap = new SongLyricistMap();
                lyricistMap.setSongId(song.getId());
                lyricistMap.setLyricistId(lyricistIds);

                return lyricistMap;
            }).collect(Collectors.toList()));
        });

        SongElasticDocument songElasticDocument = this.songElasticDocument(song, movieResponse, request.getSingerIds(), request.getComposerIds(), request.getLyricistIds());
        this.elasticService.index(Constants.INDEX_NAME_SONGS, song.getId(), songElasticDocument);
        return song;
    }

    @Override
    public List dashboard() {
        return songRepository.getDashboard();
    }

    private SearchRequest getElasticRequest(FilterRequest filterRequest)
    {
        BoolQuery.Builder query = QueryBuilders.bool();

        for(Map.Entry<String, List<String>> entry: filterRequest.getAndFilters().entrySet())
        {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();
            if (!valueList.isEmpty())
            {
                query.must(b -> b.terms(TermsQuery.of(t -> t
                        .field(key)
                        .terms(TermsQueryField.of(m -> m
                                        .value(
                                                valueList.stream().map(FieldValue::of).collect(Collectors.toList())
                                        )
                                )
                        )
                )));
            }
        }

        for(Map.Entry<String, Boolean> entry: filterRequest.getBooleanFilters().entrySet())
        {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            query.must(b -> b.term(TermQuery.of(t -> t.field(key).value(value))));
        }

        Map<String, SortOrder> sortMap = filterRequest.getSortMap();
        List<SortOptions> sortOptions = new ArrayList<>();
        for(Map.Entry<String, SortOrder> sortField: sortMap.entrySet())
        {
            String key = sortField.getKey();
            SortOrder order = sortField.getValue();

            String sortKey = (key + ".sort");
            sortOptions.add(SortOptions.of(s -> s.field(f -> f.field(sortKey).order(order))));
        }

        if(!sortMap.containsKey("id"))
        {
            sortOptions.add(SortOptions.of(s -> s.field(f -> f.field("id.sort").order(SortOrder.Asc))));
        }

        return SearchRequest.of(b -> b
                .index(Constants.INDEX_NAME_SONGS)
                .query(query.build()._toQuery()
                )
                .from(filterRequest.getOffset())
                .size(filterRequest.getCount())
                .sort(sortOptions)
        );
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

    @Override
    public Song edit(long id, SongRequest request, MovieResponse movieResponse) {
        Song song = this.songRepository.get(id);
        if(null == song)
        {
            throw new NotFoundException("Song");
        }

        List<Artist> singers = this.artistService.get(request.getSingerIds());
        if(singers.size() != request.getSingerIds().size())
        {
            throw new NotFoundException("Singer");
        }

        List<SongSingerMap> existingSingers = this.songRepository.getSingerMaps(id);
        List<SongComposerMap> existingComposers = this.songRepository.getComposerMaps(id);
        List<SongLyricistMap> existingLyricists = this.songRepository.getLyricistMaps(id);

        boolean detailsChanged = false;
        if(!request.getName().equals(song.getName()))
        {
            detailsChanged = true;
            song.setName(request.getName());
        }

        if(!request.getSize().equals(song.getSize()))
        {
            detailsChanged = true;
            song.setSize(request.getSize());
        }

        if(!request.getMovieId().equals(song.getMovieId()))
        {
            detailsChanged = true;
            song.setMovieId(request.getMovieId());
        }

        boolean finalDetailsChanged = detailsChanged;
        jpaApi.withTransaction(em -> {
            if(finalDetailsChanged)
            {
                this.songRepository.save(em, song);
            }

            List<Long> existingSingerIds = existingSingers.stream().map(SongSingerMap::getSingerId).collect(Collectors.toList());
            List<SongSingerMap> singerMapsToDelete = existingSingers.stream().filter(ssm -> !request.getSingerIds().contains(ssm.getSingerId())).collect(Collectors.toList());
            List<Long> singerIdsToAdd = request.getSingerIds().stream().filter(singerId -> !existingSingerIds.contains(singerId)).collect(Collectors.toList());
            this.songRepository.saveSingerMaps(em, singerIdsToAdd.stream().map(singerId -> {
                SongSingerMap singerMap = new SongSingerMap();
                singerMap.setSongId(song.getId());
                singerMap.setSingerId(singerId);

                return singerMap;
            }).collect(Collectors.toList()));
            this.songRepository.deleteSingerMaps(em, singerMapsToDelete);

            List<Long> existingComposerIds = existingComposers.stream().map(SongComposerMap::getComposerId).collect(Collectors.toList());
            List<SongComposerMap> composerMapsToDelete = existingComposers.stream().filter(scm -> !request.getComposerIds().contains(scm.getComposerId())).collect(Collectors.toList());
            List<Long> composerIdsToAdd = request.getComposerIds().stream().filter(composerId -> !existingComposerIds.contains(composerId)).collect(Collectors.toList());
            this.songRepository.saveComposerMaps(em, composerIdsToAdd.stream().map(composerId -> {
                SongComposerMap composerMap = new SongComposerMap();
                composerMap.setSongId(song.getId());
                composerMap.setComposerId(composerId);

                return composerMap;
            }).collect(Collectors.toList()));
            this.songRepository.deleteComposerMaps(em, composerMapsToDelete);

            List<Long> existingLyricistIds = existingLyricists.stream().map(SongLyricistMap::getLyricistId).collect(Collectors.toList());
            List<SongLyricistMap> lyricistMapsToDelete = existingLyricists.stream().filter(slm -> !request.getLyricistIds().contains(slm.getLyricistId())).collect(Collectors.toList());
            List<Long> lyricistIdsToAdd = request.getLyricistIds().stream().filter(lyricistId -> !existingLyricistIds.contains(lyricistId)).collect(Collectors.toList());
            this.songRepository.saveLyricistMaps(em, lyricistIdsToAdd.stream().map(lyricistIds -> {
                SongLyricistMap lyricistMap = new SongLyricistMap();
                lyricistMap.setSongId(song.getId());
                lyricistMap.setLyricistId(lyricistIds);

                return lyricistMap;
            }).collect(Collectors.toList()));
            this.songRepository.deleteLyricistMaps(em, lyricistMapsToDelete);
        });

        SongElasticDocument songElasticDocument = this.songElasticDocument(song, movieResponse, request.getSingerIds(), request.getComposerIds(), request.getLyricistIds());
        this.elasticService.index(Constants.INDEX_NAME_SONGS, song.getId(), songElasticDocument);

        return song;
    }
}
