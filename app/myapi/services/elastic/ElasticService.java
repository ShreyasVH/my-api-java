package myapi.services.elastic;

import myapi.constants.Constants;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import play.Logger;
import play.Play;

import java.net.InetSocketAddress;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class ElasticService extends ElasticSearch
{
    private final Logger.ALogger LOGGER = Logger.of(ElasticService.class);

    public ElasticService()
    {
        Settings settings = Settings.builder().put("cluster.name", System.getenv("ELASTIC_CLUSTER")).build();
        client = new PreBuiltTransportClient(settings);
        try
        {
            client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(System.getenv("ELASTIC_IP"), Integer.parseInt(System.getenv("ELASTIC_PORT")))));
        }
        catch (Exception ex)
        {
            LOGGER.error("Error while connecting to elastic client", ex);
        }
    }
}
