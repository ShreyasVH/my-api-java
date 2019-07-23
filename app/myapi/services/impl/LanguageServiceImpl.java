package myapi.services.impl;

import com.google.inject.Inject;
import myapi.dao.LanguageDao;
import myapi.exceptions.NotFoundException;
import myapi.models.Language;
import myapi.models.ValidationResponse;
import myapi.services.RedisService;
import myapi.services.LanguageService;
import myapi.utils.Logger;
import myapi.utils.Utils;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class LanguageServiceImpl implements LanguageService
{
    private final RedisService redisService;
    private final LanguageDao languageDao;

    @Inject
    public LanguageServiceImpl(RedisService redisService, LanguageDao languageDao)
    {
        this.redisService = redisService;
        this.languageDao = languageDao;
    }

    @Override
    public List<Language> getAllLanguages()
    {
        List<Language> languages = new ArrayList<>();
        if(redisService.getIsConnected())
        {
            try
            {
                Object languageList = redisService.get("api::languages");
                if((null == languageList) || !(languageList instanceof List))
                {
                    languages = languageDao.getAllLanguages();
                    if(!languages.isEmpty())
                    {
                        redisService.set("api::languages", languages);
                    }
                }
                else
                {
                    languages = (List<Language>) languageList;
                }
            }
            catch (Exception ex)
            {
                Logger.error("[getAllLanguages]: Error while fetching languages from cache. Message: " + ex.getMessage() + ". Cause: " + ex.getCause() + ". Trace: " + Json.toJson(ex.getStackTrace()));
                languages = languageDao.getAllLanguages();
            }
        }
        else
        {
            languages = languageDao.getAllLanguages();
        }
        return languages;
    }

    @Override
    public Language getLanguageById(Long languageId) throws NotFoundException
    {
        Language language = null;
        if(redisService.getIsConnected())
        {
            for(Language lang : getAllLanguages())
            {
                if(lang.getId().equals(languageId))
                {
                    language = lang;
                    break;
                }
            }
        }
        else
        {
            language = languageDao.getLanguageById(languageId);
        }

        if(null == language)
        {
            throw new NotFoundException(ValidationResponse.LANGUAGE_NOT_FOUND);
        }

        return language;
    }

    @Override
    public Language getLanguageByName(String name) throws NotFoundException
    {
        name = Utils.ucfirst(name);
        Language language = null;
        if(redisService.getIsConnected())
        {
            for(Language lang : getAllLanguages())
            {
                if(lang.getName().equals(name))
                {
                    language = lang;
                    break;
                }
            }
        }
        else
        {
            language = languageDao.getLanguageByName(name);
        }

        if(null == language)
        {
            throw new NotFoundException(ValidationResponse.LANGUAGE_NOT_FOUND);
        }

        return language;
    }
}
