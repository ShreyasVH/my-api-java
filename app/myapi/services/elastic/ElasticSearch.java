package myapi.services.elastic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import myapi.constants.Constants;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.sort.SortBuilder;
import play.libs.Json;
import myapi.skeletons.responses.ElasticResponse;
import myapi.utils.Utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/5/17.
 */
public abstract class ElasticSearch
{
    protected TransportClient client;

    public ElasticResponse executeElasticRequest(String index, String type, QueryBuilder query, boolean isExplainRequired, Integer offset, Integer count, List<SortBuilder> sortMapList, Class toClass)
    {
        ElasticResponse finalResponse = new ElasticResponse();

        if(null == count)
        {
            count = Constants.DEFAULT_ELASTIC_COUNT;
        }

        if(null == offset)
        {
            offset = Constants.DEFAULT_ELASTIC_OFFSET;
        }
        SearchRequestBuilder request = client.prepareSearch(index).setTypes(type).setFrom(offset).setSize(count).setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(query).setExplain(isExplainRequired);
        for(SortBuilder sortMap : sortMapList)
        {
            request.addSort(sortMap);
        }
        SearchResponse response = request.execute().actionGet();

        JsonNode resultNode = Json.parse(response.toString());

        // Add found docs to result.
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode docList = new ArrayNode(factory);

        JsonNode hits = resultNode.findPath("hits");
        JsonNode resultSet = hits.findPath("hits");
        Integer totalCount = hits.findPath("total").intValue();
        Iterator<JsonNode> hitsIterator = resultSet.iterator();
        while (hitsIterator.hasNext())
        {
            JsonNode currDoc = hitsIterator.next();
            JsonNode id = currDoc.findPath("_id");
            if (!currDoc.findPath("_source").isMissingNode()) {
                JsonNode sourceJsonNode = currDoc.findPath("_source");
                ObjectNode objectNode = (ObjectNode) getFieldsFromJson(sourceJsonNode);
                objectNode.put("id", id.asText());
//                if(!currDoc.findPath(Constants.ELASTIC_SORT_TEXT).isMissingNode()){
//                    JsonNode sortJsonArray = currDoc.findPath(Constants.ELASTIC_SORT_TEXT);
//                    Iterator<JsonNode> sortIterator = sortJsonArray.iterator();
//                    while(sortIterator.hasNext()){
//                        JsonNode currSort = sortIterator.next();
//                        objectNode.put(Constants.ELASTIC_SORT_TEXT, currSort.asText());
//                    }
//                }
                docList.add(objectNode);
            }
        }

        List documentList = Utils.convertObjectList(docList, toClass);

        finalResponse.setDocList(documentList);
        Long documentCount = Long.valueOf(totalCount);
        finalResponse.setTotalCount(documentCount);

        return finalResponse;
    }

    private static JsonNode getFieldsFromJson(JsonNode doc) {
        ObjectNode item = Json.newObject();
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = doc.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            if (field.getValue().size() < 1 && !field.getValue().isArray()) {
                item.put(field.getKey(), getFirstElement(field.getValue()));
            } else {
                item.set(field.getKey(), field.getValue());
            }
        }
        return item;
    }

    private static String getFirstElement(JsonNode node) {
        if (node.size() == 0) {
            return node.asText();
        }
        Iterator<JsonNode> it = node.iterator();
        while (it.hasNext()) {
            return it.next().toString();
        }
        return null;
    }

    public Boolean create(String index, String type, String id, String document)
    {
        IndexResponse response = client.prepareIndex(index, type, id).setSource(document, XContentType.JSON).execute().actionGet();
        return response.status().equals(RestStatus.CREATED);
    }

    public Boolean update(String index, String type, String id, String document)
    {
        UpdateResponse response = client.prepareUpdate(index, type, id).setDoc(document, XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().actionGet();
        return response.status().equals(RestStatus.OK);
    }
}
