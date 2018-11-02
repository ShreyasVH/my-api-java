package myapi.dao;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.google.inject.Inject;
import myapi.models.Language;
import org.reflections.Reflections;

import javax.persistence.Entity;
import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class BaseDao
{
    protected final EbeanServer db;

    @Inject
    public BaseDao()
    {
        String ip = System.getenv("MYSQL_IP");
        String userName = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        ServerConfig config = new ServerConfig();
        config.setName("default");

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver("com.mysql.jdbc.Driver");
        dataSourceConfig.setUrl("jdbc:mysql://" + ip + "/maindb");
        dataSourceConfig.setUsername(userName);
        dataSourceConfig.setPassword(password);

        config.setDataSourceConfig(dataSourceConfig);

        config.setDefaultServer(true);
        config.setRegister(true);

        Reflections reflections = new Reflections("myapi.models");
        Set<Class<? extends Object>> allClasses = reflections.getTypesAnnotatedWith(Entity.class);

        for(Class modelClass : allClasses)
        {
            config.addClass(modelClass);
        }

        db = EbeanServerFactory.create(config);
    }
}
