package myapi.services.impl;

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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import myapi.utils.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import play.libs.Json;

/**
 * Created by shreyas.hande on 1/7/18.
 */
public class SongIndexServiceImpl implements SongIndexService
{
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
        Long counter = 1L;
        for(SongSnippet songSnippet : songs)
        {
            Boolean isSuccess = indexSong(songSnippet);
            if(!isSuccess)
            {
                Logger.error("Failed to index movie. Id : " + songSnippet.id);
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
        return elasticService.index("songs", songSnippet.getId(), songSnippet);
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
                Logger.error("Error while indexing song. Payload: " + Json.toJson(songSnippet) + ". Exception: " + ex);
            }
        });
    }

    @Override
    public ElasticResponse getSongElasticResponse(FilterRequest filterRequest)
    {
        SearchRequest request = new SearchRequest("songs");

        Map<String, List<String>> filters = filterRequest.getFilters();

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(filterRequest.getOffset());
        builder.size(filterRequest.getCount());

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        if(!filters.isEmpty())
        {
            for(Map.Entry<String, List<String>> entry: filters.entrySet())
            {
                String key = entry.getKey();
                List<String> valueList = entry.getValue();
                if(!valueList.isEmpty())
                {
                    SongAttribute songAttribute = SongAttribute.getSongAttributeByName(key);
                    if(null != songAttribute)
                    {
                        if(Constants.FIELD_TYPE_NORMAL.equals(songAttribute.getType()))
                        {
                            query.must(QueryBuilders.termsQuery(key, valueList));
                        }
                        else if(Constants.FIELD_TYPE_NESTED.equals(songAttribute.getType()))
                        {
                            QueryBuilder nestedQuery = QueryBuilders.nestedQuery(songAttribute.getNestedLevel(), QueryBuilders.termsQuery(songAttribute.getNestedTerm(), (List) entry.getValue()),  ScoreMode.None);
                            query.must(nestedQuery);
                        }
                        else if(Constants.FIELD_TYPE_RANGE.equals(songAttribute.getType()))
                        {

                            if(valueList.size() == 1)
                            {
                                valueList.add(valueList.get(0));
                            }

                            Collections.sort(valueList);
                            query.must(QueryBuilders.rangeQuery(songAttribute.getFieldName()).from(valueList.get(0), true).to(valueList.get(1), true));
                        }
                    }
                }
            }
        }
        builder.query(query);

        Map<String, SortOrder> sortMap = filterRequest.getSortMap();
        for(Map.Entry<String, SortOrder> sortField: sortMap.entrySet())
        {
            String key = sortField.getKey();
            SortOrder order = sortField.getValue();

            SongAttribute songAttribute = SongAttribute.getSongAttributeByName(key);
            if(null != songAttribute)
            {
                String sortKey = (key + ".sort");
                builder.sort(sortKey, order);
            }
        }

        if(!sortMap.containsKey("name"))
        {
            builder.sort("name.sort", SortOrder.ASC);
        }

        request.source(builder);
        return elasticService.search(request, SongSnippet.class);
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
