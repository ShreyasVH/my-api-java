package myapi.dao;

import com.google.inject.Inject;
import myapi.models.MovieFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class FormatDao extends BaseDao
{
    @Inject
    public FormatDao()
    {
        super();
    }

    public List<MovieFormat> getAllFormats()
    {
        List<MovieFormat> formats = new ArrayList<>();
        try
        {
            formats = db.find(MovieFormat.class).orderBy("id").findList();
        }
        catch(Exception ex)
        {

        }
        return formats;
    }

    public MovieFormat getFormatById(Long formatId)
    {
       MovieFormat format = null;
       try
       {
           format = db.find(MovieFormat.class).where().eq("id", formatId).findUnique();
       }
       catch(Exception ex)
       {

       }
       return format;
    }

    public MovieFormat getFormatByName(String name)
    {
        MovieFormat format = null;
        try
        {
            format = db.find(MovieFormat.class).where().eq("name", name).findUnique();
        }
        catch(Exception ex)
        {

        }
        return format;
    }
}
