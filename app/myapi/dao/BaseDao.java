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
    protected static EbeanServer db;

    @Inject
    public BaseDao()
    {
        if(null == db)
        {
            db = Ebean.getServer("default");
        }
    }
}
