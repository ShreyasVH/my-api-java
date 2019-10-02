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
        return formatDao.getAllFormats();
    }

    @Override
    public MovieFormat getFormatById(Long formatId) throws NotFoundException
    {
        MovieFormat format = null;
        for(MovieFormat f : getAllFormats())
        {
            if(f.getId().equals(formatId))
            {
                format = f;
                break;
            }
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
        for(MovieFormat f : getAllFormats())
        {
            if(f.getName().equals(name))
            {
                format = f;
                break;
            }
        }

        if(null == format)
        {
            throw new NotFoundException(ValidationResponse.FORMAT_NOT_FOUND);
        }

        return format;
    }
}
