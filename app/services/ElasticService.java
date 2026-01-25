package services;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import responses.ElasticResponse;
import responses.FilterResponse;

import java.io.IOException;

public interface ElasticService
{
    <T> FilterResponse<T> search(SearchRequest request, Class<T> documentClass);

    boolean index(String index, Long id, Object document);

    <T> T get(String index, Long id, Class<T> documentClass);
}
