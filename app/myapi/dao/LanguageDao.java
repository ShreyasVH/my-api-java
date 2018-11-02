package myapi.dao;

import com.google.inject.Inject;
import myapi.models.Language;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class LanguageDao extends BaseDao
{
    private final Logger.ALogger LOGGER = Logger.of(LanguageDao.class);

    @Inject
    public LanguageDao()
    {

    }

    public List<Language> getAllLanguages()
    {
        List<Language> languages = new ArrayList<>();
        try
        {
            languages = db.find(Language.class).orderBy("id").findList();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getAllLanguages] Error while fetching all languages", ex);
        }
        return languages;
    }

    public Language getLanguageById(Long languageId)
    {
        Language language = null;
        try
        {
            language = db.find(Language.class).where().eq("id", languageId).findUnique();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getLanguageById] Error while fetching language by id", ex);
        }
        return language;
    }

    public Language getLanguageByName(String name)
    {
        Language language = null;
        try
        {
            language = db.find(Language.class).where().eq("name", name).findUnique();
        }
        catch(Exception ex)
        {
            LOGGER.error("[getLanguageByName] Error while fetching language by name", ex);
        }
        return language;
    }
}
