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
        return languageDao.getAllLanguages();
    }

    @Override
    public Language getLanguageById(Long languageId) throws NotFoundException
    {
        Language language = this.languageDao.getLanguageById(languageId);

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
        Language language = this.languageDao.getLanguageByName(name);

        if(null == language)
        {
            throw new NotFoundException(ValidationResponse.LANGUAGE_NOT_FOUND);
        }

        return language;
    }
}
