package myapi.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import myapi.constants.Constants;
import myapi.exceptions.MyException;
import myapi.exceptions.NotFoundException;
import myapi.models.SongAttribute;
import myapi.models.ValidationResponse;
import myapi.services.SongIndexService;
import myapi.services.SongService;
import myapi.services.elastic.ElasticService;
import myapi.skeletons.requests.FilterRequest;
import myapi.skeletons.responses.ElasticResponse;
import myapi.skeletons.responses.SongSnippet;
import myapi.utils.Utils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.Logger;

import java.util.*;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public class SongIndexServiceImpl implements SongIndexService
{
    private static final Logger.ALogger LOGGER = Logger.of(SongIndexServiceImpl.class);
    private final ElasticService elasticService;
    private final SongService songService;

    @Inject
    public SongIndexServiceImpl(
            ElasticService elasticService,
            SongService songService
    )
    {
        this.elasticService = elasticService;
        this.songService = songService;
    }

    @Override
    public Boolean reIndexSongsFromDB() throws MyException
    {
        List<SongSnippet> songs =  songService.getAllSongsFromDB();
        Boolean isCompleteSuccess = true;
        LOGGER.debug("Reindexing " + songs.size() + " songs");
        Long counter = 1L;
        for(SongSnippet songSnippet : songs)
        {
            LOGGER.debug("Indexing song " + counter.toString() + "/" + songs.size());
            Boolean isSuccess = indexSong(songSnippet);
            if(!isSuccess)
            {
                LOGGER.error("[reIndexSongsFromDB] : Failed to index movie. Id : " + songSnippet.id);
            }
            isCompleteSuccess = (isCompleteSuccess && isSuccess);
            counter++;
        }

        return isCompleteSuccess;
    }

    @Override
    public Boolean indexSong(SongSnippet songSnippet)
    {
        return indexSong(songSnippet, false);
    }

    @Override
    public Boolean indexSong(SongSnippet songSnippet, Boolean isUpdateRequired)
    {
        Boolean isSuccess;
        if(isUpdateRequired)
        {
            isSuccess = elasticService.update("song", "song", songSnippet.getId(), Utils.convertObject(songSnippet, JsonNode.class).toString());
        }
        else
        {
            isSuccess = elasticService.create("song", "song", songSnippet.getId(), Utils.convertObject(songSnippet, JsonNode.class).toString());
        }
        return isSuccess;
    }

    @Override
    public void indexSongAsThread(SongSnippet songSnippet)
    {
        indexSongAsThread(songSnippet, false);
    }

    @Override
    public void indexSongAsThread(SongSnippet songSnippet, Boolean isUpdateRequired)
    {
        Utils.scheduleOnce(() -> {
            try
            {
                indexSong(songSnippet, isUpdateRequired);
            }
            catch(Exception ex)
            {
                LOGGER.error("[indexSongAsThread] Error while indexing song", ex);
            }
        });
    }

    @Override
    public ElasticResponse getSongElasticResponse(FilterRequest filterRequest)
    {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        Iterator filterIterator = filterRequest.getFilters().entrySet().iterator();
        List<SortBuilder> sortMapList = new ArrayList<>();

        while(filterIterator.hasNext())
        {
            Map.Entry entry = (Map.Entry) filterIterator.next();
            SongAttribute songAttribute = SongAttribute.getSongAttributeByName(entry.getKey().toString());
            if(Constants.FIELD_TYPE_NORMAL.equals(songAttribute.getType()))
            {
                query.must(QueryBuilders.termsQuery(entry.getKey().toString(), (List) entry.getValue()));
            }
            else if(Constants.FIELD_TYPE_NESTED.equals(songAttribute.getType()))
            {
                QueryBuilder nestedQuery = QueryBuilders.nestedQuery(songAttribute.getNestedLevel(), QueryBuilders.termsQuery(songAttribute.getNestedTerm(), (List) entry.getValue()),  ScoreMode.None);
                query.must(nestedQuery);
            }
        }

        for (Object o : filterRequest.getSortMap().entrySet())
        {
            Map.Entry entry = (Map.Entry) o;
            SongAttribute songAttribute = SongAttribute.getSongAttributeByName(entry.getKey().toString());
            FieldSortBuilder sortMap = null;
            String sortKey = Constants.SORT_KEY_ELASTIC;
            if (Constants.FIELD_TYPE_NORMAL.equals(songAttribute.getType())) {
                sortKey = songAttribute.getFieldName() + sortKey;
                sortMap = SortBuilders.fieldSort(sortKey);
            } else if (Constants.FIELD_TYPE_NESTED.equals(songAttribute.getType())) {
                sortKey = songAttribute.getNestedTerm() + sortKey;
                sortMap = SortBuilders.fieldSort(sortKey);
                sortMap.setNestedPath(songAttribute.getNestedLevel());
            }
            sortMap.order((SortOrder) entry.getValue());
            sortMapList.add(sortMap);
        }

        return elasticService.executeElasticRequest("song", "song", query, false, filterRequest.getOffset(), filterRequest.getCount(), sortMapList, SongSnippet.class);
    }

    @Override
    public List<SongSnippet> getSongsWithFilter(FilterRequest filterRequest)
    {
        return getSongElasticResponse(filterRequest).getDocList();
    }

    @Override
    public SongSnippet getSongById(String songId) throws MyException
    {
        SongSnippet songSnippet;
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setFilters(Collections.singletonMap("id", Collections.singletonList(songId)));
        List<SongSnippet> songList = getSongsWithFilter(filterRequest);
        if(songList.isEmpty())
        {
           throw new NotFoundException(ValidationResponse.SONG_NOT_FOUND);
        }
        else
        {
            songSnippet = songList.get(0);
        }
        return songSnippet;
    }

    @Override
    public Long getSongCountWithFilter(FilterRequest filterRequest)
    {
        return getSongElasticResponse(filterRequest).getTotalCount();
    }


}
