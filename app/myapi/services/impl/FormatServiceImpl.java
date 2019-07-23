package myapi.services.impl;

import com.google.inject.Inject;
import myapi.dao.FormatDao;
import myapi.exceptions.NotFoundException;
import myapi.models.MovieFormat;
import myapi.models.ValidationResponse;
import myapi.services.FormatService;
import myapi.services.RedisService;
import myapi.utils.Logger;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class FormatServiceImpl implements FormatService
{
    private  final RedisService redisService;
    private final FormatDao formatDao;


    @Inject
    public FormatServiceImpl(RedisService redisService, FormatDao formatDao)
    {
        this.redisService = redisService;
        this.formatDao = formatDao;
    }


    @Override
    public List<MovieFormat> getAllFormats()
    {
        List<MovieFormat> formats = new ArrayList<>();
        if(redisService.getIsConnected())
        {
            try
            {
                Object formatList = redisService.get("api::formats");
                if((null == formatList) || !(formatList instanceof List))
                {
                    formats = formatDao.getAllFormats();
                    if(!formats.isEmpty())
                    {
                        redisService.set("api::formats", formats);
                    }
                }
                else
                {
                    formats = (List<MovieFormat>) formatList;
                }
            }
            catch(Exception ex)
            {
                Logger.error("[getAllFormats]: Error while fetching formats from cache. Message: " + ex.getMessage() + ". Cause: " + ex.getCause() + ". Trace: " + Json.toJson(ex.getStackTrace()));
                formats = formatDao.getAllFormats();
            }
        }
        else
        {
            formats = formatDao.getAllFormats();
        }
        return  formats;
    }

    @Override
    public MovieFormat getFormatById(Long formatId) throws NotFoundException
    {
        MovieFormat format = null;
        if(redisService.getIsConnected())
        {
            for(MovieFormat f : getAllFormats())
            {
                if(f.getId().equals(formatId))
                {
                    format = f;
                    break;
                }
            }
        }
        else
        {
            format = formatDao.getFormatById(formatId);
        }

        if(null == format)
        {
            throw new NotFoundException(ValidationResponse.FORMAT_NOT_FOUND);
        }

        return format;
    }

    @Override
    public MovieFormat getFormatByName(String name) throws NotFoundException
    {
        MovieFormat format = null;
        if(redisService.getIsConnected())
        {
            for(MovieFormat f : getAllFormats())
            {
                if(f.getName().equals(name))
                {
                    format = f;
                    break;
                }
            }
        }
        else
        {
            format = formatDao.getFormatByName(name);
        }

        if(null == format)
        {
            throw new NotFoundException(ValidationResponse.FORMAT_NOT_FOUND);
        }

        return format;
    }
}
