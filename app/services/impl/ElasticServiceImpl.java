package services.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.InternalServerError;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import play.libs.Json;
import responses.ElasticResponse;
import responses.FilterResponse;
import services.ElasticService;
import utils.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElasticServiceImpl implements ElasticService
{
    private final ElasticsearchClient client;

    @Inject
    public ElasticServiceImpl() throws URISyntaxException {
        client = this.getClient();
    }

//    private ElasticsearchClient getClient() {
//        String serverUrl = "https://" + System.getenv("ELASTIC_IP_HTTP") + ":" + System.getenv("ELASTIC_PORT_HTTP");
//        String apiKey = System.getenv("ELASTIC_API_KEY");
//
//        return ElasticsearchClient.of(b -> b
//                .host(serverUrl)
//                .apiKey(apiKey)
//        );
//    }

    private ElasticsearchClient getClient() throws URISyntaxException {
        String serverUrl = "https://" + System.getenv("ELASTIC_IP_HTTP") + ":" + System.getenv("ELASTIC_PORT_HTTP");
        String apiKey = System.getenv("ELASTIC_API_KEY");

        Rest5Client restClient = Rest5Client
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new org.apache.hc.core5.http.Header[] {
                        new org.apache.hc.core5.http.message.BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setResponseTimeout(Timeout.ofSeconds(60))
                        .setConnectionRequestTimeout(Timeout.ofSeconds(10))
                )
                .setConnectionConfigCallback(connectionConfigBuilder -> connectionConfigBuilder
                        .setConnectTimeout(Timeout.ofSeconds(10))
                        .setSocketTimeout(Timeout.ofSeconds(60))
                )
                .build();

        Rest5ClientTransport transport = new Rest5ClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
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
            ex.printStackTrace();
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
