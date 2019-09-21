package myapi.services.elastic;

import java.util.ArrayList;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import play.libs.Json;
import java.util.List;
import java.util.Map;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;

import myapi.skeletons.responses.ElasticResponse;
import myapi.utils.Utils;
/**
 * Created by shreyas.hande on 12/5/17.
 */
public abstract class ElasticSearch
{
    static RestHighLevelClient client = null;

    public <T> ElasticResponse<T> search(SearchRequest request, Class<T> documentClass)
    {
        ElasticResponse<T> elasticResponse = new ElasticResponse<>();

        try
        {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            elasticResponse.setTotalCount(hits.getTotalHits().value);
            SearchHit[] searchHits = hits.getHits();
            List<T> documents = new ArrayList<>();
            for(SearchHit searchHit: searchHits)
            {
                Map<String, Object> document = searchHit.getSourceAsMap();
                T formattedDocument = Utils.convertObject(document, documentClass);
                documents.add(formattedDocument);
            }

            elasticResponse.setDocList(documents);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return elasticResponse;
    }

    public boolean index(String index, String id, Object document)
    {
        boolean isSuccess = false;
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.id(id);
        indexRequest.source(Json.toJson(document).toString(), XContentType.JSON);
        try
        {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            Result result = indexResponse.getResult();
            isSuccess = ((Result.CREATED.getOp() == result.getOp()) || (Result.UPDATED.getOp() == result.getOp()));
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        return isSuccess;
    }
}
