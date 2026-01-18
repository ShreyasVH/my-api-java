package services.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.InternalServerError;
import play.libs.Json;
import responses.ElasticResponse;
import responses.FilterResponse;
import services.ElasticService;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElasticServiceImpl implements ElasticService
{
    private final ElasticsearchClient client;

    @Inject
    public ElasticServiceImpl() {
        client = this.getClient();
    }

    private ElasticsearchClient getClient() {
        String serverUrl = "https://" + System.getenv("ELASTIC_IP_HTTP") + ":" + System.getenv("ELASTIC_PORT_HTTP");
        String apiKey = System.getenv("ELASTIC_API_KEY");

        return ElasticsearchClient.of(b -> b
                .host(serverUrl)
                .apiKey(apiKey)
        );
    }

    public <T> FilterResponse<T> search(SearchRequest request, Class<T> documentClass)
    {
        FilterResponse<T> elasticResponse = new FilterResponse<>();

        try {
            SearchResponse<T> searchResponse = client.search(request, documentClass);
            List<T> documentList = searchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
            long totalCount = searchResponse.hits().total().value();
            elasticResponse.setTotalCount(totalCount);
            elasticResponse.setList(documentList);
            String sh = "sh";
        } catch (Exception ex) {
            String sh = "sh";
            ex.printStackTrace();
        }

        return elasticResponse;
    }

    public boolean index(String index, Long id, Object document)
    {
        try {
            IndexResponse response = client.index(i -> i.index(index).id(String.valueOf(id)).document(document));
            return response.result().equals(Result.Created);
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public <T> T get(String index, Long id, Class<T> documentClass)
    {
        GetRequest getRequest = new GetRequest
                .Builder()
                .index(index)
                .id(String.valueOf(id))
                .build();

        try {
            GetResponse<T> response = client.get(getRequest, documentClass);
            return response.source();
        } catch (IOException ex) {
            return null;
        }
    }
}
