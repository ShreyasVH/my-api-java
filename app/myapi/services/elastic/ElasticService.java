package myapi.services.elastic;

import play.Logger;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class ElasticService extends ElasticSearch
{
    private final Logger.ALogger LOGGER = Logger.of(ElasticService.class);

    public ElasticService()
    {
        if(null == client)
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
    }
}
