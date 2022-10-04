package services.impl;

import enums.ErrorCode;
import exceptions.InternalServerError;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import play.libs.Json;
import responses.ElasticResponse;
import responses.FilterResponse;
import services.ElasticService;
import utils.Utils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticServiceImpl implements ElasticService
{
    private static RestHighLevelClient client = null;

    @Inject
    public ElasticServiceImpl()
    {
        this.initializeClient();
    }

    public void initializeClient()
    {
        try
        {
            RestClientBuilder builder = RestClient.builder(new HttpHost(System.getenv("ELASTIC_IP_HTTP"), Integer.parseInt(System.getenv("ELASTIC_PORT_HTTP")), System.getenv("ELASTIC_SCHEME")));


            if(1 == Integer.parseInt(System.getenv("ELASTIC_USE_CREDENTIALS")))
            {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(System.getenv("ELASTIC_USERNAME"), System.getenv("ELASTIC_PASSWORD")));

                builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            }

            client = new RestHighLevelClient(builder);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
    }

    public <T> FilterResponse<T> search(SearchRequest request, Class<T> documentClass)
    {
        FilterResponse<T> elasticResponse = new FilterResponse<>();

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

            elasticResponse.setList(documents);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return elasticResponse;
    }

    public boolean index(String index, Long id, Object document)
    {
        boolean isSuccess = false;
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.id(id.toString());
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

    @Override
    public <T> T get(String index, Long id, Class<T> documentClass)
    {
        try
        {
            GetRequest request = new GetRequest(index, id.toString());
            GetResponse response = client.get(request, RequestOptions.DEFAULT);

            return Utils.convertObject(response.getSource(), documentClass);
        }
        catch (IOException ex)
        {
            throw new InternalServerError(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
        }
    }
}
