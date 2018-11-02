package myapi.services.elastic;

import myapi.constants.Constants;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import play.Play;

import java.net.InetSocketAddress;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class ElasticService extends ElasticSearch
{
    public ElasticService()
    {
        client = new PreBuiltTransportClient(Settings.EMPTY);
        try
        {
            client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(System.getenv("ELASTIC_IP"), Integer.parseInt(System.getenv("ELASTIC_PORT")))));
        } catch (Exception ex)
        {

        }
    }
}
