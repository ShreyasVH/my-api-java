package myapi.dao;

import com.google.inject.Inject;
import myapi.models.Language;
import myapi.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class LanguageDao extends BaseDao
{
    @Inject
    public LanguageDao()
    {
        super();
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
            Logger.error("Error while getting all languages. Exception: " + ex);
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
            Logger.error("Error while getting language by id. id: " + languageId + ". Exception: " + ex);
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
            Logger.error("Error while getting language by name. name: " + name + ". Exception: " + ex);
        }
        return language;
    }
}
